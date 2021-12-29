package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.judge.entity.JudgeContainer;
import com.portable.server.model.judge.work.AbstractJudgeWork;
import com.portable.server.model.judge.work.SolutionJudgeWork;
import com.portable.server.model.judge.work.TestJudgeWork;
import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.service.JudgeService;
import com.portable.server.type.SolutionStatusType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author shiroha
 */
@Component
public class JudgeServiceImpl implements JudgeService {

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

    @Value("${SERVICE_CODE}")
    private String serviceCode;

    @Resource
    private SolutionManager solutionManager;

    @PostConstruct
    public void init() {
        tcpJudgeMap = new ConcurrentHashMap<>(4);
        judgeCodeJudgeMap = new ConcurrentHashMap<>(2);
        solutionJudgeWorkMap = new ConcurrentHashMap<>(64);
        judgeWorkPriorityQueue = new PriorityBlockingQueue<>();
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

    }

    @Override
    public String serviceCode() {
        return null;
    }

    @Override
    public String registerJudge(String serverCode, Integer maxThreadCore, Integer maxSocketCore, Map<String, String> languageVersion) {
        return null;
    }

    @Override
    public void append(String judgeCode) {

    }

    @Override
    public HeartbeatResponse heartBeat(Integer socketAccumulation, Integer threadAccumulation) {
        return null;
    }

    @Override
    public SolutionInfoResponse getSolutionInfo(Long solutionId) {
        return null;
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

    private void removeJudge() {

    }
}
