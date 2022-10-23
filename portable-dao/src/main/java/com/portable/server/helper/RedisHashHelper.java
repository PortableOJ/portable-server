package com.portable.server.helper;

import java.util.Map;
import java.util.Optional;

/**
 * @author shiroha
 */
public interface RedisHashHelper {

    /**
     * 创建 redis hash
     * @param prefix 前缀
     * @param key key
     * @param map data
     */
    void create(String prefix, Object key, Map<Long, Integer> map);

    /**
     * 获取值
     * @param prefix 前缀
     * @param key key
     * @param index 索引
     * @return 值
     */
    Optional<Integer> get(String prefix, Object key, Long index);

    /**
     * 清空 hash
     * @param prefix 前缀
     * @param key key
     */
    void clear(String prefix, Object key);
}
