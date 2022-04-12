package com.portable.server.util.test;

import java.util.UUID;

public class MockedValueMaker {

    private static Long longValue = 0L;

    public synchronized static Integer mInt() {
        return Math.toIntExact(longValue++);
    }

    public synchronized static Long mLong() {
        return longValue++;
    }

    public synchronized static String mString() {
        return UUID.randomUUID().toString();
    }
}
