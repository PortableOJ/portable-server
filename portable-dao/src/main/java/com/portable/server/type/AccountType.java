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
    NORMAL("标准账号", true),

    /**
     * 锁定的标准账号，无法进行找回密码操作，但是下一次正常登录后，将会恢复至标准账号
     */
    LOCKED_NORMAL("锁定的标准账号", true),

    /**
     * 批量账号类型
     */
    BATCH("批量账号", false),
    ;

    private final String text;
    private final Boolean isNormal;

    AccountType(String text, Boolean isNormal) {
        this.text = text;
        this.isNormal = isNormal;
    }
}
