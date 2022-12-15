package com.portable.server.cache;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * 随机访问列表缓存
 *
 * @author shiroha
 */
public interface CacheRalHelper<K> {

    /**
     * 获取一个 list 的长度，为空或者不存在则返回 0
     *
     * @param key 缓存的 key
     * @return 长度
     */
    @NotNull Integer getListLength(K key);

    /**
     * 获取一个有序数组，若不存在此缓存则返回空数组
     *
     * @param key   缓存的 key
     * @param clazz 缓存类型
     * @param <T>   缓存类型
     * @return 缓存的数组
     */
    @NotNull <T> List<T> getList(K key, Class<T> clazz);

    /**
     * 获取一个有序数组的其中一部分，左闭右开
     *
     * @param key   缓存的 key
     * @param start 左区间
     * @param end   右区间
     * @param clazz 缓存类型
     * @param <T>   缓存类型
     * @return 缓存的数组部分
     */
    @NotNull <T> List<T> getRangeList(K key, Integer start, Integer end, Class<T> clazz);

    /**
     * 获取一个有序数组的其中一个
     *
     * @param key      缓存的 key
     * @param position 下标位置
     * @param clazz    缓存类型
     * @param <T>      缓存类型
     * @return 缓存的数组
     */
    @NotNull <T> T getAt(K key, Integer position, Class<T> clazz);

    /**
     * 保存一个有序数组，如果存在就覆盖
     *
     * @param key       缓存的 key
     * @param valueList 需要缓存的列表
     * @param <T>       缓存类型
     */
    <T> void setList(K key, List<T> valueList);

    /**
     * 修改一个数组的某个值，如果超出下标或者不存在这个 key，则返回失败
     *
     * @param key       缓存的 key
     * @param position  需要修改的下标
     * @param valueList 需要缓存的列表
     * @param <T>       缓存类型
     */
    <T> void setAt(K key, Integer position, List<T> valueList);

    /**
     * 往列表最后添加多个元素
     *
     * @param key    缓存的 key
     * @param values 需要添加的新的值
     * @param <T>    缓存类型
     */
    @SuppressWarnings({"unchecked"})
    default <T> void pushBack(K key, T... values) {
        this.pushBack(key, Arrays.asList(values));
    }

    /**
     * 往列表最后添加多个元素
     *
     * @param key       缓存的 key
     * @param valueList 需要添加的新的值
     * @param <T>       缓存类型
     */
    <T> void pushBack(K key, List<T> valueList);

    /**
     * 在列表最后删除一个元素
     *
     * @param key 缓存的 key
     */
    default void popBack(K key) {
        popBack(key, 1);
    }

    /**
     * 在列表最后删除多个元素
     *
     * @param key   缓存的 key
     * @param count 需要删除的数量
     */
    void popBack(K key, Integer count);

    /**
     * 删除某个 key
     *
     * @param key 缓存的 key
     */
    void delete(K key);
}
