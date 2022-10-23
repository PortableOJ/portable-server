package com.portable.server.helper;

import java.util.List;
import java.util.Optional;

/**
 * @author shiroha
 */
public interface RedisListHelper {

    /**
     * 创建 list
     * <p color="red">请不要传递基本类型的包装类以及 String</p>
     * @param prefix 前缀
     * @param key key 值
     * @param data 数组数据内容
     * @param <T> 类型
     */
    <T> void create(String prefix, Object key, List<T> data);

    /**
     * 获取第 i 个值
     * <p color="red">请不要传递基本类型的包装类以及 String</p>
     * @param prefix 前缀
     * @param key key 值
     * @param index 数组下标
     * @param clazz 类型
     * @param <T> 类型
     * @return 值
     */
    <T> Optional<T> get(String prefix, Object key, Integer index, Class<T> clazz);

    /**
     * 获取列表长度
     * @param prefix 前缀
     * @param key key 的值
     * @return 长度
     */
    Integer getLen(String prefix, Object key);

    /**
     * 分页获取数据
     * <p color="red">请不要传递基本类型的包装类以及 String</p>
     * @param prefix 前缀
     * @param key key 值
     * @param clazz 类型
     * @param pageSize 单页大小
     * @param offset 偏移量
     * @param <T> 类型
     * @return 列表
     */
    <T> List<T> getPage(String prefix, Object key, Integer pageSize, Integer offset, Class<T> clazz);

    /**
     * 清空 list
     * @param prefix 前缀
     * @param key key 的值
     */
    void clear(String prefix, Object key);
}
