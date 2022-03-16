package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum ContestAccessType implements ExceptionTextType {

    /**
     * 公开的
     */
    PUBLIC("公开的"),

    /**
     * 需要密码的
     */
    PASSWORD("需要密码的"),

    /**
     * 邀请制的
     */
    PRIVATE("邀请制的"),

    /**
     * 提供账号的
     */
    BATCH("提供账号的")
    ;

    private final String text;

    ContestAccessType(String text) {
        this.text = text;
    }
}
