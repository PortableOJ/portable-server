package com.portable.server.judge;

import com.portable.server.exception.PortableException;
import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.service.JudgeService;
import com.portable.server.socket.EpollManager;
import com.portable.server.socket.annotation.EpollMethod;
import com.portable.server.socket.annotation.EpollParam;
import com.portable.server.type.SolutionStatusType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;

/**
 * @author shiroha
 */
@Component
public class JudgeApiController {

    static {
        EpollManager.setUp(JudgeApiController.class);
    }

    @Resource
    private JudgeService judgeService;

    @EpollMethod("Register")
    public String registerJudge(@EpollParam String serverCode, @EpollParam Integer maxThreadCore, @EpollParam Integer maxWorkCore, @EpollParam Integer maxSocketCore) throws PortableException {
        return judgeService.registerJudge(serverCode, maxThreadCore, maxWorkCore, maxSocketCore);
    }

    @EpollMethod("Append")
    public void append(@EpollParam String judgeCode) throws PortableException {
        judgeService.append(judgeCode);
    }

    @EpollMethod(close = true)
    public void close() {
        judgeService.close();
    }

    @EpollMethod("Heartbeat")
    public HeartbeatResponse heartbeat(Integer socketAccumulation, Integer workAccumulation, Integer threadAccumulation) throws PortableException {
        return judgeService.heartbeat(socketAccumulation, workAccumulation, threadAccumulation);
    }

    @EpollMethod("SolutionInfo")
    public SolutionInfoResponse getSolutionInfo(Long solutionId) throws PortableException {
        return judgeService.getSolutionInfo(solutionId);
    }

    @EpollMethod("SolutionTest")
    public String getSolutionNextTestName(Long solutionId) throws PortableException {
        return judgeService.getSolutionNextTestName(solutionId);
    }

    @EpollMethod("SolutionTestReport")
    public void reportRunningResult(Long solutionId, SolutionStatusType statusType, Integer timeCost, Integer memoryCost) throws PortableException {
        judgeService.reportRunningResult(solutionId, statusType, timeCost, memoryCost);
    }

    @EpollMethod("SolutionCompileMsgReport")
    public void reportCompileResult(Long solutionId, Boolean compileResult, Boolean judgeCompileResult, String compileMsg) throws PortableException {
        judgeService.reportCompileResult(solutionId, compileResult, judgeCompileResult, compileMsg);
    }

    @EpollMethod("SolutionCode")
    public String getSolutionCode(Long solutionId) throws PortableException {
        return judgeService.getSolutionCode(solutionId);
    }

    @EpollMethod("StandardJudge")
    public String getStandardJudgeList() {
        return judgeService.getStandardJudgeList();
    }

    @EpollMethod("StandardJudgeCode")
    public File getStandardJudgeCode(String name) throws PortableException {
        return judgeService.getStandardJudgeCode(name);
    }

    @EpollMethod("ProblemJudgeCode")
    public String getProblemJudgeCode(Long problemId) throws PortableException {
        return judgeService.getProblemJudgeCode(problemId);
    }

    @EpollMethod("ProblemDataIn")
    InputStream getProblemInputTest(Long problemId, String name) throws PortableException {
        return judgeService.getProblemInputTest(problemId, name);
    }

    @EpollMethod("ProblemDataOut")
    InputStream getProblemOutputTest(Long problemId, String name) throws PortableException {
        return judgeService.getProblemOutputTest(problemId, name);
    }

    @EpollMethod("TestLibRequest")
    File getTestLibCode() throws PortableException {
        return judgeService.getTestLibCode();
    }
}
