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
     * 连接池最大连接数
     */
    private Integer maxSocketCore;

    /**
     * 是否是新的 thread core 或者 socket core 值
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
     * 所有的 socket
     */
    private Set<String> sockets;

    /**
     * 正在进行中的 judge 任务
     */
    private Map<Long, SolutionJudgeWork> judgeWorkMap;

    /**
     * 正在进行中的 test 任务
     */
    private Map<Long, TestJudgeWork> testWorkMap;
}
