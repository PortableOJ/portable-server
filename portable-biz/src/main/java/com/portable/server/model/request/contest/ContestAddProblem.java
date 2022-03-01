package com.portable.server.model.request.contest;

import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class ContestAddProblem {

    private Long contestId;

    private Long problemId;
}
