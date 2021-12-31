package com.portable.server.service.impl;

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
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.service.JudgeService;
import com.portable.server.socket.EpollManager;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.type.SolutionStatusType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author shiroha
 */
@Component
public class JudgeServiceImpl implements JudgeService {

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
     * 所有未进行的任务
     */
    private Queue<AbstractJudgeWork> judgeWorkPriorityQueue;

    /**
     * 是否写死 service code，为 null 则为随机
     */
    @Value("${SERVICE_CODE}")
    private String serviceCode;

    /**
     * 若 service Code 非 null，则生成一个此 code
     */
    private ServiceVerifyCode serviceVerifyCode;

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

    @PostConstruct
    public void init() {
        tcpJudgeMap = new ConcurrentHashMap<>(4);
        judgeCodeJudgeMap = new ConcurrentHashMap<>(2);
        solutionJudgeWorkMap = new ConcurrentHashMap<>(64);
        judgeWorkPriorityQueue = new PriorityBlockingQueue<>();

        if (serviceCode != null) {
            serviceVerifyCode = ServiceVerifyCode.builder()
                    .code(serviceCode)
                    .temporary(false)
                    .endTime(null)
                    .build();
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
        SolutionJudgeWork solutionJudgeWork = new SolutionJudgeWork(solution.getSolutionType());
        solutionJudgeWork.setSolutionId(solutionId);
        solutionJudgeWork.setCurTestId(null);
        judgeWorkPriorityQueue.add(solutionJudgeWork);
    }

    @Override
    public void addTestTask(Long problemId) {
        TestJudgeWork testJudgeWork = new TestJudgeWork();
        testJudgeWork.setProblemId(problemId);
        testJudgeWork.setCurTestId(null);
        judgeWorkPriorityQueue.add(testJudgeWork);
    }

    @Override
    public void killJudgeTask(Long solutionId) {
        solutionJudgeWorkMap.get(solutionId).setKilled(true);
    }

    @Override
    public ServiceVerifyCode getServiceCode() {
        return serviceVerifyCode != null ? serviceVerifyCode : temporaryDataManager.getServiceCode();
    }

    @Override
    public String registerJudge(String serverCode, Integer maxThreadCore, Integer maxSocketCore, Map<String, String> languageVersion) throws PortableException {
        cleanJudgeContainer();
        if (!Objects.equals(serverCode, getServiceCode().getCode())) {
            throw PortableException.of("S-06-002", serverCode);
        }
        String judgeCode = UUID.randomUUID().toString();
        String address = EpollManager.getAddress();
        JudgeContainer judgeContainer = JudgeContainer.builder()
                .judgeCode(judgeCode)
                .maxThreadCore(maxThreadCore)
                .maxSocketCore(maxSocketCore)
                .maxWorkNum(0)
                .sockets(Collections.synchronizedSet(new HashSet<String>(1) {{
                    add(address);
                }}))
                .lastHeartbeat(new Date())
                .judgeWorkMap(new ConcurrentHashMap<>(1))
                .testWorkMap(new ConcurrentHashMap<>(1))
                .build();

        tcpJudgeMap.put(address, judgeContainer);
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
        judgeContainer.getSockets().add(address);
        tcpJudgeMap.put(address, judgeContainer);
    }

    @Override
    public HeartbeatResponse heartBeat(Integer socketAccumulation, Integer threadAccumulation) throws PortableException {
        JudgeContainer judgeContainer = getCurContainer();
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();
        if (judgeContainer.getIsNewCore()) {
            judgeContainer.setIsNewCore(false);
            heartbeatResponse.setNewThreadCore(judgeContainer.getMaxThreadCore());
            heartbeatResponse.setNewSocketCore(judgeContainer.getMaxSocketCore());
        }
        int newWork = judgeContainer.getMaxWorkNum() - judgeContainer.getJudgeWorkMap().size() - judgeContainer.getTestWorkMap().size();
        for (int i = 0; i < newWork; i++) {
            AbstractJudgeWork judgeWork = judgeWorkPriorityQueue.poll();
            if (judgeWork instanceof SolutionJudgeWork) {
                SolutionJudgeWork solutionJudgeWork = (SolutionJudgeWork) judgeWork;
                judgeContainer.getJudgeWorkMap().put(solutionJudgeWork.getSolutionId(), solutionJudgeWork);
            } else if (judgeWork instanceof TestJudgeWork) {
                TestJudgeWork testJudgeWork = (TestJudgeWork) judgeWork;
                judgeContainer.getTestWorkMap().put(testJudgeWork.getProblemId(), testJudgeWork);
            }
        }
        return heartbeatResponse;
    }

    @Override
    public SolutionInfoResponse getSolutionInfo(Long solutionId) throws PortableException {
        SolutionInfoResponse solutionInfoResponse = new SolutionInfoResponse();
        Solution solution = solutionManager.selectSolutionById(solutionId);
        if (solution == null) {
            throw PortableException.of("S-06-001", solutionId);
        }
        SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
        if (solutionData == null) {
            throw PortableException.of("S-05-001");
        }
        Problem problem = problemManager.getProblemById(solution.getProblemId());
        if (problem == null) {
            throw PortableException.of("S-06-005", solution.getProblemId());
        }
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        if (problemData == null) {
            throw PortableException.of("S-03-001");
        }
        solutionInfoResponse.setProblemId(solution.getProblemId());
        solutionInfoResponse.setLanguage(solution.getLanguageType());
        solutionInfoResponse.setJudgeName(problemData.getJudgeCodeType());
        solutionInfoResponse.setTestNum(problemData.getTestName().size());
        solutionInfoResponse.setTimeLimit(problemData.getTimeLimit(solution.getLanguageType()));
        solutionInfoResponse.setMemoryLimit(problemData.getMemoryLimit(solution.getLanguageType()));
        return solutionInfoResponse;
    }

    @Override
    public String getSolutionCode(Long solutionId) {
        return null;
    }

    @Override
    public String getProblemJudgeCode(Long problemId) {
        return null;
    }

    @Override
    public void reportCompileResult(Long solutionId, Boolean compileResult, String compileMsg) {

    }

    @Override
    public void reportRunningResult(Long solutionId, SolutionStatusType statusType, Long timeCost, Long memoryCost) {

    }

    @Override
    public String getStandardJudgeList() {
        return null;
    }

    @Override
    public String getStandardJudgeCode(String name) {
        return null;
    }

    @Override
    public File getProblemInputTest(String name) {
        return null;
    }

    @Override
    public File getProblemOutputTest(String name) {
        return null;
    }

    @Override
    public String getSolutionNextTestName(Long solutionId) {
        return null;
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
                solutionManager.updateStatus(solutionJudgeWork.getSolutionId(), SolutionStatusType.SYSTEM_ERROR);
                solutionJudgeWorkMap.remove(solutionJudgeWork.getSolutionId());
            }
            for (TestJudgeWork testJudgeWork : judgeContainer.getTestWorkMap().values()) {
                problemManager.updateProblemStatus(testJudgeWork.getProblemId(), ProblemStatusType.TREAT_FAILED);
            }
        }
    }

    private void removeJudge() {

    }
}
