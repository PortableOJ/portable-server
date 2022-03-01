package com.portable.server.kit;

import com.portable.server.model.RedisKeyAndExpire;

import java.util.Optional;

/**
 * @author shiroha
 */
public interface RedisKit {

    /**
     * 保存数据
     * @param prefix key 的前缀
     * @param key key 值
     * @param data 数据内容
     * @param time 过期时间（分钟）
     */
    void set(String prefix, String key, String data, Long time);

    /**
     * 保存数据
     * @param prefix key 的前缀
     * @param key key 值
     * @param data 数据内容
     * @param time 过期时间（分钟）
     * @param <T> 数据类型
     */
    <T> void set(String prefix, String key, T data, Long time);

    /**
     * 获取数据
     * @param prefix key 的前缀
     * @param key key 值
     * @param clazz 值的类型
     * @param <T> 数据类型
     * @return 返回值
     */
    <T> Optional<T> get(String prefix, String key, Class<T> clazz);

    /**
     * 检查是否存在此 key
     * @param prefix key 的前缀
     * @param key key 的值
     * @param clazz 值的类型
     * @param <T> 值的类型
     * @return 过期时间数据结构
     */
    <T> RedisKeyAndExpire<T> getValueAndTime(String prefix, String key, Class<T> clazz);


    /**
     * 检查是否存在此 key
     * @param prefix key 的前缀
     * @param key key 的值
     * @return 过期时间数据结构
     */
    RedisKeyAndExpire<String> getValueAndTime(String prefix, String key);
}
