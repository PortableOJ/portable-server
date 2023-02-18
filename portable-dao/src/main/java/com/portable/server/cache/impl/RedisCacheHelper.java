package com.portable.server.cache.impl;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.portable.server.cache.CacheKvHelper;
import com.portable.server.cache.CacheMapHelper;
import com.portable.server.cache.CacheRalHelper;
import com.portable.server.exception.PortableErrors;
import com.portable.server.redis.RedisAdapter;
import com.portable.server.util.JsonUtils;

import org.jetbrains.annotations.NotNull;

/**
 * 基本的 jedis 相关的配置
 *
 * @author shiroha
 */
public class RedisCacheHelper<K> implements CacheKvHelper<K>, CacheRalHelper<K>, CacheMapHelper<K> {

    private final RedisAdapter redisAdapter;

    public RedisCacheHelper(RedisAdapter redisAdapter) {
        this.redisAdapter = redisAdapter;
    }

    @Override
    public <T> Optional<T> get(K key, Class<T> clazz) {
        return Optional.ofNullable(JsonUtils.toObject(redisAdapter.get(String.valueOf(key)), clazz));
    }

    @SafeVarargs
    @Override
    public final @NotNull <T> List<T> multiGet(Class<T> clazz, K... keys) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <T> void set(K key, T value) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <T> void set(K key, T value, Duration duration) {
        redisAdapter.set(String.valueOf(key), JsonUtils.toString(value), duration.getSeconds());
    }

    @Override
    public @NotNull Integer getListLength(K key) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public @NotNull <T> List<T> getList(K key, Class<T> clazz) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public @NotNull <T> List<T> getRangeList(K key, Integer start, Integer end, Class<T> clazz) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <T> @NotNull T getAt(K key, Integer position, Class<T> clazz) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <T> void setList(K key, List<T> valueList) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <T> void setAt(K key, Integer position, List<T> valueList) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @SafeVarargs
    @Override
    public final <T> void pushBack(K key, T... values) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <T> void pushBack(K key, List<T> valueList) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public void popBack(K key) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public void popBack(K key, Integer count) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public void delete(K key) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public @NotNull Integer getMapSize(K key) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public @NotNull <V> Map<String, V> getMap(K key, Class<V> vClass) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    @SafeVarargs
    public final @NotNull <S, V> Map<S, V> getSubMap(K key, Class<V> vClass, S... keys) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public @NotNull <S, V> Map<S, V> getSubMap(K key, Collection<S> keys, Class<V> vClass) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public @NotNull <S, V> Map<S, V> getMapItem(K key, S subKey, Class<V> vClass) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <S, V> void setMap(K key, Map<S, V> valueMap) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <S, V> void setMapItem(K key, Map<S, V> valueMap) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public <S, V> void setMapItem(K key, S subKey, V value) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    public void deleteAll(K key) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }

    @Override
    @SafeVarargs
    public final <S> void deleteItem(K key, S... subKeys) {
        throw PortableErrors.of("B-03-001", "暂不支持");
    }
}
