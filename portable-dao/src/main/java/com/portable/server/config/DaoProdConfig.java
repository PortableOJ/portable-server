package com.portable.server.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.portable.server.cache.CacheKvHelper;
import com.portable.server.cache.impl.RedisCacheHelper;
import com.portable.server.cache.impl.proxy.CacheKvPrefixHelper;
import com.portable.server.exception.PortableException;
import com.portable.server.model.judge.JudgeContainer;
import com.portable.server.persistent.StructuredHelper;
import com.portable.server.persistent.impl.MemStructuredHelperImpl;
import com.portable.server.redis.JedisPoolAdapter;
import com.portable.server.redis.RedisAdapter;
import com.portable.server.redis.RedisProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

/**
 * @author shiroha
 */
@Configuration
@Profile("prod")
public class DaoProdConfig extends BaseDaoConfig {

    @Resource
    private RedisProperties redisProperties;

    /**
     * redis 连接
     */
    private RedisAdapter redisAdapter;

    @PostConstruct
    private void init() {
        // TODO: 对于可能存在使用其他 db 而不是 redis 作为缓存的情况需要进行支持
        if (redisProperties.getUseCluster()) {
            throw PortableException.of("B-03-001", "暂不支持 redis 集群连接");
        } else {
            redisAdapter = new JedisPoolAdapter();
        }
        redisAdapter.init(redisProperties);
    }

    /// region 缓存层构建

    private <T> CacheKvHelper<T> buildPrefixCacheKvHelp(String prefix) {
        return new CacheKvPrefixHelper<>(prefix, DEFAULT_CACHE_TIME_TO_LIVE, new RedisCacheHelper<>(redisAdapter));
    }

    @Lazy
    @Bean("userCacheKvHelper")
    public CacheKvHelper<Long> userCacheKvHelper() {
        return this.buildPrefixCacheKvHelp(USER_PREFIX);
    }

    @Lazy
    @Bean("userHandleCacheKvHelper")
    public CacheKvHelper<String> userHandleCacheKvHelper() {
        return this.buildPrefixCacheKvHelp(USER_HANDLE_PREFIX);
    }

    @Lazy
    @Bean("problemCacheKvHelper")
    public CacheKvHelper<Long> problemCacheKvHelper() {
        return this.buildPrefixCacheKvHelp(PROBLEM_PREFIX);
    }

    @Lazy
    @Bean("solutionCacheKvHelper")
    public CacheKvHelper<Long> solutionCacheKvHelper() {
        return this.buildPrefixCacheKvHelp(SOLUTION_PREFIX);
    }

    @Lazy
    @Bean("contestCacheKvHelper")
    public CacheKvHelper<Long> contestCacheKvHelper() {
        return this.buildPrefixCacheKvHelp(CONTEST_PREFIX);
    }

    @Lazy
    @Bean("batchCacheKvHelper")
    public CacheKvHelper<Long> batchCacheKvHelper() {
        return this.buildPrefixCacheKvHelp(BATCH_PREFIX);
    }

    @Lazy
    @Bean("judgeCacheKvHelper")
    public CacheKvHelper<String> judgeCacheKvHelper() {
        return this.buildPrefixCacheKvHelp(JUDGE_PREFIX);
    }

    /// endregion

    /// region 持久层构建

    @Lazy
    @Bean("judgeRepo")
    public StructuredHelper<JudgeContainer, String> judgeRepo() {
        return new MemStructuredHelperImpl<>();
    }

    /// endregion
}
