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
public class PortableException extends Exception {

    @Getter
    private final String code;

    @Getter
    private final Object[] objects;

    public PortableException(String code, Object... objects) {
        this.code = code;
        this.objects = objects;
    }

    public PortableException(Throwable throwable, String code, Object... objects) {
        super(throwable);
        this.code = code;
        this.objects = objects;
    }

    @Override
    public String getMessage() {
        return code;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // 阻止填充栈信息，因为通常不需要
        return this;
    }

    public static PortableException of(String code, Object... objects) {
        return new PortableException(code, objects);
    }

    public static PortableException of(Throwable throwable, String code, Object... objects) {
        return new PortableException(throwable, code, objects);
    }

    public static Supplier<PortableException> from(String code, Object... objects) {
        return () -> new PortableException(code, objects);
    }

    public PortableRuntimeException asRuntime() {
        return PortableRuntimeException.of(this);
    }
}
