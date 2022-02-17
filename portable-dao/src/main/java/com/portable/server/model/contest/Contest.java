package com.portable.server.model.contest;

import com.portable.server.type.ContestAccessType;
import lombok.Data;

import java.util.Date;

/**
 * @author shiroha
 */
@Data
public class Contest {

    /**
     * 数据库主键
     */
    private Long id;

    /**
     * 数据主键
     */
    private String dataId;

    /**
     * 所有者的 id
     */
    private Long owner;

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
}
