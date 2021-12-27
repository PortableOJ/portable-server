package com.portable.server.model.response.judge;

import com.portable.server.socket.model.AbstractEpollResponse;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.LanguageType;

/**
 * @author shiroha
 */
public class SolutionInfoResponse extends AbstractEpollResponse {

    private static final String PROBLEM_ID = "problemId";
    private static final String LANGUAGE = "language";
    private static final String JUDGE_NAME = "judgeName";
    private static final String TEST_NUM = "testNum";
    private static final String TIME_LIMIT = "timeLimit";
    private static final String MEMORY_LIMIT = "memoryLimit";


    public void setProblemId(Long problemId) {
        super.add(PROBLEM_ID, problemId);
    }

    public void setLanguage(LanguageType language) {
        super.add(LANGUAGE, language);
    }

    public void setJudgeName(JudgeCodeType judgeName) {
        super.add(JUDGE_NAME, judgeName);
    }

    public void setTestNum(Integer testNum) {
        super.add(TEST_NUM, testNum);
    }

    public void setTimeLimit(Integer timeLimit) {
        super.add(TIME_LIMIT, timeLimit);
    }

    public void setMemoryLimit(Integer memoryLimit) {
        super.add(MEMORY_LIMIT, memoryLimit);
    }
}
