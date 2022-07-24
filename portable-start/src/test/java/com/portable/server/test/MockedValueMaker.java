package com.portable.server.test;

import java.util.Date;
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

    public synchronized static Date mDate() {
        return new Date(mLong());
    }
}
