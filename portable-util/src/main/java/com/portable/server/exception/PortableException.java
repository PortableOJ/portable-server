package com.portable.server.exception;

import java.util.function.Supplier;

import lombok.Getter;
import lombok.ToString;

/**
 * Portable 异常
 *
 * @author shiroha
 */
@ToString
public class PortableException extends RuntimeException {

    @Getter
    private final String code;

    @Getter
    private final Object[] objects;

    public static final String SYSTEM_CODE = "S-00-000";
    public static final String USER_INPUT_NULL = "A-00-001";
    public static final String THIRD_PART_CODE = "B-00-000";

    public PortableException(String code, Object... objects) {
        this.code = code;
        this.objects = objects;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // 阻止填充栈信息，因为不需要
        return this;
    }

    public static PortableException systemDefaultException() {
        return new PortableException(SYSTEM_CODE);
    }

    public static PortableException userInputNullException() {
        return new PortableException(USER_INPUT_NULL);
    }

    public static PortableException of(String code, Object... objects) {
        return new PortableException(code, objects);
    }

    public static Supplier<PortableException> from(String code, Object... objects) {
        return () -> new PortableException(code, objects);
    }
}
