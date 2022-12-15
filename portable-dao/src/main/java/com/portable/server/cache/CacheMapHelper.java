package com.portable.server.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * 键值映射缓存
 *
 * @author shiroha
 */
public interface CacheMapHelper<K> {

    /// region get

    /**
     * 获取 map 的大小，为空或者不存在则返回 0
     *
     * @param key 缓存的 key
     * @return 缓存的 map 大小
     */
    @NotNull Integer getMapSize(K key);

    /**
     * 获取一个 map ，若不存在则返回一个空 map
     *
     * @param key    缓存的 key
     * @param vClass 缓存 map 的 value 类型
     * @param <V>    缓存 map 的 value 类型
     * @return 缓存的 map
     */
    @NotNull <V> Map<String, V> getMap(K key, Class<V> vClass);

    /**
     * 获取一个 map 的一部分 key
     *
     * @param key    缓存的 key
     * @param vClass 缓存 map 的 value 类型
     * @param keys   需要的 key 列表
     * @param <V>    缓存 map 的 value 类型
     * @return 缓存的 map
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <S, V> Map<S, V> getSubMap(K key, Class<V> vClass, S... keys) {
        return this.getSubMap(key, Arrays.asList(keys), vClass);
    }

    /**
     * 获取一个 map 的一部分 key
     *
     * @param key    缓存的 key
     * @param vClass 缓存 map 的 value 类型
     * @param keys   需要的 key 列表
     * @param <V>    缓存 map 的 value 类型
     * @return 缓存的 map
     */
    @NotNull <S, V> Map<S, V> getSubMap(K key, Collection<S> keys, Class<V> vClass);

    /**
     * 获取一个 map 的一个 key
     *
     * @param key    缓存的 key
     * @param vClass 缓存 map 的 value 类型
     * @param subKey 需要的 key
     * @param <V>    缓存 map 的 value 类型
     * @return 缓存的 map
     */
    @NotNull <S, V> Map<S, V> getMapItem(K key, S subKey, Class<V> vClass);

    /// endregion

    /// region set

    /**
     * 设置一个 map，如果已经存在则覆盖
     *
     * @param key      缓存的 key
     * @param valueMap 需要缓存的值
     * @param <V>      缓存的 map 的 value 类型
     */
    <S, V> void setMap(K key, Map<S, V> valueMap);

    /**
     * 设置一个 map 的部分值，如果存在则覆盖，如果不存在则新增
     *
     * @param key      缓存的 key
     * @param valueMap 需要缓存的值
     * @param <V>      缓存的 map 的 value 类型
     */
    <S, V> void setMapItem(K key, Map<S, V> valueMap);

    /**
     * 设置一个 map 的部分值，如果存在则覆盖，如果不存在则新增
     *
     * @param key    缓存的 key
     * @param subKey 需要缓存的值的 key
     * @param value  需要缓存的值
     * @param <V>    缓存的 map 的 value 类型
     */
    <S, V> void setMapItem(K key, S subKey, V value);

    /// endregion

    /**
     * 删除整个缓存的 hash 结构
     *
     * @param key 缓存的 key
     */
    void deleteAll(K key);

    /**
     * 删除一个缓存的 hash 中多个值
     *
     * @param key     缓存的 key
     * @param subKeys 需要删除的 sub key
     */
    <S> void deleteItem(K key, S... subKeys);
}
