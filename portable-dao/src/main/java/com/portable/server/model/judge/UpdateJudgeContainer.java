package com.portable.server.model.judge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJudgeContainer {

    /**
     * 目标 Judge 的 judgeCode
     */
    private String judgeCode;

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
        judgeContainer.setMaxWorkCore(this.maxWorkCore);
        judgeContainer.setMaxSocketCore(this.maxSocketCore);
        judgeContainer.setMaxWorkNum(this.maxWorkNum);
        judgeContainer.setUpdated(true);
    }
}
