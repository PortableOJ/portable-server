package com.portable.server.kit.impl;

import com.portable.server.kit.RedisListKit;
import com.portable.server.util.JsonUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class RedisListKitImpl extends BaseRedisKit implements RedisListKit {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ListOperations<String, String> redisListOperation;

    @PostConstruct
    public void init() {
        redisListOperation = stringRedisTemplate.opsForList();
    }

    @Override
    public <T> void create(String prefix, Object key, List<T> data) {
        List<String> stringList = data.stream().map(JsonUtils::toString).collect(Collectors.toList());
        redisListOperation.rightPushAll(getKey(prefix, key), stringList);
    }

    @Override
    public <T> Optional<T> get(String prefix, Object key, Integer index, Class<T> clazz) {
        String value = redisListOperation.index(getKey(prefix, key), index);
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(JsonUtils.toObject(value, clazz));
    }

    @Override
    public Integer getLen(String prefix, Object key) {
        Long len = redisListOperation.size(getKey(prefix, key));
        if (len == null) {
            return 0;
        }
        return Math.toIntExact(len);
    }

    @NonNull
    @Override
    public <T> List<T> getPage(String prefix, Object key, Integer pageSize, Integer offset, Class<T> clazz) {
        List<String> stringList = redisListOperation.range(getKey(prefix, key), offset, offset + pageSize - 1);
        if (stringList == null) {
            return new ArrayList<>();
        }
        return stringList.stream()
                .parallel()
                .map(s -> JsonUtils.toObject(s, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String prefix, Object key) {
        redisListOperation.trim(getKey(prefix, key), 1, 0);
    }
}
