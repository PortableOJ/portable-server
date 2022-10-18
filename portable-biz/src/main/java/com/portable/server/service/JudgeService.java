package com.portable.server.service;

import java.util.List;

import com.portable.server.exception.PortableException;
import com.portable.server.model.ServiceVerifyCode;
import com.portable.server.model.judge.entity.JudgeContainer;
import com.portable.server.model.judge.entity.UpdateJudgeContainer;

/**
 * @author shiroha
 */
public interface JudgeService {

    /**
     * 获取 server code
     *
     * @return 返回当前的 server code
     */
    ServiceVerifyCode getServiceCode();

    /**
     * 在项目启动后，获取一次 server code
     *
     * @return serve code
     */
    String getTheServiceCodeFirstTime();

    /**
     * 获取所有 Judge 信息
     * @return 所有 Judge 信息
     */
    List<JudgeContainer> getJudgeContainerList();

    /**
     * 更新 judge 容器信息
     * @param updateJudgeContainer 更新后的信息
     * @throws PortableException 遇到不存在此 Judge 时抛出错误
     */
    void updateJudgeContainer(UpdateJudgeContainer updateJudgeContainer);

    /**
     * 终止一个 judge 任务
     * @param solutionId 提交 ID
     * @throws PortableException 当提交不存在时候则抛出
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
