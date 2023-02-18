package com.portable.server.service;

import java.util.List;

import com.portable.server.model.judge.JudgeContainer;
import com.portable.server.model.judge.ServerCode;
import com.portable.server.model.judge.UpdateJudgeContainer;

/**
 * @author shiroha
 */
public interface JudgeMaintenanceService {

    /**
     * 获取 server code
     *
     * @return 返回当前的 server code
     */
    ServerCode getServiceCode();

    /**
     * 获取所有 Judge 信息
     * @return 所有 Judge 信息
     */
    List<JudgeContainer> getJudgeContainerList();

    /**
     * 更新 judge 容器配置
     *
     * @param updateJudgeContainer 更新后的配置
     */
    void updateJudgeContainer(UpdateJudgeContainer updateJudgeContainer);

    /**
     * 终止一个 judge 任务
     * @param solutionId 提交 ID
     */
    void killJudge(Long solutionId);

    /**
     * 终止一个 test 任务
     * @param problemId 题目 ID
     */
    void killTest(Long problemId);

    /**
     * 关闭一个 judge
     * @param judgeCode 判题机器的 code
     */
    void stopJudge(String judgeCode);
}
