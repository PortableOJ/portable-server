package com.portable.server.support.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.*;
import com.portable.server.model.ServiceVerifyCode;
import com.portable.server.model.judge.entity.JudgeContainer;
import com.portable.server.model.judge.work.AbstractJudgeWork;
import com.portable.server.model.judge.work.SolutionJudgeWork;
import com.portable.server.model.judge.work.TestJudgeWork;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.model.response.judge.TestInfoResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.support.JudgeSupport;
import com.portable.server.socket.EpollManager;
import com.portable.server.support.FileSupport;
import com.portable.server.type.AccountType;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.JudgeWorkType;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class JudgeSupportImpl implements JudgeSupport {

    /**
     * judge 的保持存活时间
     */
    private static final Integer JUDGE_KEEP_ALIVE_TIME = 30;

    /**
     * tcp 连接映射到 judge 容器
     */
    private Map<String, JudgeContainer> tcpJudgeMap;

    /**
     * judge code 映射 judge 容器
     */
    private Map<String, JudgeContainer> judgeCodeJudgeMap;

    /**
     * solution ID 映射 judge 任务
     */
    private Map<Long, SolutionJudgeWork> solutionJudgeWorkMap;

    /**
     * problem ID 映射 judge 任务
     */
    private Map<Long, TestJudgeWork> problemTestJudgeWorkMap;

    /**
     * 所有未进行的任务
     */
    private Queue<AbstractJudgeWork> judgeWorkPriorityQueue;

    /**
     * 若 service Code 非 null，则生成一个此 code
     */
    private ServiceVerifyCode serviceVerifyCode;

    @Resource
    private Environment env;

    @Resource
    private UserManager userManager;

    @Resource
    private NormalUserManager normalUserManager;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private SolutionDataManager solutionDataManager;

    @Resource
    private ProblemManager problemManager;

    @Resource
    private ProblemDataManager problemDataManager;

    @Resource
    private TemporaryDataManager temporaryDataManager;

    @Resource
    private FileSupport fileSupport;

    @PostConstruct
    public void init() {
        tcpJudgeMap = new ConcurrentHashMap<>(4);
        judgeCodeJudgeMap = new ConcurrentHashMap<>(2);
        solutionJudgeWorkMap = new ConcurrentHashMap<>(64);
        problemTestJudgeWorkMap = new ConcurrentHashMap<>(4);
        judgeWorkPriorityQueue = new PriorityBlockingQueue<>();

        String serviceCode = env.getProperty("SERVICE_CODE");
        if (serviceCode != null) {
            serviceVerifyCode = ServiceVerifyCode.builder().code(serviceCode).temporary(false).endTime(null).build();
        } else {
            serviceVerifyCode = null;
        }
    }

    @Override
    public void addJudgeTask(Long solutionId) throws PortableException {
        Solution solution = solutionManager.selectSolutionById(solutionId);
        if (solution == null) {
            throw PortableException.of("S-06-001", solutionId);
        }
        ProblemData problemData = getProblemData(solution.getProblemId());
        SolutionJudgeWork solutionJudgeWork = new SolutionJudgeWork(solution.getSolutionType());
        solutionJudgeWork.setSolutionId(solutionId);
        solutionJudgeWork.setProblemId(solution.getProblemId());
        solutionJudgeWork.setCurTestId(null);
        solutionJudgeWork.setMaxTest(problemData.getTestName().size());
        solutionJudgeWorkMap.put(solutionId, solutionJudgeWork);
        judgeWorkPriorityQueue.add(solutionJudgeWork);
        solutionManager.updateStatus(solutionId, SolutionStatusType.PENDING);
    }

    @Override
    public void addTestTask(Long problemId) throws PortableException {
        ProblemData problemData = getTestProblemData(getTestProblem(problemId));
        TestJudgeWork testJudgeWork = new TestJudgeWork();
        testJudgeWork.setProblemId(problemId);
        testJudgeWork.setCurTestId(0);
        testJudgeWork.setMaxTest(problemData.getTestName().size());
        problemTestJudgeWorkMap.put(problemId, testJudgeWork);
        judgeWorkPriorityQueue.add(testJudgeWork);
        problemManager.updateProblemStatus(problemId, ProblemStatusType.PENDING);
    }

    @Override
    public void killJudgeTask(Long solutionId, SolutionStatusType endType, Integer timeCost, Integer memoryCost) {
        SolutionJudgeWork solutionJudgeWork = solutionJudgeWorkMap.get(solutionId);
        solutionJudgeWork.getJudgeContainer().getJudgeWorkMap().remove(solutionId);
        solutionJudgeWorkMap.remove(solutionId);
        solutionManager.updateCostAndStatus(solutionId, endType, timeCost, memoryCost);
        Solution solution = solutionManager.selectSolutionById(solutionId);

        if (!SolutionStatusType.ACCEPT.equals(endType)) {
            return;
        }
        // 更新统计数量
        switch (solution.getSolutionType()) {
            case CONTEST:
                // contest 还需要单独加入到 contest 的统计列表中
            case PUBLIC:
                problemManager.updateProblemCount(solutionJudgeWork.getProblemId(), 0, 1);

                User user = userManager.getAccountById(solution.getUserId());
                if (user != null && AccountType.NORMAL.equals(user.getType())) {
                    NormalUserData normalUserData = normalUserManager.getUserDataById(user.getDataId());
                    normalUserData.setAccept(normalUserData.getAccept() + 1);
                    normalUserManager.updateNormalUserData(normalUserData);
                }
                break;
            case PROBLEM_PROCESS:
            default:
                break;
        }
    }

    @Override
    public void killTestTask(Long problemId, Boolean endType) {
        TestJudgeWork testJudgeWork = problemTestJudgeWorkMap.get(problemId);
        if (testJudgeWork == null) {
            return;
        }
        testJudgeWork.getJudgeContainer().getTestWorkMap().remove(problemId);
        problemTestJudgeWorkMap.remove(problemId);
        problemManager.updateProblemStatus(problemId, endType ? ProblemStatusType.CHECKING : ProblemStatusType.TREAT_FAILED);
    }

    @Override
    public ServiceVerifyCode getServiceCode() {
        return serviceVerifyCode != null ? serviceVerifyCode : temporaryDataManager.getServiceCode();
    }

    @Override
    public String registerJudge(String serverCode, Integer maxThreadCore, Integer maxWorkCore, Integer maxSocketCore) throws PortableException {
        cleanJudgeContainer();
        if (!Objects.equals(serverCode, getServiceCode().getCode())) {
            throw PortableException.of("S-06-002", serverCode);
        }
        String judgeCode = UUID.randomUUID().toString();
        String address = EpollManager.getAddress();
        Set<String> tcpAddressSet = Collections.synchronizedSet(new HashSet<>());
        JudgeContainer judgeContainer = JudgeContainer.builder()
                .judgeCode(judgeCode)
                .maxThreadCore(maxThreadCore)
                .maxWorkCore(maxWorkCore)
                .maxSocketCore(maxSocketCore)
                .threadAccumulation(0)
                .workAccumulation(0)
                .socketAccumulation(0)
                .isNewCore(false)
                .maxWorkNum(maxWorkCore)
                .lastHeartbeat(new Date())
                .judgeWorkMap(new ConcurrentHashMap<>(1))
                .testWorkMap(new ConcurrentHashMap<>(1))
                .tcpAddressSet(tcpAddressSet)
                .build();

        tcpJudgeMap.put(address, judgeContainer);
        judgeContainer.getTcpAddressSet().add(address);
        judgeCodeJudgeMap.put(judgeCode, judgeContainer);

        return judgeCode;
    }

    @Override
    public void append(String judgeCode) throws PortableException {
        if (!judgeCodeJudgeMap.containsKey(judgeCode)) {
            throw PortableException.of("S-06-003", judgeCode);
        }
        JudgeContainer judgeContainer = judgeCodeJudgeMap.get(judgeCode);
        String address = EpollManager.getAddress();
        tcpJudgeMap.put(address, judgeContainer);
        judgeContainer.getTcpAddressSet().add(address);
    }

    @Override
    public void close() {
        String address = EpollManager.getAddress();
        JudgeContainer judgeContainer = tcpJudgeMap.get(address);
        if (judgeContainer != null) {
            judgeContainer.getTcpAddressSet().remove(address);
        }
        tcpJudgeMap.remove(address);
    }

    @Override
    public HeartbeatResponse heartbeat(Integer threadAccumulation, Integer workAccumulation, Integer socketAccumulation) throws PortableException {
        JudgeContainer judgeContainer = getCurContainer();
        judgeContainer.setThreadAccumulation(threadAccumulation);
        judgeContainer.setWorkAccumulation(workAccumulation);
        judgeContainer.setSocketAccumulation(socketAccumulation);

        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();
        if (judgeContainer.getIsNewCore()) {
            judgeContainer.setIsNewCore(false);
            heartbeatResponse.setNewThreadCore(judgeContainer.getMaxThreadCore());
            heartbeatResponse.setNewWorkCore(judgeContainer.getMaxWorkCore());
            heartbeatResponse.setNewSocketCore(judgeContainer.getMaxSocketCore());
        }
        int newWork = judgeContainer.getMaxWorkNum() - judgeContainer.getJudgeWorkMap().size() - judgeContainer.getTestWorkMap().size();
        for (int i = 0; i < newWork; i++) {
            AbstractJudgeWork judgeWork = judgeWorkPriorityQueue.poll();
            if (judgeWork == null) {
                break;
            }
            judgeWork.setJudgeContainer(judgeContainer);
            if (judgeWork instanceof SolutionJudgeWork) {
                SolutionJudgeWork solutionJudgeWork = (SolutionJudgeWork) judgeWork;

                // 虽然还在队列中，但是已经被删除 judge 了
                if (!solutionJudgeWorkMap.containsKey(solutionJudgeWork.getSolutionId())) {
                    continue;
                }
                Long solutionId = solutionJudgeWork.getSolutionId();
                judgeContainer.getJudgeWorkMap().put(solutionId, solutionJudgeWork);
                heartbeatResponse.addJudgeTask(solutionId);
            } else if (judgeWork instanceof TestJudgeWork) {
                TestJudgeWork testJudgeWork = (TestJudgeWork) judgeWork;
                judgeContainer.getTestWorkMap().put(testJudgeWork.getProblemId(), testJudgeWork);
                heartbeatResponse.addTestTask(testJudgeWork.getProblemId());
            }
        }
        return heartbeatResponse;
    }

    @Override
    public SolutionInfoResponse getSolutionInfo(Long solutionId) throws PortableException {
        getCurContainer();
        SolutionInfoResponse solutionInfoResponse = new SolutionInfoResponse();
        Solution solution = solutionManager.selectSolutionById(solutionId);
        if (solution == null) {
            throw PortableException.of("S-06-001", solutionId);
        }
        solutionManager.updateStatus(solutionId, SolutionStatusType.COMPILING);
        ProblemData problemData = getProblemData(solution.getProblemId());

        LanguageType languageType = solution.getLanguageType();

        solutionInfoResponse.setProblemId(solution.getProblemId());
        solutionInfoResponse.setLanguage(languageType);
        solutionInfoResponse.setJudgeName(problemData.getJudgeCodeType());
        solutionInfoResponse.setTestNum(problemData.getTestName().size());
        solutionInfoResponse.setTimeLimit(problemData.getTimeLimit(languageType));
        solutionInfoResponse.setMemoryLimit(problemData.getMemoryLimit(languageType));

        return solutionInfoResponse;
    }

    @Override
    public String getSolutionCode(Long solutionId) throws PortableException {
        getCurContainer();
        SolutionData solutionData = getSolutionData(solutionId);
        return solutionData.getCode();
    }

    @Override
    public String getProblemJudgeCode(Long problemId) throws PortableException {
        getCurContainer();
        ProblemData problemData = getProblemData(problemId);
        return problemData.getJudgeCode();
    }

    @Override
    public void reportCompileResult(Long solutionId, Boolean compileResult, Boolean judgeCompileResult, String compileMsg) throws PortableException {
        getCurContainer();
        SolutionData solutionData = getSolutionData(solutionId);
        solutionData.setCompileMsg(compileMsg);
        solutionDataManager.saveSolutionData(solutionData);
        solutionJudgeWorkMap.get(solutionId).setCurTestId(0);
        if (!compileResult || !judgeCompileResult) {
            killJudgeTask(solutionId, compileResult ? SolutionStatusType.JUDGE_COMPILE_ERROR : SolutionStatusType.COMPILE_ERROR, 0, 0);
        } else {
            solutionManager.updateStatus(solutionId, SolutionStatusType.JUDGING);
        }
    }

    @Override
    public void reportRunningResult(Long solutionId, SolutionStatusType statusType, Integer timeCost, Integer memoryCost) throws PortableException {
        getCurContainer();
        SolutionJudgeWork solutionJudgeWork = solutionJudgeWorkMap.get(solutionId);
        if (!SolutionStatusType.ACCEPT.equals(statusType)) {
            killJudgeTask(solutionId, statusType, timeCost, memoryCost);
            checkProblemCheckOver(solutionJudgeWork);
            throw PortableException.of("S-06-008", solutionId);
        } else {
            if (solutionJudgeWork != null && solutionJudgeWork.testOver()) {
                killJudgeTask(solutionId, statusType, timeCost, memoryCost);
                checkProblemCheckOver(solutionJudgeWork);
            } else {
                solutionManager.updateCostAndStatus(solutionId, SolutionStatusType.JUDGING, timeCost, memoryCost);
            }
        }
    }

    @Override
    public String getStandardJudgeList() {
        return Arrays.stream(JudgeCodeType.values()).filter(judgeCodeType -> !JudgeCodeType.DIY.equals(judgeCodeType)).map(JudgeCodeType::toString).collect(Collectors.joining(" "));
    }

    @Override
    public File getStandardJudgeCode(String name) throws PortableException {
        try {
            JudgeCodeType judgeCodeType = JudgeCodeType.valueOf(name);
            return judgeCodeType.getCode();
        } catch (IllegalArgumentException e) {
            throw PortableException.of("S-06-007", name);
        }
    }

    @Override
    public File getTestLibCode() throws PortableException {
        return JudgeCodeType.getTestLib();
    }

    @Override
    public InputStream getProblemInputTest(Long problemId, String name) throws PortableException {
        getCurContainer();
        return fileSupport.getTestInput(problemId, name);
    }

    @Override
    public InputStream getProblemOutputTest(Long problemId, String name) throws PortableException {
        getCurContainer();
        return fileSupport.getTestOutput(problemId, name);
    }

    @Override
    public String getSolutionNextTestName(Long solutionId) throws PortableException {
        getCurContainer();
        SolutionJudgeWork solutionJudgeWork = solutionJudgeWorkMap.get(solutionId);
        if (solutionJudgeWork == null) {
            throw PortableException.of("S-06-008", solutionId);
        }
        ProblemData problemData = getProblemData(solutionJudgeWork.getProblemId());
        return problemData.getTestName().get(solutionJudgeWork.nextTest());
    }

    @Override
    public TestInfoResponse getTestInfo(Long problemId) throws PortableException {
        getCurContainer();
        TestInfoResponse testInfoResponse = new TestInfoResponse();
        ProblemData problemData = getTestProblemData(getTestProblem(problemId));
        problemManager.updateProblemStatus(problemId, ProblemStatusType.TREATING);

        LanguageType languageType = problemData.getStdCode().getLanguageType();
        testInfoResponse.setTestNum(problemData.getTestName().size());
        testInfoResponse.setLanguage(languageType);
        testInfoResponse.setTimeLimit(problemData.getTimeLimit(languageType));
        testInfoResponse.setMemoryLimit(problemData.getMemoryLimit(languageType));

        return testInfoResponse;
    }

    @Override
    public String getTestStdCode(Long problemId) throws PortableException {
        getCurContainer();

        ProblemData problemData = getTestProblemData(getTestProblem(problemId));

        return problemData.getStdCode().getCode();
    }

    @Override
    public String getNextTest(Long problemId) throws PortableException {
        getCurContainer();
        TestJudgeWork testJudgeWork = problemTestJudgeWorkMap.get(problemId);
        if (testJudgeWork == null) {
            throw PortableException.of("S-06-009", problemId);
        }
        ProblemData problemData = getTestProblemData(getTestProblem(problemId));
        return problemData.getTestName().get(testJudgeWork.nextTest());
    }

    @Override
    public void reportTestOutput(Long problemId, Boolean flag, String name, Integer pos, byte[] value) throws PortableException {
        if (flag) {
            if (pos == 0) {
                fileSupport.createTestOutput(problemId, name, value);
            } else {
                fileSupport.appendTestOutput(problemId, name, value);
            }
        } else {
            killTestTask(problemId, false);
            throw PortableException.of("S-06-009", problemId);
        }
    }

    @Override
    public void reportTestCompileFail(Long problemId) throws PortableException {
        killTestTask(problemId, false);
        throw PortableException.of("S-06-009", problemId);
    }

    @Override
    public void reportTestOver(Long problemId) throws PortableException {
        killTestTask(problemId, true);
        Problem problem = getTestProblem(problemId);
        ProblemData problemData = getTestProblemData(problem);
        // 检查是否有必要 check
        Set<LanguageType> testCodeUsed = problemData.getTestCodeList().stream()
                .map(ProblemData.StdCode::getLanguageType)
                .collect(Collectors.toSet());
        if (!problemData.getSupportLanguage().containsAll(testCodeUsed)) {
            problemManager.updateProblemStatus(problemId, ProblemStatusType.CHECK_FAILED);
            return;
        }

        for (ProblemData.StdCode stdCode : problemData.getTestCodeList()) {
            SolutionData solutionData = solutionDataManager.newSolutionData(problemData);
            solutionData.setCode(stdCode.getCode());
            solutionDataManager.insertSolutionData(solutionData);

            Solution solution = solutionManager.newSolution();
            solution.setDataId(solutionData.get_id());
            solution.setUserId(problem.getOwner());
            solution.setProblemId(problemId);
            solution.setLanguageType(stdCode.getLanguageType());
            solution.setSolutionType(SolutionType.PROBLEM_PROCESS);
            solutionManager.insertSolution(solution);
            addJudgeTask(solution.getId());
            stdCode.setSolutionId(solution.getId());
        }
        problemManager.updateProblemStatus(problemId, ProblemStatusType.CHECKING);
        problemDataManager.updateProblemData(problemData);
    }

    private JudgeContainer getCurContainer() throws PortableException {
        JudgeContainer judgeContainer = tcpJudgeMap.get(EpollManager.getAddress());
        if (judgeContainer == null) {
            throw PortableException.of("S-06-004", EpollManager.getAddress());
        }
        return judgeContainer;
    }

    private void cleanJudgeContainer() {
        Iterator<Map.Entry<String, JudgeContainer>> iterator = judgeCodeJudgeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JudgeContainer> entry = iterator.next();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -JUDGE_KEEP_ALIVE_TIME);
            if (!entry.getValue().getLastHeartbeat().before(calendar.getTime())) {
                continue;
            }
            iterator.remove();
            // 处理正在执行中的任务，将其转为失败
            JudgeContainer judgeContainer = entry.getValue();
            for (SolutionJudgeWork solutionJudgeWork : judgeContainer.getJudgeWorkMap().values()) {
                killJudgeTask(solutionJudgeWork.getSolutionId(), SolutionStatusType.SYSTEM_ERROR, 0, 0);
            }
            for (TestJudgeWork testJudgeWork : judgeContainer.getTestWorkMap().values()) {
                problemManager.updateProblemStatus(testJudgeWork.getProblemId(), ProblemStatusType.TREAT_FAILED);
            }
            for (String tcpAddress : judgeContainer.getTcpAddressSet()) {
                tcpJudgeMap.remove(tcpAddress);
            }
        }
    }

    private SolutionData getSolutionData(Long solutionId) throws PortableException {
        Solution solution = solutionManager.selectSolutionById(solutionId);
        if (solution == null) {
            throw PortableException.of("S-06-001", solutionId);
        }
        SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
        if (solutionData == null) {
            solutionManager.updateStatus(solutionId, SolutionStatusType.SYSTEM_ERROR);
            throw PortableException.of("S-05-001");
        }
        return solutionData;
    }

    private ProblemData getProblemData(Long problemId) throws PortableException {
        Problem problem = problemManager.getProblemById(problemId);
        if (problem == null) {
            throw PortableException.of("S-06-005", problemId);
        }
        if (!problem.getStatusType().getTreated()) {
            throw PortableException.of("S-06-006", problemId);
        }
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        if (problemData == null) {
            throw PortableException.of("S-03-001");
        }
        return problemData;
    }

    private Problem getTestProblem(Long problemId) throws PortableException {
        Problem problem = problemManager.getProblemById(problemId);
        if (problem == null) {
            throw PortableException.of("S-06-005", problemId);
        }
        return problem;
    }

    private ProblemData getTestProblemData(Problem problem) throws PortableException {
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        if (problemData == null) {
            throw PortableException.of("S-03-001");
        }
        return problemData;
    }

    private void checkProblemCheckOver(SolutionJudgeWork solutionJudgeWork) throws PortableException {
        if (JudgeWorkType.CHECK_PROBLEM.equals(solutionJudgeWork.getJudgeWorkType())) {
            synchronized (this) {
                ProblemData problemData = getProblemData(solutionJudgeWork.getProblemId());
                Set<Boolean> statusSet = problemData.getTestCodeList().stream()
                        .map(stdCode -> {
                            Solution solution = solutionManager.selectSolutionById(stdCode.getSolutionId());
                            if (solution == null) {
                                return false;
                            }
                            if (!solution.getStatus().getEndingResult()) {
                                return null;
                            }
                            return Objects.equals(solution.getStatus(), stdCode.getExpectResultType());
                        })
                        .collect(Collectors.toSet());
                if (!statusSet.contains(null)) {
                    if (statusSet.contains(false)) {
                        problemManager.updateProblemStatus(solutionJudgeWork.getProblemId(), ProblemStatusType.CHECK_FAILED);
                    } else {
                        problemManager.updateProblemStatus(solutionJudgeWork.getProblemId(), ProblemStatusType.NORMAL);
                    }
                }
            }
        }
    }
}