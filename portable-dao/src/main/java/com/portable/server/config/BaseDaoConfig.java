package com.portable.server.config;

import java.time.Duration;

import com.portable.server.model.task.AbstractTask;
import com.portable.server.persistent.PriorityQueueHelper;
import com.portable.server.persistent.impl.MemPriorityQueueHelper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * @author shiroha
 */
public abstract class BaseDaoConfig {

    /**
     * 默认缓存时间
     */
    protected static final Duration DEFAULT_CACHE_TIME_TO_LIVE = Duration.ofSeconds(600L);

    /**
     * 用户信息缓存 key
     */
    protected static final String USER_PREFIX = "USER";

    /**
     * 用户 handle 缓存
     */
    protected static final String USER_HANDLE_PREFIX = "USER_HANDLE";

    /**
     * 问题信息缓存 key
     */
    protected static final String PROBLEM_PREFIX = "PROBLEM";

    /**
     * 提交信息缓存 key
     */
    protected static final String SOLUTION_PREFIX = "SOLUTION";

    /**
     * 比赛信息缓存 key
     */
    protected static final String CONTEST_PREFIX = "CONTEST";

    /**
     * 批用户组信息缓存 key
     */
    protected static final String BATCH_PREFIX = "BATCH";

    /**
     * 评测系统的缓存/存储
     */
    protected static final String JUDGE_PREFIX = "JUDGE";

    @Lazy
    @Bean(name = "taskPriorityQueueHelper")
    public PriorityQueueHelper<AbstractTask> taskPriorityQueueHelper() {
        return new MemPriorityQueueHelper<>();
    }
}
