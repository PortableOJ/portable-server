package com.portable.server.model.response.contest;

import com.portable.server.model.contest.ContestRankItem;
import com.portable.server.model.contest.ContestRankProblemStatus;
import com.portable.server.model.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    ContestRankListResponse(@NotNull ContestRankItem contestRankItem,
                            @Nullable User user,
                            @NotNull Boolean freeze) {
        this.rank = contestRankItem.getRank();
        this.userHandle = user == null ? "" : user.getHandle();
        this.totalCost = contestRankItem.getTotalCost();
        this.totalSolve = contestRankItem.getTotalSolve();
        this.submitStatus = freeze ? contestRankItem.getSubmitStatus() : contestRankItem.getNoFreezeSubmitStatus();
    }

    public static ContestRankListResponse of(@NotNull ContestRankItem contestRankItem,
                                             @Nullable User user,
                                             @NotNull Boolean freeze) {
        return new ContestRankListResponse(contestRankItem, user, freeze);
    }
}
