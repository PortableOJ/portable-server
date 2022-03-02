package com.portable.server.model.response.contest;

import lombok.Data;

import java.util.Map;

/**
 * @author shiroha
 */
@Data
public class ContestRankListResponse {

    /**
     * 排名
     */
    private Integer rank;

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
    private Map<Integer, ContestRankProblemStatusResponse> submitStatus;

    @Data
    public static class ContestRankProblemStatusResponse {

        /**
         * 解决此题目经过的时间（秒）
         */
        private Long solveTime;

        /**
         * 总共需要惩罚的次数
         */
        private Integer penaltyTimes;

        /**
         * 未结束评测以及冻结榜单时的提交数量
         */
        private Integer runningSubmit;
    }
}
