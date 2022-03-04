package com.portable.server.kit.impl;

import com.portable.server.kit.RedisHashKit;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class RedisHashKitImpl extends BaseRedisKit implements RedisHashKit {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private HashOperations<String, Object, String> hashOperations;

    @PostConstruct
    public void init() {
        hashOperations = stringRedisTemplate.opsForHash();
    }

    @Override
    public void create(String prefix, Object key, Map<Long, Integer> map) {
        if (map.isEmpty()) {
            return;
        }
        Map<String, String> dataMap = map.entrySet().stream()
                .parallel()
                .collect(Collectors.toMap(longIntegerEntry -> longIntegerEntry.getKey().toString(),
                        longIntegerEntry -> longIntegerEntry.getValue().toString()));
        hashOperations.putAll(getKey(prefix, key), dataMap);
    }

    @Override
    public Optional<Integer> get(String prefix, Object key, Long index) {
        String value = hashOperations.get(getKey(prefix, key), index.toString());
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Integer.valueOf(value));
    }

    @Override
    public void clear(String prefix, Object key) {
        Set<Object> keySet = hashOperations.keys(getKey(prefix, key));
        if (keySet.isEmpty()) {
            return;
        }
        hashOperations.delete(getKey(prefix, key), keySet.toArray());
    }
}
