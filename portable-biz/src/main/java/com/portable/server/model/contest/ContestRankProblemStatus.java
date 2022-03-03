package com.portable.server.model.contest;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Date;

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
     * 添加评测
     *
     * @param solution    提交的信息
     * @param startTime   比赛开始时间
     * @param freezeTime  比赛冻结时间
     */
    @Nullable
    public void add(Solution solution, Date startTime, Date freezeTime) {
        if (freezeTime.before(solution.getSubmitTime())) {
            runningSubmit++;
        } else {
            if (!solution.getStatus().getEndingResult()) {
                runningSubmit++;
            } else if (solution.getStatus().getPenalty()) {
                penaltyTimes++;
            } else if (SolutionStatusType.ACCEPT.equals(solution.getStatus())) {
                firstSolveId = solution.getId();
                solveTime = (solution.getSubmitTime().getTime() - startTime.getTime()) / 1000;
                penaltyTimes = 0;
                runningSubmit = 0;
            }
        }
    }
}
