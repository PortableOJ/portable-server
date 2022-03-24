package com.portable.server.model.judge.entity;

import com.portable.server.model.judge.work.SolutionJudgeWork;
import com.portable.server.model.judge.work.TestJudgeWork;
import com.portable.server.model.response.judge.HeartbeatResponse;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
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

    /**
     * 需要删除的问题 cache 列表
     */
    private List<Long> needDeleteProblemCacheIdList;

    /**
     * 需要删除的问题 judge 列表
     */
    private List<Long> needDeleteProblemJudgeIdList;

    /**
     * 关闭此 judge
     */
    private Boolean terminal;

    public synchronized void addDeleteProblemCacheId(Long problemId) {
        needDeleteProblemCacheIdList.add(problemId);
    }

    public synchronized void addDeleteProblemJudgeId(Long problemId) {
        needDeleteProblemJudgeIdList.add(problemId);
    }

    public synchronized void dump(HeartbeatResponse heartbeatResponse) {
        needDeleteProblemCacheIdList.forEach(heartbeatResponse::addCleanProblem);
        needDeleteProblemCacheIdList.clear();
        needDeleteProblemJudgeIdList.forEach(heartbeatResponse::addCleanJudge);
        needDeleteProblemJudgeIdList.clear();
    }
}
