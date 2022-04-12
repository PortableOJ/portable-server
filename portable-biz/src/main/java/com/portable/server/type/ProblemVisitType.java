package com.portable.server.type;


import com.portable.server.model.contest.Contest;
import com.portable.server.model.problem.Problem;
import com.portable.server.util.UserContext;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author shiroha
 */
@Getter
public enum ProblemVisitType {

    /**
     * 无访问权限
     */
    NO_ACCESS(false, false),

    /**
     * 查看与提交
     */
    VIEW(true, false),

    /**
     * 完全的访问权限
     */
    FULL_ACCESS(true, true),
    ;

    private final Boolean viewProblem;
    private final Boolean editProblem;

    ProblemVisitType(Boolean viewProblem, Boolean editProblem) {
        this.viewProblem = viewProblem;
        this.editProblem = editProblem;
    }

    public static ProblemVisitType of(@NotNull Problem problem, @Nullable Contest contest) {
        if (UserContext.ctx().getPermissionTypeSet().contains(PermissionType.CREATE_AND_EDIT_PROBLEM)) {
            // 题目拥有者拥有完整权限
            if (Objects.equals(problem.getOwner(), UserContext.ctx().getId())) {
                return FULL_ACCESS;
            }

            // 题目第一次绑定的比赛的拥有者，在比赛结束前拥有完整权限
            if (contest != null && Objects.equals(contest.getOwner(), UserContext.ctx().getId()) && !contest.isEnd()) {
                return FULL_ACCESS;
            }
        }

        ProblemVisitType resultAccessType;
        switch (problem.getAccessType()) {
            case PUBLIC:
                resultAccessType = VIEW;
                break;
            case HIDDEN:
                if (UserContext.ctx().isLogin()
                        && UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_HIDDEN_PROBLEM)) {
                    resultAccessType = VIEW;
                } else {
                    resultAccessType = NO_ACCESS;
                }
                break;
            case PRIVATE:
            default:
                return NO_ACCESS;
        }

        if (ProblemVisitType.VIEW.equals(resultAccessType)
                && UserContext.ctx().isLogin()
                && UserContext.ctx().getPermissionTypeSet().contains(PermissionType.EDIT_NOT_OWNER_PROBLEM)) {
            return FULL_ACCESS;
        }
        return resultAccessType;
    }
}
