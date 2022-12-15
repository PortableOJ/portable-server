package com.portable.server.manager;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.portable.server.model.judge.JudgeContainer;
import com.portable.server.model.judge.ServerCode;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public interface JudgeManager {

    /**
     * 获取服务端 code
     *
     * @return 服务端 code
     */
    ServerCode getSeverCode();

    /**
     * 设置服务端 code
     *
     * @param severCode 服务端 code 内容
     */
    void setSeverCode(ServerCode severCode);

    /**
     * 获取全部的容器信息
     *
     * @return 容器信息
     */
    @NotNull List<JudgeContainer> getAllJudgeContainer();

    /**
     * 根据容器 code 获取容器
     *
     * @param code 容器 code
     * @return 容器信息
     */
    Optional<JudgeContainer> getJudgeContainerById(String code);

    /**
     * 新增一个 judgeContainer
     *
     * @param judgeContainer 新的判题容器
     * @param ttl            保存时间
     */
    void insertJudgeContainerWithTtl(JudgeContainer judgeContainer, Duration ttl);

    /**
     * 更新 judgeContainer
     *
     * @param judgeContainer
     */
    void updateJudgeContainer(JudgeContainer judgeContainer);

    /**
     * 重置一个容器的保存时间
     *
     * @param code 容器 code
     */
    void resetExpireTime(String code);

    /**
     * 移除一个 judgeContainer
     *
     * @param code judgeContainer 的 code
     */
    void removeJudgeContainer(String code);
}
