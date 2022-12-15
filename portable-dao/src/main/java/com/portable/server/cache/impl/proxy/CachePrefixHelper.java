package com.portable.server.cache.impl.proxy;

import java.time.Duration;

/**
 * @author shiroha
 */
public abstract class CachePrefixHelper<K> {

    /**
     * 前缀内容
     */
    private final String prefix;

    /**
     * 默认的缓存时间
     */
    protected final Duration defaultTtl;

    protected CachePrefixHelper(String prefix, Duration defaultTtl) {
        this.prefix = prefix;
        this.defaultTtl = defaultTtl;
    }

    protected String prefixKey(K key) {
        return String.format("%s$%s", prefix, key);
    }
}
