package com.portable.server.model.judge.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class UpdateJudgeContainer {

    /**
     * 目标 Judge 的 judgeCode
     */
    private String judgeCode;

    /**
     * 线程池核心线程数
     */
    private Integer maxThreadCore;

    /**
     * 任务池核心线程数
     */
    private Integer maxWorkCore;

    /**
     * 连接池最大连接数
     */
    private Integer maxSocketCore;

    /**
     * 同步最大任务数量
     */
    private Integer maxWorkNum;

    public void toJudgeContainer(JudgeContainer judgeContainer) {
        judgeContainer.setMaxThreadCore(this.maxThreadCore);
        judgeContainer.setMaxWorkCore(this.maxWorkCore);
        judgeContainer.setMaxSocketCore(this.maxSocketCore);
        judgeContainer.setMaxWorkNum(this.maxWorkNum);
        judgeContainer.setIsNewCore(true);
    }
}
