package com.portable.server.time;

import com.portable.server.constant.Constant;

import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class Interval {

    /**
     * 间隔时间，毫秒
     */
    private Long time;

    public static Interval ofMillisecond(Integer millisecond) {
        return ofMillisecond(millisecond * Constant.MILLISECOND);
    }

    public static Interval ofMillisecond(Long millisecond) {
        return Interval.builder()
                .time(millisecond)
                .build();
    }

    public static Interval ofSecond(Integer second) {
        return ofMillisecond(second * Constant.SECOND);
    }

    public static Interval ofSecond(Long second) {
        return ofMillisecond(second * Constant.SECOND);
    }

    public static Interval ofMinute(Integer minute) {
        return ofMillisecond(minute * Constant.MINUTE);
    }

    public static Interval ofMinute(Long minute) {
        return ofMillisecond(minute * Constant.MINUTE);
    }

    public static Interval ofHour(Integer hour) {
        return ofMillisecond(hour * Constant.HOUR);
    }

    public static Interval ofHour(Long hour) {
        return ofMillisecond(hour * Constant.HOUR);
    }

    public Long toMillisecond() {
        return time;
    }

    public Long toSecond() {
        return time / Constant.SECOND;
    }

    public Long toMinute() {
        return time / Constant.MINUTE;
    }

    public Long toHour() {
        return time / Constant.HOUR;
    }
}
