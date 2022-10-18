package com.portable.server.judge;

import java.io.InputStream;

import javax.annotation.Resource;

import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.model.response.judge.TestInfoResponse;
import com.portable.server.socket.EpollManager;
import com.portable.server.socket.annotation.EpollMethod;
import com.portable.server.socket.annotation.EpollParam;
import com.portable.server.socket.type.EpollDataType;
import com.portable.server.support.JudgeSupport;
import com.portable.server.type.SolutionStatusType;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class JudgeApiController {

    static {
        EpollManager.setUp(JudgeApiController.class);
    }

    @Resource
    private JudgeSupport judgeSupport;

    @EpollMethod("Register")
    public String registerJudge(@EpollParam String serverCode, @EpollParam Integer maxThreadCore, @EpollParam Integer maxWorkCore, @EpollParam Integer maxSocketCore) {
        return judgeSupport.registerJudge(serverCode, maxThreadCore, maxWorkCore, maxSocketCore);
    }

    @EpollMethod("Append")
    public void append(@EpollParam String judgeCode) {
        judgeSupport.append(judgeCode);
    }

    @EpollMethod(close = true)
    public void close() {
        judgeSupport.close();
    }

    @EpollMethod("Heartbeat")
    public HeartbeatResponse heartbeat(@EpollParam Integer socketAccumulation, @EpollParam Integer workAccumulation, @EpollParam Integer threadAccumulation) {
        return judgeSupport.heartbeat(socketAccumulation, workAccumulation, threadAccumulation);
    }

    @EpollMethod("SolutionInfo")
    public SolutionInfoResponse getSolutionInfo(@EpollParam Long id) {
        return judgeSupport.getSolutionInfo(id);
    }

    @EpollMethod("SolutionTest")
    public String getSolutionNextTestName(@EpollParam Long id) {
        return judgeSupport.getSolutionNextTestName(id);
    }

    @EpollMethod("SolutionCompileMsgReport")
    public void reportCompileResult(@EpollParam Long id, @EpollParam Boolean value, @EpollParam Boolean judge, @EpollParam(EpollDataType.COMPLEX) String msg) {
        judgeSupport.reportCompileResult(id, value, judge, msg);
    }

    @EpollMethod("SolutionTestReport")
    public void reportRunningResult(@EpollParam Long id, @EpollParam SolutionStatusType value, @EpollParam String testName, @EpollParam(EpollDataType.COMPLEX) String msg, @EpollParam Integer timeCost, @EpollParam Integer memoryCost) {
        judgeSupport.reportRunningResult(id, value, testName, timeCost, memoryCost, msg);
    }

    @EpollMethod("SolutionCode")
    public String getSolutionCode(@EpollParam Long id) {
        return judgeSupport.getSolutionCode(id);
    }

    @EpollMethod("StandardJudge")
    public String getStandardJudgeList() {
        return judgeSupport.getStandardJudgeList();
    }

    @EpollMethod("StandardJudgeCode")
    public InputStream getStandardJudgeCode(@EpollParam String name) {
        return judgeSupport.getStandardJudgeCode(name);
    }

    @EpollMethod("ProblemJudgeCode")
    public String getProblemJudgeCode(@EpollParam Long id) {
        return judgeSupport.getProblemJudgeCode(id);
    }

    @EpollMethod("ProblemDataIn")
    public InputStream getProblemInputTest(@EpollParam Long id, @EpollParam String name) {
        return judgeSupport.getProblemInputTest(id, name);
    }

    @EpollMethod("ProblemDataOut")
    public InputStream getProblemOutputTest(@EpollParam Long id, @EpollParam String name) {
        return judgeSupport.getProblemOutputTest(id, name);
    }

    @EpollMethod("TestLibRequest")
    public InputStream getTestLibCode() {
        return judgeSupport.getTestLibCode();
    }

    @EpollMethod("TestInfo")
    public TestInfoResponse getTestInfo(@EpollParam Long id) {
        return judgeSupport.getTestInfo(id);
    }

    @EpollMethod("TestStdCode")
    public String getTestStdCode(@EpollParam Long id) {
        return judgeSupport.getTestStdCode(id);
    }

    @EpollMethod("TestTest")
    public String getNextTest(@EpollParam Long id) {
        return judgeSupport.getNextTest(id);
    }

    @EpollMethod("TestReportOutput")
    public void reportTestOutput(@EpollParam Long id, @EpollParam Boolean flag, @EpollParam String name, @EpollParam Integer pos, @EpollParam(EpollDataType.COMPLEX) byte[] value) {
        judgeSupport.reportTestOutput(id, flag, name, pos, value);
    }

    @EpollMethod("TestResultReport")
    public void reportTestCompileFail(@EpollParam Long id) {
        judgeSupport.reportTestCompileFail(id);
    }

    @EpollMethod("TestReportOver")
    public void reportTestOver(@EpollParam Long id) {
        judgeSupport.reportTestOver(id);
    }
}
