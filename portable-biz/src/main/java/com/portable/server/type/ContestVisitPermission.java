package com.portable.server.type;

import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum ContestVisitPermission {

    /**
     * 无任何权限
     */
    NO_ACCESS(0),

    /**
     * 仅访问权限
     */
    VISIT(1),

    /**
     * 比赛参与者，可以提交
     */
    PARTICIPANT(2),

    /**
     * 合作出题人，可以在比赛开始前添加题目，可以提交至测试列表
     */
    CO_AUTHOR(3),

    /**
     * 比赛拥有者，可以拥有题目的完整权限，提交至测试列表
     */
    ADMIN(4),
    ;

    private final Integer code;

    ContestVisitPermission(int code) {
        this.code = code;
    }

    public Boolean approve(ContestVisitPermission contestVisitPermission) {
        return this.code <= contestVisitPermission.getCode();
    }
}
