package com.portable.server.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author shiroha
 */
public class JedisPoolAdapter implements RedisAdapter {

    private static JedisPool jedisPool;

    @Override
    public synchronized void init(RedisProperties config) {
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(config.getMaxIdle());
        poolConfig.setMinIdle(config.getMinIdle());
        poolConfig.setMaxTotal(config.getMaxIdle());

        jedisPool = new JedisPool(poolConfig, config.getHost(), config.getPort(),
                config.getTimeout(), config.getSoTimeout(), config.getPassword(), config.getPassword(),
                config.getDatabase(), config.getClientName());
    }

    @Override
    public @Nullable String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public void set(String key, String value, Long expire) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, expire, value);
        }
    }
}
