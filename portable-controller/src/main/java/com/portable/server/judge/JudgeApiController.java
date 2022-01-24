package com.portable.server.judge;

import com.portable.server.exception.PortableException;
import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.model.response.judge.TestInfoResponse;
import com.portable.server.service.JudgeService;
import com.portable.server.socket.EpollManager;
import com.portable.server.socket.annotation.EpollMethod;
import com.portable.server.socket.annotation.EpollParam;
import com.portable.server.socket.type.EpollDataType;
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
    public SolutionInfoResponse getSolutionInfo(@EpollParam Long id) throws PortableException {
        return judgeService.getSolutionInfo(id);
    }

    @EpollMethod("SolutionTest")
    public String getSolutionNextTestName(@EpollParam Long id) throws PortableException {
        return judgeService.getSolutionNextTestName(id);
    }

    @EpollMethod("SolutionTestReport")
    public void reportRunningResult(@EpollParam Long id, @EpollParam SolutionStatusType value, @EpollParam(EpollDataType.COMPLEX) String msg, @EpollParam Integer timeCost, @EpollParam Integer memoryCost) throws PortableException {
        System.out.println(msg);
        judgeService.reportRunningResult(id, value, timeCost, memoryCost);
    }

    @EpollMethod("SolutionCompileMsgReport")
    public void reportCompileResult(@EpollParam Long id, @EpollParam Boolean value, @EpollParam Boolean judge, @EpollParam(EpollDataType.COMPLEX) String msg) throws PortableException {
        judgeService.reportCompileResult(id, value, judge, msg);
    }

    @EpollMethod("SolutionCode")
    public String getSolutionCode(@EpollParam Long id) throws PortableException {
        return judgeService.getSolutionCode(id);
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
    public String getProblemJudgeCode(@EpollParam Long id) throws PortableException {
        return judgeService.getProblemJudgeCode(id);
    }

    @EpollMethod("ProblemDataIn")
    public InputStream getProblemInputTest(@EpollParam Long id, @EpollParam String name) throws PortableException {
        return judgeService.getProblemInputTest(id, name);
    }

    @EpollMethod("ProblemDataOut")
    public InputStream getProblemOutputTest(@EpollParam Long id, @EpollParam String name) throws PortableException {
        return judgeService.getProblemOutputTest(id, name);
    }

    @EpollMethod("TestLibRequest")
    public File getTestLibCode() throws PortableException {
        return judgeService.getTestLibCode();
    }

    @EpollMethod("TestInfo")
    public TestInfoResponse getTestInfo(@EpollParam Long id) throws PortableException {
        return judgeService.getTestInfo(id);
    }

    @EpollMethod("TestStdCode")
    public String getTestStdCode(@EpollParam Long id) throws PortableException {
        return judgeService.getTestStdCode(id);
    }

    @EpollMethod("TestTest")
    public String getNextTest(@EpollParam Long id) throws PortableException {
        return judgeService.getNextTest(id);
    }

    @EpollMethod("TestReportOutput")
    public void reportTestOutput(@EpollParam Long id, @EpollParam Boolean flag, @EpollParam String name, @EpollParam Integer pos, @EpollParam(EpollDataType.COMPLEX) byte[] value) throws PortableException {
        judgeService.reportTestOutput(id, flag, name, pos, value);
    }

    @EpollMethod("TestResultReport")
    public void reportTestCompileFail(@EpollParam Long id) throws PortableException {
        judgeService.reportTestCompileFail(id);
    }

    @EpollMethod("TestReportOver")
    public void reportTestOver(@EpollParam Long id) throws PortableException {
        judgeService.reportTestOver(id);
    }
}
