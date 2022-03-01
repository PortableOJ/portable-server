package com.portable.server.kit.impl;

import com.portable.server.model.RedisKeyAndExpire;
import com.portable.server.kit.RedisKit;
import com.portable.server.util.JsonUtils;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author shiroha
 */
@Component
public class RedisKitImpl implements RedisKit {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> redisValueOperation;

    @PostConstruct
    public void init() {
        redisValueOperation = stringRedisTemplate.opsForValue();
    }

    @Override
    public <T> void set(String prefix, String key, T data, Long time) {
        redisValueOperation.set(getKey(prefix, key), JsonUtils.toString(data), time, TimeUnit.SECONDS);
    }

    @Override
    public <T> Optional<T> get(String prefix, String key, Class<T> clazz) {
        String value = redisValueOperation.get(getKey(prefix, key));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(JsonUtils.toObject(value, clazz));
    }

    @Override
    public <T> RedisKeyAndExpire<T> getValueAndTime(String prefix, String key, Class<T> clazz) {
        BoundValueOperations<String, String> boundValueOperations = stringRedisTemplate.boundValueOps(getKey(prefix, key));
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(getKey(prefix, key)))) {
            String value = boundValueOperations.get();
            Long expire = boundValueOperations.getExpire();
            return RedisKeyAndExpire.<T>builder()
                    .data(JsonUtils.toObject(value, clazz))
                    .hasKey(true)
                    .expireTime(expire)
                    .build();
        }
        return RedisKeyAndExpire.<T>builder()
                .data(null)
                .hasKey(false)
                .expireTime(0L)
                .build();
    }

    @Override
    public RedisKeyAndExpire<String> getValueAndTime(String prefix, String key) {
        BoundValueOperations<String, String> boundValueOperations = stringRedisTemplate.boundValueOps(getKey(prefix, key));
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(getKey(prefix, key)))) {
            String value = boundValueOperations.get();
            Long expire = boundValueOperations.getExpire();
            return RedisKeyAndExpire.<String>builder()
                    .data(value)
                    .hasKey(true)
                    .expireTime(expire)
                    .build();
        }
        return RedisKeyAndExpire.<String>builder()
                .data(null)
                .hasKey(false)
                .expireTime(0L)
                .build();
    }

    private String getKey(String prefix, String key) {
        return String.format("%s_%s", prefix, key);
    }
}
