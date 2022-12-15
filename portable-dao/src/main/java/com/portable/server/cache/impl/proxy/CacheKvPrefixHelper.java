package com.portable.server.cache.impl.proxy;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.portable.server.cache.CacheKvHelper;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class CacheKvPrefixHelper<K> extends CachePrefixHelper<K> implements CacheKvHelper<K> {

    /**
     * 实际的存储结构
     */
    private final CacheKvHelper<String> cacheKvHelper;

    public CacheKvPrefixHelper(String prefix, Duration defaultTtl, CacheKvHelper<String> cacheKvHelper) {
        super(prefix, defaultTtl);
        this.cacheKvHelper = cacheKvHelper;
    }

    @Override
    public <T> Optional<T> get(K key, Class<T> clazz) {
        return cacheKvHelper.get(this.prefixKey(key), clazz);
    }

    @Override
    @SafeVarargs
    public final @NotNull <T> List<T> multiGet(Class<T> clazz, K... keys) {
        String[] prefixKeys = (String[]) Arrays.stream(keys)
                .parallel()
                .map(this::prefixKey)
                .toArray();

        return cacheKvHelper.multiGet(clazz, prefixKeys);
    }

    @Override
    public <T> void set(K key, T value) {
        cacheKvHelper.set(this.prefixKey(key), value, defaultTtl);
    }

    @Override
    public <T> void set(K key, T value, Duration duration) {
        cacheKvHelper.set(this.prefixKey(key), value, duration);
    }

    @Override
    public void delete(K key) {
        cacheKvHelper.delete(this.prefixKey(key));
    }
}
