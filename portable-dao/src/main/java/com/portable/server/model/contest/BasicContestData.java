package com.portable.server.model.contest;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
public abstract class BasicContestData {

    /**
     * 题目列表与题目信息
     */
    private List<ContestProblemData> problemList;

    /**
     * 共同的出题人
     */
    private Set<Long> coAuthor;

    /**
     * 封榜时长
     */
    private Integer freezeTime;

    /**
     * 公告
     */
    private String announcement;

    /**
     * 惩罚时间（分钟）
     */
    private Integer penaltyTime;

    @Data
    public static class ContestProblemData {

        /**
         * 真实的问题 id
         */
        private Long problemId;

        /**
         * 历史提交数量
         */
        private Integer submissionCount;

        /**
         * 历史通过的数量
         */
        private Integer acceptCount;

        public ContestProblemData(Long problemId) {
            this.problemId = problemId;
            this.submissionCount = 0;
            this.acceptCount = 0;
        }
    }
}
