package com.portable.server.kit.impl;

import com.portable.server.kit.RedisValueKit;
import com.portable.server.model.RedisKeyAndExpire;
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
public class RedisValueKitImpl extends BaseRedisKit implements RedisValueKit {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> redisValueOperation;

    @PostConstruct
    public void init() {
        redisValueOperation = stringRedisTemplate.opsForValue();
    }

    @Override
    public void set(String prefix, Object key, Long data, Long time) {
        redisValueOperation.set(getKey(prefix, key), String.valueOf(data), time, TimeUnit.SECONDS);
    }

    @Override
    public void set(String prefix, Object key, String data, Long time) {
        redisValueOperation.set(getKey(prefix, key), data, time, TimeUnit.SECONDS);
    }

    @Override
    public <T> void set(String prefix, Object key, T data, Long time) {
        redisValueOperation.set(getKey(prefix, key), JsonUtils.toString(data), time, TimeUnit.SECONDS);
    }

    @Override
    public <T> Optional<T> get(String prefix, Object key, Class<T> clazz) {
        String value = redisValueOperation.get(getKey(prefix, key));
        if (value == null) {
            return Optional.empty();
        }
        Object res;
        if (Integer.class.equals(clazz)) {
            res = Integer.valueOf(value);
        } else if (Long.class.equals(clazz)) {
            res = Long.valueOf(value);
        } else if (Double.class.equals(clazz)) {
            res = Double.valueOf(value);
        } else if (String.class.equals(clazz)) {
            res = value;
        } else {
            res = JsonUtils.toObject(value, clazz);
        }
        return Optional.ofNullable(clazz.cast(res));
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
}
