package com.portable.server.exception;

import java.util.function.Supplier;

import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum PortableErrors {

    /**
     * 系统错误
     */
    SYSTEM_CODE("S-00-000"),

    LOCAL_THREAD_LOCK_FAIL("S-01-007"),

    USER_INPUT_NULL("A-00-001"),
    ;

    private final String code;

    PortableErrors(String code) {
        this.code = code;
    }

    /// region 静态方法

    public static PortableRuntimeException of(String code, Object... objects) {
        return PortableRuntimeException.of(code, objects);
    }

    public static PortableRuntimeException of(Throwable throwable, String code, Object... objects) {
        return PortableRuntimeException.of(throwable, code, objects);
    }

    public static Supplier<PortableRuntimeException> from(String code, Object... objects) {
        return () -> PortableRuntimeException.of(code, objects);
    }

    public static PortableException ofThrow(String code, Object... objects) {
        return PortableException.of(code, objects);
    }

    public static PortableException ofThrow(Throwable throwable, String code, Object... objects) {
        return PortableException.of(throwable, code, objects);
    }

    public PortableRuntimeException of() {
        return PortableErrors.of(this.code);
    }

    public PortableRuntimeException of(Object... objects) {
        return PortableErrors.of(this.code, objects);
    }

    public PortableRuntimeException of(Throwable throwable, Object... objects) {
        return PortableErrors.of(throwable, this.code, objects);
    }

    /// endregion

    /// region 成员方法

    public PortableException ofThrow() {
        return PortableErrors.ofThrow(this.code);
    }

    public PortableException ofThrow(Object... objects) {
        return PortableErrors.ofThrow(this.code, objects);
    }

    public PortableException ofThrow(Throwable throwable, Object... objects) {
        return PortableErrors.ofThrow(throwable, this.code, objects);
    }

    /// endregion

}
