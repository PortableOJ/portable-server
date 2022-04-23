package com.portable.server.type;

import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.util.UserContext;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author shiroha
 */
@Getter
public enum ContestVisitType {

    /**
     * 无任何权限
     */
    NO_ACCESS(0),

    /**
     * 仅访问权限
     */
    VISIT(1),

    /**
     * 比赛参与者，可以提交
     */
    PARTICIPANT(2),

    /**
     * 合作出题人，可以在比赛开始前添加题目，可以提交至测试列表
     */
    CO_AUTHOR(3),

    /**
     * 比赛拥有者，可以拥有题目的完整权限，提交至测试列表
     */
    ADMIN(4),
    ;

    private final Integer code;

    ContestVisitType(int code) {
        this.code = code;
    }

    @NotNull
    public Boolean approve(@NotNull ContestVisitType contestVisitType) {
        return this.code <= contestVisitType.getCode();
    }

    @NotNull
    public static ContestVisitType checkPermission(@NotNull Contest contest, @NotNull BaseContestData contestData) {
        UserContext userContext = UserContext.ctx();
        ContestVisitType contestVisitType = userContext.getContestVisitPermissionMap().get(contest.getId());
        if (contestVisitType != null) {
            return contestVisitType;
        }
        // 检查拥有者和出题人情况
        if (Objects.equals(contest.getOwner(), userContext.getId())) {
            contestVisitType = ContestVisitType.ADMIN;
        } else if (contestData.getCoAuthor().contains(userContext.getId())) {
            contestVisitType = ContestVisitType.CO_AUTHOR;
        } else {
            // 根据比赛的类型判断状态
            switch (contest.getAccessType()) {
                case PUBLIC:
                    contestVisitType = ContestVisitType.PARTICIPANT;
                    break;
                case PRIVATE:
                    PrivateContestData privateContestData = (PrivateContestData) contestData;
                    contestVisitType = privateContestData.getInviteUserSet().contains(userContext.getId())
                            ? ContestVisitType.PARTICIPANT
                            : ContestVisitType.NO_ACCESS;
                    break;
                case BATCH:
                    contestVisitType = (AccountType.BATCH.equals(userContext.getType()) && Objects.equals(userContext.getContestId(), contest.getId()))
                            ? ContestVisitType.PARTICIPANT
                            : ContestVisitType.NO_ACCESS;
                    break;
                case PASSWORD:
                default:
                    contestVisitType = ContestVisitType.NO_ACCESS;
                    break;
            }
        }
        // 根据用户所具有的权利判断
        if (userContext.getPermissionTypeSet().contains(PermissionType.EDIT_NOT_OWNER_CONTEST)) {
            contestVisitType = ContestVisitType.ADMIN;
        }
        if (!ContestVisitType.VISIT.approve(contestVisitType)
                && userContext.getPermissionTypeSet().contains(PermissionType.VIEW_ALL_CONTEST)) {
            contestVisitType = ContestVisitType.VISIT;
        }
        userContext.addContestVisit(contest.getId(), contestVisitType);
        return contestVisitType;
    }

}
