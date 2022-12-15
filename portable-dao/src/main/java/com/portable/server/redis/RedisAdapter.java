package com.portable.server.redis;

import org.jetbrains.annotations.Nullable;

/**
 * @author shiroha
 */
public interface RedisAdapter {

    /**
     * 初始化连接
     *
     * @param redisProperties
     */
    void init(RedisProperties redisProperties);

    /**
     * 从 redis 中获取值，如果不存在则返回 null
     *
     * @param key key
     * @return 值
     */
    @Nullable String get(String key);

    /**
     * 从 redis 中设置值，如果已经存在则覆盖
     *
     * @param key    key
     * @param value  新的值
     * @param expire 过期时间，秒
     */
    void set(String key, String value, Long expire);
}
