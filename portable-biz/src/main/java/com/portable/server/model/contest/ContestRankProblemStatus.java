package com.portable.server.model.contest;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContestRankProblemStatus {

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
     * 是否是首个 Accept
     */
    private Boolean firstBlood;

    /**
     * 添加评测
     *
     * @param solution   提交的信息
     * @param startTime  比赛开始时间
     * @param freezeTime 比赛冻结时间
     * @param freeze     是否启用封榜
     */
    public void add(@NotNull Solution solution, @NotNull Date startTime, @NotNull Date freezeTime, @NotNull Boolean freeze) {
        if (freeze && freezeTime.before(solution.getSubmitTime())) {
            runningSubmit++;
        } else {
            if (!solution.getStatus().getEndingResult()) {
                runningSubmit++;
            } else if (solution.getStatus().getPenalty()) {
                penaltyTimes++;
            } else if (SolutionStatusType.ACCEPT.equals(solution.getStatus())) {
                // 榜单是从后往前的，所以当遇到了一个 ac 的题目之后，
                // 说明之前的统计信息都是这次 ac 之后的提交，不应该被计算在内
                firstSolveId = solution.getId();
                solveTime = (solution.getSubmitTime().getTime() - startTime.getTime()) / 1000;
                penaltyTimes = 0;
                runningSubmit = 0;
            }
        }
    }

    public void setFirstBlood() {
        if (Objects.nonNull(firstSolveId)) {
            firstBlood = true;
        }
    }

    public static ContestRankProblemStatus newProblem() {
        return ContestRankProblemStatus.builder()
                .firstSolveId(null)
                .solveTime(null)
                .penaltyTimes(0)
                .runningSubmit(0)
                .firstBlood(false)
                .build();
    }
}
