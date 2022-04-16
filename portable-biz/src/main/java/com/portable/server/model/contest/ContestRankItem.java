package com.portable.server.model.contest;

import com.portable.server.model.solution.Solution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
    public void addSolution(@NotNull Solution solution, @NotNull Integer problemIndex, @NotNull Date startTime, @NotNull Date freezeTime) {
        ContestRankProblemStatus freezeStatus;
        ContestRankProblemStatus noFreezeStatus;
        if (!submitStatus.containsKey(problemIndex)) {
            freezeStatus = ContestRankProblemStatus.newProblem();
            noFreezeStatus = ContestRankProblemStatus.newProblem();
            submitStatus.put(problemIndex, freezeStatus);
            noFreezeSubmitStatus.put(problemIndex, noFreezeStatus);
        } else {
            freezeStatus = submitStatus.get(problemIndex);
            noFreezeStatus = noFreezeSubmitStatus.get(problemIndex);
        }
        freezeStatus.add(solution, startTime, freezeTime, true);
        noFreezeStatus.add(solution, startTime, freezeTime, false);
    }

    /**
     * 将某一题目设置为首 A
     *
     * @param problemIndex 题目序号
     */
    public void setFirstBlood(@NotNull Integer problemIndex) {
        submitStatus.get(problemIndex).setFirstBlood();
        noFreezeSubmitStatus.get(problemIndex).setFirstBlood();
    }

    /**
     * 计算耗时
     *
     * @param penaltyTime 惩罚时间(分钟)
     * @param freeze      是否使用冻结榜单后的数据
     */
    public void calCost(@NotNull Integer penaltyTime, @NotNull Boolean freeze) {
        Map<Integer, ContestRankProblemStatus> statusMap = freeze ? submitStatus : noFreezeSubmitStatus;
        totalSolve = 0;
        totalCost = statusMap.values().stream()
                .map(contestRankProblemStatus -> {
                    if (contestRankProblemStatus.getFirstSolveId() == null) {
                        return 0L;
                    }
                    totalSolve++;
                    // 惩罚是分钟值，需要再 * 60 变成秒
                    return contestRankProblemStatus.getSolveTime() + (long) contestRankProblemStatus.getPenaltyTimes() * penaltyTime * 60;
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

    public static ContestRankItem newItem(Long userId) {
        return ContestRankItem.builder()
                .rank(0).userId(userId).totalCost(0L).totalSolve(0)
                .submitStatus(new ConcurrentHashMap<>(0))
                .noFreezeSubmitStatus(new ConcurrentHashMap<>(0))
                .build();
    }
}
