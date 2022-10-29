package com.portable.server.helper;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.portable.server.model.BaseEntity;

/**
 * @author shiroha
 */
public interface MemProtractedHelper<T extends BaseEntity<V>, V extends Serializable> {

    /**
     * 根据 id 获取存储的值
     *
     * @param id 值的 id
     * @return 存储的值，可能为 null
     */
    Optional<T> getDataById(V id);

    /**
     * 根据条件查找第一个
     *
     * @param function 查找条件
     * @return 返回命中的第一个
     */
    Optional<T> searchFirst(Function<T, Boolean> function);

    /**
     * 根据条件查找
     *
     * @param function 查找条件
     * @return 返回命中所有
     */
    List<T> searchList(Function<T, Boolean> function);

    /**
     * 更新值
     *
     * @param data 存储的值，可以为 null
     */
    void updateById(T data);

    /**
     * 自定义更新值
     *
     * @param filter   过滤条件
     * @param consumer 更新方法
     */
    void updateByFunction(Function<T, Boolean> filter, Consumer<T> consumer);

    /**
     * 自定义更新值
     *
     * @param id       key
     * @param consumer 更新方法
     */
    void updateByFunction(V id, Consumer<T> consumer);

    /**
     * 插入值
     *
     * @param data      新增值，并修改其对应的 key
     * @param translate key 转换函数
     */
    void insert(T data, Function<Long, V> translate);
}
