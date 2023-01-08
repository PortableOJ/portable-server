package com.portable.server.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * @author shiroha
 */
@ToString
public class PortableRuntimeException extends RuntimeException {

    @Getter
    private final String code;

    @Getter
    private final Object[] objects;

    private PortableRuntimeException(String code, Object[] objects) {
        this.code = code;
        this.objects = objects;
    }

    private PortableRuntimeException(Throwable throwable, String code, Object[] objects) {
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

    public static PortableRuntimeException of(String code, Object... objects) {
        return new PortableRuntimeException(code, objects);
    }

    public static PortableRuntimeException of(Throwable throwable, String code, Object... objects) {
        return new PortableException(throwable, code, objects).asRuntime();
    }

    public static PortableRuntimeException of(PortableException exception) {
        return new PortableRuntimeException(exception, exception.getCode(), exception.getObjects());
    }
}
