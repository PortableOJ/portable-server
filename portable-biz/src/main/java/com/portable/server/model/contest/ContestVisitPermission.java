package com.portable.server.model.contest;

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
     * 比赛参与者
     */
    PARTICIPANT(2),

    /**
     * 合作出题人
     */
    CO_AUTHOR(3),

    /**
     * 比赛拥有者
     */
    ADMIN(4),
    ;

    private final Integer code;

    ContestVisitPermission(int code) {
        this.code = code;
    }

    public Boolean approve(ContestVisitPermission contestVisitPermission) {
        return this.code < contestVisitPermission.getCode();
    }
}
