package com.portable.server.model.contest;

import com.portable.server.type.ContestAccessType;
import lombok.Builder;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import java.util.Calendar;
import java.util.Date;

/**
 * @author shiroha
 */
@Data
@Builder
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

    public Boolean isStarted() {
        return !startTime.after(new Date());
    }

    public Boolean isEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.MINUTE, duration);
        return !calendar.getTime().after(new Date());
    }
}
