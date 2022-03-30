package com.portable.server.model.contest;

import com.portable.server.model.solution.Solution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * 不封榜的用户提交的题目信息
     */
    private Map<Integer, ContestRankProblemStatus> noFreezeSubmitStatus;

    /**
     * 添加评测
     *
     * @param solution     提交的信息
     * @param problemIndex 比赛的序号
     * @param startTime    比赛开始时间
     * @param freezeTime   比赛冻结时间
     */
    public void addSolution(Solution solution, Integer problemIndex, Date startTime, Date freezeTime) {
        ContestRankProblemStatus freezeStatus;
        ContestRankProblemStatus noFreezeStatus;
        if (!submitStatus.containsKey(problemIndex)) {
            freezeStatus = ContestRankProblemStatus.builder()
                    .firstSolveId(null)
                    .solveTime(null)
                    .penaltyTimes(0)
                    .runningSubmit(0)
                    .build();
            noFreezeStatus = ContestRankProblemStatus.builder()
                    .firstSolveId(null)
                    .solveTime(null)
                    .penaltyTimes(0)
                    .runningSubmit(0)
                    .build();
            submitStatus.put(problemIndex, freezeStatus);
            noFreezeSubmitStatus.put(problemIndex, noFreezeStatus);
        } else {
            freezeStatus = submitStatus.get(problemIndex);
            noFreezeStatus = noFreezeSubmitStatus.get(problemIndex);
        }
        freezeStatus.add(solution, startTime, freezeTime, true);
        noFreezeStatus.add(solution, startTime, freezeTime, false);
    }

    public void calCost(Integer penaltyTime, Boolean freeze) {
        Map<Integer, ContestRankProblemStatus> statusMap = freeze ? submitStatus : noFreezeSubmitStatus;
        totalSolve = 0;
        totalCost = statusMap.values().stream()
                .map(contestRankProblemStatus -> {
                    if (contestRankProblemStatus.getFirstSolveId() == null) {
                        return 0L;
                    }
                    totalSolve++;
                    return contestRankProblemStatus.getSolveTime() + (long) contestRankProblemStatus.getPenaltyTimes() * penaltyTime;
                })
                .reduce(0L, Long::sum);
    }

    @Override
    public int compareTo(ContestRankItem o) {
        if (Objects.equals(totalSolve, o.getTotalSolve())) {
            return Long.compare(totalCost, o.getTotalCost());
        }
        return Long.compare(o.getTotalSolve(), totalSolve);
    }
}
