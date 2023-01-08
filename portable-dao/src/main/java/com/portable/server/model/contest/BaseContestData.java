package com.portable.server.model.contest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.portable.server.exception.PortableErrors;
import com.portable.server.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseContestData extends BaseEntity<String> {

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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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

        public void init() {
            this.acceptCount = 0;
            this.submissionCount = 0;
        }

        public void incAccept() {
            this.acceptCount++;
        }

        public void incSubmit() {
            this.submissionCount++;
        }
    }

    public Map<Long, Integer> idToIndex() {
        return IntStream.range(0, problemList.size())
                .boxed()
                .collect(Collectors.toMap(i -> problemList.get(i).getProblemId(), i -> i));
    }

    public ContestProblemData atProblem(Integer index, Long contestId) {
        if (index < 0 || index >= problemList.size()) {
            throw PortableErrors.of("A-08-018", contestId, index);
        }
        return problemList.get(index);
    }

}
