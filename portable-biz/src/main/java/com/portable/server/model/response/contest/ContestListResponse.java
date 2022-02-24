package com.portable.server.model.response.contest;

import com.portable.server.model.contest.Contest;
import com.portable.server.type.ContestAccessType;
import lombok.Data;

import java.util.Date;

/**
 * @author shiroha
 */
@Data
public class ContestListResponse {

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
     * 是否已经认证过了，即不需要输入认证信息
     */
    private Boolean isCertified;

    private ContestListResponse(Contest contest) {
        this.id = contest.getId();
        this.title = contest.getTitle();
        this.startTime = contest.getStartTime();
        this.duration = contest.getDuration();
        this.accessType = contest.getAccessType();
    }

    public static ContestListResponse of(Contest contest) {
        return new ContestListResponse(contest);
    }
}
