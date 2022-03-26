package com.portable.server.model.response.solution;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.user.User;
import com.portable.server.type.LanguageType;
import com.portable.server.type.SolutionStatusType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * @author shiroha
 */
@Data
public class SolutionListResponse {

    /**
     * 提交的 id
     */
    private Long id;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 提交者 id
     */
    private Long userId;

    /**
     * 提交者 handle
     */
    private String userHandle;

    /**
     * 问题的 id
     */
    private Long problemId;

    /**
     * 问题的标题
     */
    private String problemTitle;

    /**
     * 语言
     */
    private LanguageType languageType;

    /**
     * 当前状态
     */
    private SolutionStatusType status;

    /**
     * 耗时（ms）
     */
    private Integer timeCost;

    /**
     * 内存消耗（mb）
     */
    private Integer memoryCost;

    private SolutionListResponse(@NotNull Solution solution, @Nullable User user, @Nullable Problem problem) {
        this.id = solution.getId();
        this.submitTime = solution.getSubmitTime();
        this.userId = solution.getUserId();
        this.userHandle = user == null ? "" : user.getHandle();
        this.problemId = solution.getProblemId();
        this.problemTitle = problem == null ? "" : problem.getTitle();
        this.languageType = solution.getLanguageType();
        this.status = solution.getStatus();
        this.timeCost = solution.getTimeCost();
        this.memoryCost = solution.getMemoryCost();
    }

    public static SolutionListResponse of(@NotNull Solution solution, @Nullable User user, @Nullable Problem problem) {
        return new SolutionListResponse(solution, user, problem);
    }
}
