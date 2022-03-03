package com.portable.server.model.response.contest;

import com.portable.server.model.contest.ContestRankItem;
import com.portable.server.model.contest.ContestRankProblemStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author shiroha
 */
@Data
@NoArgsConstructor
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
    private Long totalCost;

    /**
     * 总共的解决问题数量
     */
    private Integer totalSolve;

    /**
     * 用户提交的题目的信息
     */
    private Map<Integer, ContestRankProblemStatus> submitStatus;

    ContestRankListResponse(ContestRankItem contestRankItem, String userHandle) {
        this.rank = contestRankItem.getRank();
        this.userHandle = userHandle;
        this.totalCost = contestRankItem.getTotalCost();
        this.totalSolve = contestRankItem.getTotalSolve();
        this.submitStatus = contestRankItem.getSubmitStatus();
    }

    public static ContestRankListResponse of(ContestRankItem contestRankItem, String userHandle) {
        return new ContestRankListResponse(contestRankItem, userHandle);
    }
}
