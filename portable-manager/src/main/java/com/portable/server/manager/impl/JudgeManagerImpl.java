package com.portable.server.manager.impl;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import com.portable.server.cache.CacheKvHelper;
import com.portable.server.manager.JudgeManager;
import com.portable.server.model.judge.JudgeContainer;
import com.portable.server.model.judge.ServerCode;
import com.portable.server.persistent.StructuredHelper;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class JudgeManagerImpl implements JudgeManager {

    /**
     * 评测服务器 code 缓存 key
     */
    private static final String CODE = "CODE";

    @Resource(name = "judgeCacheKvHelper")
    private CacheKvHelper<String> judgeCacheKvHelper;

    @Resource(name = "judgeRepo")
    private StructuredHelper<JudgeContainer, String> judgeRepo;

    @Override
    public ServerCode getSeverCode() {
        return judgeCacheKvHelper.get(CODE, ServerCode.class).orElse(null);
    }

    @Override
    public void setSeverCode(ServerCode severCode) {
        Duration ttl = Duration.ofMillis(severCode.getExpireTime() - System.currentTimeMillis());
        judgeCacheKvHelper.set(CODE, severCode, ttl);
    }

    @Override
    public @NotNull List<JudgeContainer> getAllJudgeContainer() {
        return judgeRepo.searchList(judgeContainer -> true);
    }

    @Override
    public Optional<JudgeContainer> getJudgeContainerById(String code) {
        return judgeRepo.getDataById(code);
    }

    @Override
    public void insertJudgeContainerWithTtl(JudgeContainer judgeContainer, Duration ttl) {
        judgeRepo.insert(judgeContainer, l -> UUID.randomUUID().toString());
    }

    @Override
    public void updateJudgeContainer(JudgeContainer judgeContainer) {
        judgeRepo.updateById(judgeContainer);
    }

    @Override
    public void resetExpireTime(String code) {
        // TODO: 目前使用了内存级的持久存储来保存，故暂时不实现此方法
    }

    @Override
    public void removeJudgeContainer(String code) {
        judgeRepo.removeById(code);
    }
}
