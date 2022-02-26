package com.portable.server.model.response.contest;

import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.type.ContestAccessType;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author shiroha
 */
@Data
public class ContestDetailResponse {

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
     * 题目列表
     */
    private List<ProblemListResponse> problemList;

    /**
     * 共同的出题人的昵称
     */
    private Set<String> coAuthor;

    /**
     * 封榜时长
     */
    private Integer freezeTime;

    /**
     * 公告
     */
    private String announcement;

    ContestDetailResponse(Contest contest, BaseContestData contestData, String ownerHandle, List<ProblemListResponse> problemList, Set<String> coAuthor) {
        this.id = contest.getId();
        this.title = contest.getTitle();
        this.startTime = contest.getStartTime();
        this.duration = contest.getDuration();
        this.accessType = contest.getAccessType();
        this.ownerHandle = ownerHandle;
        this.problemList = problemList;
        this.coAuthor = coAuthor;
        this.freezeTime = contestData.getFreezeTime();
        this.announcement = contestData.getAnnouncement();
    }

    public static ContestDetailResponse of(Contest contest, BaseContestData contestData, String ownerHandle, List<ProblemListResponse> problemList, Set<String> coAuthor) {
        return new ContestDetailResponse(contest, contestData, ownerHandle, problemList, coAuthor);
    }
}
