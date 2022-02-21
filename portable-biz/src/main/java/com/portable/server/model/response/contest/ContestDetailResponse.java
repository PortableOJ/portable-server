package com.portable.server.model.response.contest;

import com.portable.server.type.ContestAccessType;
import com.portable.server.type.ProblemAccessType;
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
    private List<Long> problemList;

    /**
     * 共同的出题人
     */
    private Set<Long> coAuthor;

    /**
     * 封榜时长
     */
    private Long freezeTime;

    /**
     * 公告
     */
    private String announcement;
}
