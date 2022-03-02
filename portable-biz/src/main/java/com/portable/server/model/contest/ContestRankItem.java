package com.portable.server.model.contest;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author shiroha
 */
@Data
@Builder
public class ContestRankItem implements Comparable<ContestRankItem> {

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 用户的 id
     */
    private Long userId;

    /**
     * 总共的惩罚时间（秒）
     */
    private Long totalCost;

    /**
     * 总共的解决问题数量
     */
    private Integer totalSolve;

    /**
     * 用户提交的题目的信息
     */
    private Map<Integer, ContestRankProblemStatus> submitStatus;

    /**
     * 添加评测
     * @param solution 提交的信息
     * @param problemIndex 比赛的序号
     * @param startTime 比赛开始时间
     * @param freezeTime 比赛冻结时间
     * @param penaltyCost 单次惩罚时间
     */
    public void addSolution(Solution solution, Integer problemIndex, Date startTime, Date freezeTime, Integer penaltyCost) {
        ContestRankProblemStatus contestRankProblemStatus;
        if (!submitStatus.containsKey(problemIndex)) {
            contestRankProblemStatus = ContestRankProblemStatus.builder()
                    .firstSolveId(null)
                    .solveTime(null)
                    .penaltyTimes(0)
                    .runningSubmit(0)
                    .build();
            submitStatus.put(problemIndex, contestRankProblemStatus);
        } else {
            contestRankProblemStatus = submitStatus.get(problemIndex);
        }
        Long penalty = contestRankProblemStatus.add(solution, startTime, freezeTime, penaltyCost);
        if (penalty == null) {
            return;
        }
        totalCost += penalty;
        ++totalSolve;
    }

    @Override
    public int compareTo(ContestRankItem o) {
        if (Objects.equals(totalSolve, o.getTotalSolve())) {
            return Long.compare(totalCost, o.getTotalCost());
        }
        return Long.compare(totalSolve, o.getTotalSolve());
    }

    @Data
    @Builder
    public static class ContestRankProblemStatus {

        /**
         * 第一次解决此问题的 solution id
         */
        private Long firstSolveId;

        /**
         * 解决此题目经过的时间（秒）
         */
        private Long solveTime;

        /**
         * 总共结束评测的提交的且不包含不惩罚的次数
         */
        private Integer penaltyTimes;

        /**
         * 未结束评测以及冻结榜单时的提交数量
         */
        private Integer runningSubmit;

        /**
         * 添加评测
         * @param solution 提交的信息
         * @param startTime 比赛开始时间
         * @param freezeTime 比赛冻结时间
         * @param penaltyCost 单次惩罚时间
         * @return 惩罚添加的时间，若不需要惩罚，则返回 null
         */
        @Nullable
        public Long add(Solution solution, Date startTime, Date freezeTime, Integer penaltyCost) {
            if (freezeTime.before(solution.getSubmitTime())) {
                runningSubmit++;
            } else {
                if (!solution.getStatus().getEndingResult()) {
                    runningSubmit++;
                } else if (solution.getStatus().getPenalty()) {
                    penaltyTimes++;
                } else if (SolutionStatusType.ACCEPT.equals(solution.getStatus())) {
                    if (firstSolveId != null) {
                        firstSolveId = solution.getId();
                        solveTime = (solution.getSubmitTime().getTime() - startTime.getTime()) / 1000;
                        return ((long) penaltyTimes * penaltyCost) + solveTime;
                    }
                }
            }
            return null;
        }
    }
}
