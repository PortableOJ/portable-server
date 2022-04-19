package com.portable.server.kit;

import com.portable.server.model.RedisKeyAndExpire;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author shiroha
 */
public interface RedisValueKit {

    /**
     * 保存数据
     *
     * @param prefix key 的前缀
     * @param key    key 值
     * @param data   数据内容
     * @param time   过期时间（分钟）
     */
    void set(String prefix, Object key, Long data, Long time);

    /**
     * 保存数据
     *
     * @param prefix key 的前缀
     * @param key    key 值
     * @param data   数据内容
     * @param time   过期时间（分钟）
     */
    void set(String prefix, Object key, String data, Long time);

    /**
     * 保存数据
     *
     * @param prefix key 的前缀
     * @param key    key 值
     * @param data   数据内容
     * @param time   过期时间（分钟）
     * @param <T>    数据类型
     */
    <T> void set(String prefix, Object key, T data, Long time);

    /**
     * 获取数据
     *
     * @param prefix key 的前缀
     * @param key    key 值
     * @param clazz  值的类型
     * @param <T>    数据类型
     * @return 返回值
     */
    <T> Optional<T> get(String prefix, Object key, Class<T> clazz);

    /**
     * 变更数据
     *
     * @param prefix   key 的前缀
     * @param key      key 值
     * @param clazz    值的类型
     * @param time     新的缓存时间
     * @param consumer 变换方式
     * @param <T>      数据类型
     */
    <T> void getPeek(String prefix, Object key, Class<T> clazz, Long time, Consumer<T> consumer);

    /**
     * 检查是否存在此 key
     *
     * @param prefix key 的前缀
     * @param key    key 的值
     * @param clazz  值的类型
     * @param <T>    值的类型
     * @return 过期时间数据结构
     */
    <T> RedisKeyAndExpire<T> getValueAndTime(String prefix, String key, Class<T> clazz);

    /**
     * 检查是否存在此 key
     *
     * @param prefix key 的前缀
     * @param key    key 的值
     * @return 过期时间数据结构
     */
    RedisKeyAndExpire<String> getValueAndTime(String prefix, String key);
}
