package com.portable.server.helper.impl;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.portable.server.helper.RedisValueHelper;
import com.portable.server.model.redis.RedisKeyAndExpire;
import com.portable.server.util.JsonUtils;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class RedisValueHelperImpl extends BaseRedisKit implements RedisValueHelper {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> redisValueOperation;

    @PostConstruct
    public void init() {
        redisValueOperation = stringRedisTemplate.opsForValue();
    }

    @Override
    public void set(String prefix, Object key, Long data, Long time) {
        redisValueOperation.set(getKey(prefix, key), String.valueOf(data), time, TimeUnit.MINUTES);
    }

    @Override
    public void set(String prefix, Object key, String data, Long time) {
        redisValueOperation.set(getKey(prefix, key), data, time, TimeUnit.MINUTES);
    }

    @Override
    public <T> void set(String prefix, Object key, T data, Long time) {
        redisValueOperation.set(getKey(prefix, key), JsonUtils.toString(data), time, TimeUnit.MINUTES);
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
    public <T> void getPeek(String prefix, Object key, Class<T> clazz, Long time, Consumer<T> consumer) {
        Optional<T> tOptional = get(prefix, key, clazz);
        if (tOptional.isPresent()) {
            consumer.accept(tOptional.get());
            set(prefix, key, tOptional.get(), time);
        }
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
