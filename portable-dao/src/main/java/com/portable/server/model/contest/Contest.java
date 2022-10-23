package com.portable.server.model.contest;

import java.util.Calendar;
import java.util.Date;

import com.portable.server.model.BaseEntity;
import com.portable.server.type.ContestAccessType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contest extends BaseEntity<Long> {

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
