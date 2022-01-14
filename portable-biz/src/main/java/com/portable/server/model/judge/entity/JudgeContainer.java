package com.portable.server.model.judge.entity;

import com.portable.server.model.judge.work.SolutionJudgeWork;
import com.portable.server.model.judge.work.TestJudgeWork;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author shiroha
 */
@Data
@Builder
public class JudgeContainer {

    /**
     * 此 Judge 的 judgeCode
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
     * 线程池堆积任务数量
     */
    private Integer threadAccumulation;

    /**
     * 任务池堆积任务数量
     */
    private Integer workAccumulation;

    /**
     * 连接池堆积任务数量
     */
    private Integer socketAccumulation;

    /**
     * 是否更新了的 thread core 或者 socket core 或者 work core 的值
     */
    private Boolean isNewCore;

    /**
     * 同步最大任务数量
     */
    private Integer maxWorkNum;

    /**
     * 最后一次心跳包
     */
    private Date lastHeartbeat;

    /**
     * 正在进行中的 judge 任务
     */
    private Map<Long, SolutionJudgeWork> judgeWorkMap;

    /**
     * 正在进行中的 test 任务
     */
    private Map<Long, TestJudgeWork> testWorkMap;

    /**
     * 所有连接的 TCP
     */
    private Set<String> tcpAddressSet;
}
