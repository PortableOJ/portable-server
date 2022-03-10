package com.portable.server.model.request.contest;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Data
public class ContestAddProblem {

    /**
     * 要添加题目的比赛
     */
    @NotNull(message = "A-08-002")
    @Min(value = 1, message = "A-08-002")
    private Long contestId;

    /**
     * 要添加的题目 id
     */
    @NotNull(message = "A-04-001")
    @Min(value = 1, message = "A-04-001")
    private Long problemId;
}
