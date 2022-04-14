package com.portable.server.model.response.contest;

import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.user.User;
import com.portable.server.type.ContestAccessType;
import com.portable.server.util.ObjectUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Data
@NoArgsConstructor
public class ContestInfoResponse {

    /**
     * 数据库主键
     */
    private Long id;

    /**
     * 比赛标题
     */
    private String title;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 持续时间（分钟）
     */
    private Integer duration;

    /**
     * 访问权限
     */
    private ContestAccessType accessType;

    /**
     * 作者 handle
     */
    private String ownerHandle;

    /**
     * 共同的出题人的昵称
     */
    private Set<String> coAuthor;

    /**
     * 封榜时长
     */
    private Integer freezeTime;

    /**
     * 惩罚时间（分钟）
     */
    private Integer penaltyTime;

    /**
     * 公告
     */
    private String announcement;

    ContestInfoResponse(@NotNull Contest contest,
                        @NotNull BaseContestData contestData,
                        @Nullable User owner,
                        @NotNull Set<User> coAuthor) {
        this.id = contest.getId();
        this.title = contest.getTitle();
        this.startTime = contest.getStartTime();
        this.duration = contest.getDuration();
        this.accessType = contest.getAccessType();
        this.ownerHandle = owner == null ? "" : owner.getHandle();
        this.coAuthor = coAuthor.stream()
                .parallel()
                .filter(ObjectUtils::isNotNull)
                .map(User::getHandle)
                .collect(Collectors.toSet());
        this.freezeTime = contestData.getFreezeTime();
        this.penaltyTime = contestData.getPenaltyTime();
        this.announcement = contestData.getAnnouncement();
    }

    public static ContestInfoResponse of(@NotNull Contest contest,
                                         @NotNull BaseContestData contestData,
                                         @Nullable User owner,
                                         @NotNull Set<User> coAuthor) {
        return new ContestInfoResponse(contest, contestData, owner, coAuthor);
    }
}
