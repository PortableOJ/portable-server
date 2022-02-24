package com.portable.server.model.response.contest;

import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class ContestRankResponse {

    private String userHandle;

    private Integer totalCost;

    private Integer totalSolve;

    @Data
    public static class ContestRankProblemResponse {

        private Integer solveTime;

        private Integer tryTimes;
    }
}
