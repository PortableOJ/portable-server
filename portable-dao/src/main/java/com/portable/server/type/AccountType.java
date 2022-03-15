package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum AccountType implements ExceptionTextType {

    /**
     * 标准的账号类型
     */
    NORMAL("标准账号"),

    /**
     * 批量账号类型
     */
    BATCH("批量账号"),
    ;

    private final String text;

    AccountType(String text) {
        this.text = text;
    }
}
