package com.portable.server.model.contest;

import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public class ContestVisitPermission {

    private Boolean visit;

    private Boolean submit;

    private Boolean changeProblem;

    private Boolean manager;
}
