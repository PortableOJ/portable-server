package com.portable.server.model.response.judge;

import com.portable.server.socket.model.AbstractEpollResponse;

/**
 * @author shiroha
 */
public class HeartbeatResponse extends AbstractEpollResponse {

    private static final String JUDGE_TASK = "judgeTask";
    private static final String TEST_TASK = "testTask";
    private static final String THREAD_CORE = "threadCore";
    private static final String SOCKET_CORE = "socketCore";

    public void addJudgeTask(Long solutionId) {
        super.add(JUDGE_TASK, solutionId);
    }

    public void addTestTask(Long problemId) {
        super.add(TEST_TASK, problemId);
    }

    public void setNewThreadCore(Integer threadCore) {
        super.add(THREAD_CORE, threadCore);
    }

    public void setNewSocketCore(Integer socketCore) {
        super.add(SOCKET_CORE, socketCore);
    }
}
