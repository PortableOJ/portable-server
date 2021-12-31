package com.portable.server.exception;

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

    public static final String SYSTEM_CODE = "S-00-000";
    public static final String USER_CODE = "A-00-000";
    public static final String THIRD_PART_CODE = "B-00-000";

    public PortableException(String code, Object... objects) {
        this.code = code;
        this.objects = objects;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    public static PortableException systemDefaultException() {
        return new PortableException(SYSTEM_CODE);
    }

    public static PortableException of(String code, Object... objects) {
        return new PortableException(code, objects);
    }
}
