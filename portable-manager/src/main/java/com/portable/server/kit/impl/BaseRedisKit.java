package com.portable.server.kit.impl;

/**
 * @author shiroha
 */
public abstract class BaseRedisKit {
    protected String getKey(String prefix, Object key) {
        return String.format("%s_%s", prefix, key.toString());
    }
}
