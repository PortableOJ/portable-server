package com.portable.server.model.response.contest;

import lombok.Data;

import java.util.Map;

/**
 * @author shiroha
 */
@Data
public class ContestRankResponse {

    /**
     * 用户信息
     */
    private String userHandle;

    /**
     * 总共的惩罚时间（秒）
     */
    private Integer totalCost;

    /**
     * 总共的解决问题数量
     */
    private Integer totalSolve;

    /**
     * 用户提交的题目的信息
     */
    private Map<Integer, ContestRankProblemResponse> submitStatus;

    @Data
    public static class ContestRankProblemResponse {

        /**
         * 解决此题目经过的时间（秒）
         */
        private Integer solveTime;

        /**
         * 总共提交的且不包含不惩罚的次数
         */
        private Integer tryTimes;

        /**
         * 冻结榜单的时候提交的次数
         */
        private Integer freezeSubmit;
    }
}
