package com.portable.server.support.impl;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.helper.RedisValueHelper;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.judge.entity.JudgeContainer;
import com.portable.server.model.judge.entity.UpdateJudgeContainer;
import com.portable.server.model.judge.work.AbstractJudgeWork;
import com.portable.server.model.judge.work.SolutionJudgeWork;
import com.portable.server.model.judge.work.TestJudgeWork;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.redis.RedisKeyAndExpire;
import com.portable.server.model.redis.ServiceVerifyCode;
import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.model.response.judge.TestInfoResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.socket.EpollManager;
import com.portable.server.support.FileSupport;
import com.portable.server.support.JudgeSupport;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.JudgeWorkType;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

    @Value("${portable.recover.judge}")
    private Integer recoverJudgeJob;

    @Value("${portable.service.code.expire}")
    private Integer serverCodeExpireTime;

    /**
     * 保存在 redis 中的服务器密钥的 key 值
     */
    private static final String SERVICE_CODE_KEY = "SERVICE_CODE";

    @Resource
    private Environment env;

    @Resource
    private UserManager userManager;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private ProblemManager problemManager;

    @Resource
    private FileSupport fileSupport;

    @Resource
    private RedisValueHelper redisValueHelper;

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

        // 获取过去一段时间内的一部分未完成的提交，并对他们进行 rejudge
        List<Solution> solutionList = solutionManager.selectSolutionLastNotEndSolution(recoverJudgeJob);
        solutionList.forEach(solution -> {
            try {
                // 不重新运行题目的校验任务
                if (!SolutionType.PROBLEM_PROCESS.equals(solution.getSolutionType())) {
                    addJudgeTask(solution.getId());
                }
            } catch (PortableException ignored) {
            }
        });
        List<SolutionStatusType> notEndStatusList = Arrays.stream(SolutionStatusType.values())
                .filter(solutionStatusType -> !solutionStatusType.getEndingResult())
                .collect(Collectors.toList());
        solutionManager.updateAllStatus(notEndStatusList, SolutionStatusType.SYSTEM_ERROR);
        problemManager.updateAllStatus(ProblemStatusType.TREATING, ProblemStatusType.UNTREATED);
        problemManager.updateAllStatus(ProblemStatusType.CHECKING, ProblemStatusType.UNCHECK);
    }

    @Override
    public List<JudgeContainer> getJudgeContainerList() {
        return new ArrayList<>(judgeCodeJudgeMap.values());
    }

    @Override
    public void updateJudgeContainer(UpdateJudgeContainer updateJudgeContainer) {
        JudgeContainer judgeContainer = judgeCodeJudgeMap.get(updateJudgeContainer.getJudgeCode());
        if (judgeContainer == null) {
            throw PortableException.of("A-07-001");
        }
        updateJudgeContainer.toJudgeContainer(judgeContainer);
    }

    @Override
    public void addJudgeTask(Long solutionId) {
        Solution solution = solutionManager.selectSolutionById(solutionId)
                .orElseThrow(PortableException.from("S-06-001", solutionId));
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
    public void addTestTask(Long problemId) {
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
        Solution solution = solutionManager.selectSolutionById(solutionId)
                .orElseThrow(PortableException.from("A-05-001", solutionId));

        if (!SolutionStatusType.ACCEPT.equals(endType)) {
            return;
        }
        // 更新统计数量
        switch (solution.getSolutionType()) {
            case PUBLIC:
                problemManager.updateProblemCount(solutionJudgeWork.getProblemId(), 0, 1);

                Optional<User> userOptional = userManager.getAccountById(solution.getUserId());
                if (userOptional.isPresent() && userOptional.get().getType().getIsNormal()) {
                    NormalUserData normalUserData = userManager.getNormalUserDataById(userOptional.get().getDataId());
                    normalUserData.setAccept(normalUserData.getAccept() + 1);
                    userManager.updateUserData(normalUserData);
                }
                break;
            case PROBLEM_PROCESS:
            case CONTEST:
                // 在生成榜单时候，再更新
            case TEST_CONTEST:
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
    public void removeProblemCache(Long problemId) {
        judgeCodeJudgeMap.values().forEach(judgeContainer -> judgeContainer.addDeleteProblemCacheId(problemId));
    }

    @Override
    public void removeProblemJudge(Long problemId) {
        judgeCodeJudgeMap.values().forEach(judgeContainer -> judgeContainer.addDeleteProblemJudgeId(problemId));
    }

    @Override
    public void killJudge(String judgeCode) {
        judgeCodeJudgeMap.get(judgeCode).setTerminal(true);
    }

    @Override
    public ServiceVerifyCode getServiceCode() {
        if (serviceVerifyCode != null) {
            return serviceVerifyCode;
        }
        RedisKeyAndExpire<String> serviceCode = redisValueHelper.getValueAndTime(SERVICE_CODE_KEY, "");
        if (serviceCode.getHasKey()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, serviceCode.getExpireTime().intValue());
            return ServiceVerifyCode.builder()
                    .code(serviceCode.getData())
                    .endTime(calendar.getTime())
                    .temporary(true)
                    .build();
        }
        String code = UUID.randomUUID().toString();
        redisValueHelper.set(SERVICE_CODE_KEY, "", code, Long.valueOf(serverCodeExpireTime));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, serverCodeExpireTime);
        return ServiceVerifyCode.builder()
                .code(code)
                .temporary(true)
                .endTime(calendar.getTime())
                .build();
    }

    @Override
    public String registerJudge(String serverCode, Integer maxThreadCore, Integer maxWorkCore, Integer maxSocketCore) {
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
                .needDeleteProblemCacheIdList(new ArrayList<>())
                .needDeleteProblemJudgeIdList(new ArrayList<>())
                .terminal(false)
                .build();

        tcpJudgeMap.put(address, judgeContainer);
        judgeContainer.getTcpAddressSet().add(address);
        judgeCodeJudgeMap.put(judgeCode, judgeContainer);

        return judgeCode;
    }

    @Override
    public void append(String judgeCode) {
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
    public HeartbeatResponse heartbeat(Integer threadAccumulation, Integer workAccumulation, Integer socketAccumulation) {
        JudgeContainer judgeContainer = getCurContainer();
        judgeContainer.setThreadAccumulation(threadAccumulation);
        judgeContainer.setWorkAccumulation(workAccumulation);
        judgeContainer.setSocketAccumulation(socketAccumulation);
        judgeContainer.setLastHeartbeat(new Date());

        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();
        if (judgeContainer.getTerminal()) {
            heartbeatResponse.terminate();
            return heartbeatResponse;
        }

        if (judgeContainer.getIsNewCore()) {
            judgeContainer.setIsNewCore(false);
            heartbeatResponse.setNewThreadCore(judgeContainer.getMaxThreadCore());
            heartbeatResponse.setNewWorkCore(judgeContainer.getMaxWorkCore());
            heartbeatResponse.setNewSocketCore(judgeContainer.getMaxSocketCore());
        }
        judgeContainer.dump(heartbeatResponse);
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
                    i--;
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
    public SolutionInfoResponse getSolutionInfo(Long solutionId) {
        getCurContainer();
        SolutionInfoResponse solutionInfoResponse = new SolutionInfoResponse();
        Solution solution = solutionManager.selectSolutionById(solutionId)
                .orElseThrow(PortableException.from("S-06-001", solutionId));
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
    public String getSolutionCode(Long solutionId) {
        getCurContainer();
        SolutionData solutionData = getSolutionData(solutionId);
        return solutionData.getCode();
    }

    @Override
    public String getProblemJudgeCode(Long problemId) {
        getCurContainer();
        ProblemData problemData = getProblemData(problemId);
        return problemData.getJudgeCode();
    }

    @Override
    public void reportCompileResult(Long solutionId, Boolean compileResult, Boolean judgeCompileResult, String compileMsg) {
        getCurContainer();
        SolutionData solutionData = getSolutionData(solutionId);
        solutionData.setCompileMsg(compileMsg);
        solutionManager.saveSolutionData(solutionData);
        solutionJudgeWorkMap.get(solutionId).setCurTestId(0);
        if (!compileResult || !judgeCompileResult) {
            killJudgeTask(solutionId, compileResult ? SolutionStatusType.JUDGE_COMPILE_ERROR : SolutionStatusType.COMPILE_ERROR, 0, 0);
        } else {
            solutionManager.updateStatus(solutionId, SolutionStatusType.JUDGING);
        }
    }

    @Override
    public void reportRunningResult(Long solutionId, SolutionStatusType statusType, String testName, Integer timeCost, Integer memoryCost, String msg) {
        getCurContainer();
        SolutionJudgeWork solutionJudgeWork = solutionJudgeWorkMap.get(solutionId);
        SolutionData solutionData = getSolutionData(solutionId);

        // 除非是 AC 或者 WA，否则 msg 里面的信息有可能是上一次的信息
        if (!SolutionStatusType.ACCEPT.equals(statusType) && !SolutionStatusType.WRONG_ANSWER.equals(statusType)) {
            msg = null;
        }
        solutionData.getRunningMsg().put(testName,
                SolutionData.JudgeReportMsg.builder()
                        .statusType(statusType)
                        .msg(msg)
                        .build()
        );
        solutionManager.saveSolutionData(solutionData);
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
    public InputStream getStandardJudgeCode(String name) {
        try {
            JudgeCodeType judgeCodeType = JudgeCodeType.valueOf(name);
            return judgeCodeType.getCode();
        } catch (IllegalArgumentException e) {
            throw PortableException.of("S-06-007", name);
        }
    }

    @Override
    public InputStream getTestLibCode() {
        return JudgeCodeType.getTestLib();
    }

    @Override
    public InputStream getProblemInputTest(Long problemId, String name) {
        getCurContainer();
        return fileSupport.getTestInput(problemId, name);
    }

    @Override
    public InputStream getProblemOutputTest(Long problemId, String name) {
        getCurContainer();
        return fileSupport.getTestOutput(problemId, name);
    }

    @Override
    public String getSolutionNextTestName(Long solutionId) {
        getCurContainer();
        SolutionJudgeWork solutionJudgeWork = solutionJudgeWorkMap.get(solutionId);
        if (solutionJudgeWork == null) {
            throw PortableException.of("S-06-008", solutionId);
        }
        ProblemData problemData = getProblemData(solutionJudgeWork.getProblemId());
        return problemData.getTestName().get(solutionJudgeWork.nextTest());
    }

    @Override
    public TestInfoResponse getTestInfo(Long problemId) {
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
    public String getTestStdCode(Long problemId) {
        getCurContainer();

        ProblemData problemData = getTestProblemData(getTestProblem(problemId));

        return problemData.getStdCode().getCode();
    }

    @Override
    public String getNextTest(Long problemId) {
        getCurContainer();
        TestJudgeWork testJudgeWork = problemTestJudgeWorkMap.get(problemId);
        if (testJudgeWork == null) {
            throw PortableException.of("S-06-009", problemId);
        }
        ProblemData problemData = getTestProblemData(getTestProblem(problemId));
        return problemData.getTestName().get(testJudgeWork.nextTest());
    }

    @Override
    public void reportTestOutput(Long problemId, Boolean flag, String name, Integer pos, byte[] value) {
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
    public void reportTestCompileFail(Long problemId) {
        killTestTask(problemId, false);
        throw PortableException.of("S-06-009", problemId);
    }

    @Override
    public void reportTestOver(Long problemId) {
        killTestTask(problemId, true);
        Problem problem = getTestProblem(problemId);
        ProblemData problemData = getTestProblemData(problem);
        // 检查是否有必要 check
        Set<LanguageType> testCodeUsed = problemData.getTestCodeList().stream()
                .map(ProblemData.StdCode::getLanguageType)
                .collect(Collectors.toSet());
        if (!new HashSet<>(problemData.getSupportLanguage()).containsAll(testCodeUsed)) {
            problemManager.updateProblemStatus(problemId, ProblemStatusType.CHECK_FAILED);
            return;
        }

        if (problemData.getTestCodeList().size() == 0) {
            problemManager.updateProblemStatus(problemId, ProblemStatusType.NORMAL);
            return;
        }
        problemManager.updateProblemStatus(problemId, ProblemStatusType.CHECKING);

        for (ProblemData.StdCode stdCode : problemData.getTestCodeList()) {
            SolutionData solutionData = solutionManager.newSolutionData(problemData);
            solutionData.setCode(stdCode.getCode());
            solutionManager.insertSolutionData(solutionData);

            Solution solution = solutionManager.newSolution();
            solution.setDataId(solutionData.getId());
            solution.setUserId(problem.getOwner());
            solution.setProblemId(problemId);
            solution.setLanguageType(stdCode.getLanguageType());
            solution.setSolutionType(SolutionType.PROBLEM_PROCESS);
            solutionManager.insertSolution(solution);
            addJudgeTask(solution.getId());
            stdCode.setSolutionId(solution.getId());
        }
        problemManager.updateProblemData(problemData);
    }

    private JudgeContainer getCurContainer() {
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
                try {
                    killJudgeTask(solutionJudgeWork.getSolutionId(), SolutionStatusType.SYSTEM_ERROR, 0, 0);
                } catch (PortableException ignore) {
                }
            }
            for (TestJudgeWork testJudgeWork : judgeContainer.getTestWorkMap().values()) {
                problemManager.updateProblemStatus(testJudgeWork.getProblemId(), ProblemStatusType.TREAT_FAILED);
            }
            for (String tcpAddress : judgeContainer.getTcpAddressSet()) {
                tcpJudgeMap.remove(tcpAddress);
            }
        }
    }

    private SolutionData getSolutionData(Long solutionId) {
        Solution solution = solutionManager.selectSolutionById(solutionId)
                .orElseThrow(PortableException.from("S-06-001", solutionId));
        return solutionManager.getSolutionData(solution.getDataId());
    }

    private ProblemData getProblemData(Long problemId) {
        Problem problem = problemManager.getProblemById(problemId)
                .orElseThrow(PortableException.from("S-06-005", problemId));
        if (!problem.getStatusType().getTreated()) {
            throw PortableException.of("S-06-006", problemId);
        }
        return problemManager.getProblemData(problem.getDataId());
    }

    private Problem getTestProblem(Long problemId) {
        return problemManager.getProblemById(problemId)
                .orElseThrow(PortableException.from("S-06-005", problemId));
    }

    private ProblemData getTestProblemData(Problem problem) {
        return problemManager.getProblemData(problem.getDataId());
    }

    /**
     * 如果这次提交是进行 check 的，那么检查现在 check 是不是已经结束了
     *
     * @param solutionJudgeWork 这次提交的测试信息
     * @throws PortableException 题目不存在则抛出错误
     */
    private void checkProblemCheckOver(SolutionJudgeWork solutionJudgeWork) {
        if (JudgeWorkType.CHECK_PROBLEM.equals(solutionJudgeWork.getJudgeWorkType())) {
            synchronized (this) {
                ProblemData problemData = getProblemData(solutionJudgeWork.getProblemId());
                Set<Boolean> statusSet = problemData.getTestCodeList().stream()
                        .map(stdCode -> {
                            Optional<Solution> solutionOptional = solutionManager.selectSolutionById(stdCode.getSolutionId());
                            if (!solutionOptional.isPresent()) {
                                return false;
                            }
                            Solution solution = solutionOptional.get();
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
