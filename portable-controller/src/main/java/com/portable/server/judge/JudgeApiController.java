package com.portable.server.judge;

import com.portable.server.exception.PortableException;
import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.model.response.judge.TestInfoResponse;
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
    public HeartbeatResponse heartbeat(@EpollParam Integer socketAccumulation, @EpollParam Integer workAccumulation, @EpollParam Integer threadAccumulation) throws PortableException {
        return judgeService.heartbeat(socketAccumulation, workAccumulation, threadAccumulation);
    }

    @EpollMethod("SolutionInfo")
    public SolutionInfoResponse getSolutionInfo(@EpollParam Long solutionId) throws PortableException {
        return judgeService.getSolutionInfo(solutionId);
    }

    @EpollMethod("SolutionTest")
    public String getSolutionNextTestName(@EpollParam Long solutionId) throws PortableException {
        return judgeService.getSolutionNextTestName(solutionId);
    }

    @EpollMethod("SolutionTestReport")
    public void reportRunningResult(@EpollParam Long solutionId, @EpollParam SolutionStatusType statusType, @EpollParam Integer timeCost, @EpollParam Integer memoryCost) throws PortableException {
        judgeService.reportRunningResult(solutionId, statusType, timeCost, memoryCost);
    }

    @EpollMethod("SolutionCompileMsgReport")
    public void reportCompileResult(@EpollParam Long solutionId, @EpollParam Boolean compileResult, @EpollParam Boolean judgeCompileResult, @EpollParam String compileMsg) throws PortableException {
        judgeService.reportCompileResult(solutionId, compileResult, judgeCompileResult, compileMsg);
    }

    @EpollMethod("SolutionCode")
    public String getSolutionCode(@EpollParam Long solutionId) throws PortableException {
        return judgeService.getSolutionCode(solutionId);
    }

    @EpollMethod("StandardJudge")
    public String getStandardJudgeList() {
        return judgeService.getStandardJudgeList();
    }

    @EpollMethod("StandardJudgeCode")
    public File getStandardJudgeCode(@EpollParam String name) throws PortableException {
        return judgeService.getStandardJudgeCode(name);
    }

    @EpollMethod("ProblemJudgeCode")
    public String getProblemJudgeCode(@EpollParam Long problemId) throws PortableException {
        return judgeService.getProblemJudgeCode(problemId);
    }

    @EpollMethod("ProblemDataIn")
    public InputStream getProblemInputTest(@EpollParam Long problemId, @EpollParam String name) throws PortableException {
        return judgeService.getProblemInputTest(problemId, name);
    }

    @EpollMethod("ProblemDataOut")
    public InputStream getProblemOutputTest(@EpollParam Long problemId, @EpollParam String name) throws PortableException {
        return judgeService.getProblemOutputTest(problemId, name);
    }

    @EpollMethod("TestLibRequest")
    public File getTestLibCode() throws PortableException {
        return judgeService.getTestLibCode();
    }

    @EpollMethod("TestInfo")
    public TestInfoResponse getTestInfo(Long problemId) throws PortableException {
        return judgeService.getTestInfo(problemId);
    }

    @EpollMethod("TestStdCode")
    public String getTestStdCode(Long problemId) throws PortableException {
        return judgeService.getTestStdCode(problemId);
    }

    @EpollMethod("TestReportOutput")
    public void reportTestOutput(Long problemId, Boolean flag, String name, Integer pos, byte[] value) throws PortableException {
        judgeService.reportTestOutput(problemId, flag, name, pos, value);
    }

    @EpollMethod("TestResultReport")
    public void reportTestCompileFail(Long problemId) throws PortableException {
        judgeService.reportTestCompileFail(problemId);
    }

    @EpollMethod("TestReportOver")
    public void reportTestOver(Long problemId) {
        judgeService.reportTestOver(problemId);
    }
}
