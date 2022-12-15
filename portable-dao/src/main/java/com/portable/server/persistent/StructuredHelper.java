package com.portable.server.persistent;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.portable.server.model.BaseEntity;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public interface StructuredHelper<E extends BaseEntity<K>, K extends Comparable<K>> {

    /**
     * 获取全部值
     *
     * @return 全部的值
     */
    @NotNull List<E> getAll();

    /**
     * 根据 id 获取存储的值
     *
     * @param id 值的 id
     * @return 存储的值，可能为 null
     */
    Optional<E> getDataById(K id);

    /**
     * 根据条件查找第一个
     *
     * @param function 查找条件
     * @return 返回命中的第一个
     */
    default Optional<E> searchFirst(Predicate<E> function) {
        return searchFirst(function, Comparator.comparing(BaseEntity::getId));
    }

    /**
     * 根据条件查找第一个
     *
     * @param function   查找条件
     * @param comparator 排序规则
     * @return 返回命中的第一个
     */
    Optional<E> searchFirst(Predicate<E> function, Comparator<E> comparator);

    /**
     * 根据条件统计数量
     *
     * @param function 查找条件
     * @return 返回数量
     */
    @NotNull Integer countList(Predicate<E> function);

    /**
     * 根据条件查找
     *
     * @param function 查找条件
     * @return 返回命中所有
     */
    @NotNull
    default List<E> searchList(Predicate<E> function) {
        return searchList(function, Comparator.comparing(BaseEntity::getId));
    }

    /**
     * 根据条件查找
     *
     * @param function   查找条件
     * @param comparator 比较方法，默认为正向
     * @return 返回命中所有
     */
    @NotNull List<E> searchList(Predicate<E> function, Comparator<E> comparator);

    /**
     * 根据条件查找
     *
     * @param function 查找条件
     * @param pageSize 单页大小
     * @param offset   偏移量
     * @return 返回命中所有
     */
    @NotNull
    default List<E> searchListByPage(Predicate<E> function, Integer pageSize, Integer offset) {
        return searchListByPage(function, pageSize, offset, Comparator.comparing(BaseEntity::getId));
    }

    /**
     * 根据条件查找
     *
     * @param function   查找条件
     * @param pageSize   单页大小
     * @param offset     偏移量
     * @param comparator 排序方式
     * @return 返回命中所有
     */
    @NotNull List<E> searchListByPage(Predicate<E> function, Integer pageSize, Integer offset, Comparator<E> comparator);

    /**
     * 更新值
     *
     * @param data 存储的值，可以为 null
     */
    void updateById(E data);

    /**
     * 自定义更新值
     *
     * @param filter   过滤条件
     * @param consumer 更新方法
     */
    void updateByFunction(Predicate<E> filter, Consumer<E> consumer);

    /**
     * 自定义更新值
     *
     * @param id       key
     * @param consumer 更新方法
     */
    void updateByFunction(K id, Consumer<E> consumer);

    /**
     * 插入值
     *
     * @param data      新增值，并修改其对应的 key
     * @param translate key 转换函数
     */
    void insert(E data, Function<Long, K> translate);

    /**
     * 根据 ID 移除元素
     *
     * @param id 指定的 id
     */
    void removeById(K id);

    /**
     * 根据条件移除元素
     *
     * @param filter 条件
     */
    void removeIf(Predicate<E> filter);
}
