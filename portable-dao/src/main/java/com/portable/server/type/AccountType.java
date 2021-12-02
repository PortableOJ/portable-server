package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

@Getter
public enum AccountType implements ExceptionTextType {

    /**
     * 标准的账号类型
     */
    NORMAL("标准账号"),

    /**
     * 临时账号类型
     */
    TEMPORARY("临时账号"),
    ;

    private final String text;

    AccountType(String text) {
        this.text = text;
    }
}
