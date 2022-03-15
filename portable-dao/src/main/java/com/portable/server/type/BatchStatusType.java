package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum BatchStatusType implements ExceptionTextType {

    /**
     * 正常状态，可以登录
     */
    NORMAL("正常"),

    /**
     * 禁止登录状态，不再可以登录此账号
     */
    DISABLE("禁止登录");

    private final String text;

    BatchStatusType(String text) {
        this.text = text;
    }
}
