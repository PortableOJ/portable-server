package com.portable.server.cache;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

/**
 * 基本类型映射
 *
 * @author shiroha
 */
public interface CacheKvHelper<K> {

    /// region get

    /**
     * 获取缓存
     *
     * @param key   缓存的 key
     * @param clazz 缓存的类型
     * @return 缓存值
     */
    <T> Optional<T> get(K key, Class<T> clazz);

    /**
     * 获取多个缓存，返回一个列表，每一项依次映射，如果不存在，则对应项为 null
     *
     * @param clazz 缓存的类型
     * @param keys  缓存的 key 列表
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    @NotNull <T> List<T> multiGet(Class<T> clazz, K... keys);

    /// endregion

    /// region set

    /**
     * 设置值，并设置默认过期时间
     *
     * @param key   缓存的 key
     * @param value 需要缓存的值
     */
    <T> void set(K key, T value);

    /**
     * 设置值，并设置过期时间
     *
     * @param key      缓存的 key
     * @param value    需要缓存的值
     * @param duration 缓存时间
     */
    <T> void set(K key, T value, Duration duration);

    /// endregion

    /**
     * 删除某个缓存值
     *
     * @param key 缓存值
     */
    void delete(K key);
}
