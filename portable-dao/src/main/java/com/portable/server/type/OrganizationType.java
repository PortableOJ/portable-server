package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

import java.util.Objects;

/**
 * @author shiroha
 */

@Getter
public enum OrganizationType implements ExceptionTextType {

    /**
     * 00000000 学生
     */
    STUDENT(0x0, "普通学生"),

    /**
     * 00000001 特别的学生
     */
    SPECIAL_STUDENT(0x01, "特别的学生"),

    /**
     * 00000011 老师
     */
    TEACHER(0x03, "老师"),

    /**
     * 00000101 集训队
     */
    ACMER(0x05, "集训队成员"),

    /**
     * 00000111 ACM 会长
     */
    PRESIDENT(0x07, "ACM 会长"),

    /**
     * 00001111 荣誉管理
     */
    HONOR(0x0f, "荣誉集训队成员"),

    /**
     * 00011111 老大专属
     */
    BOSS(0x1f, "老大"),

    /**
     * 11111111 超级管理员
     */
    ADMIN(0xff, "超级管理员"),

    ;

    private final Integer code;
    private final String text;

    OrganizationType(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Boolean isDominate(OrganizationType organizationType) {
        return !Objects.equals(this, organizationType) && Integer.valueOf((this.code & organizationType.code)).equals(organizationType.getCode());
    }
}
