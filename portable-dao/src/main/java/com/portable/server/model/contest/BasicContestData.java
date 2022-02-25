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
    private Long freezeTime;

    /**
     * 公告
     */
    private String announcement;

    /**
     * 获取访问权限的值
     * @return 访问权限的值
     */
    public abstract Object getAccessValue();

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

        ContestProblemData() {
            this.submissionCount = 0;
            this.acceptCount = 0;
        }
    }
}
