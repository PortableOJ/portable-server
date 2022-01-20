package com.portable.server.model.response.judge;

import com.portable.server.socket.model.AbstractEpollResponse;
import com.portable.server.type.LanguageType;

/**
 * @author shiroha
 */
public class TestInfoResponse extends AbstractEpollResponse {

    private static final String LANGUAGE = "language";
    private static final String TEST_NUM = "testNum";
    private static final String TIME_LIMIT = "timeLimit";
    private static final String MEMORY_LIMIT = "memoryLimit";

    public void setLanguage(LanguageType language) {
        super.add(LANGUAGE, language);
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
