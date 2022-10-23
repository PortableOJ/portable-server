package com.portable.server.helper;

import java.io.Serializable;
import java.util.function.Function;

import com.portable.server.model.BaseEntity;

/**
 * @author shiroha
 */
public interface MapDatabaseHelper<T extends BaseEntity<V>, V extends Serializable> {

    /**
     * 根据 id 获取存储的值
     *
     * @param id 值的 id
     * @return 存储的值，可能为 null
     */
    T getDataById(V id);

    /**
     * 更新值
     *
     * @param data 存储的值，可以为 null
     */
    void updateById(T data);

    /**
     * 插入值
     *
     * @param data      新增值，并修改其对应的 key
     * @param translate key 转换函数
     */
    void insert(T data, Function<Long, V> translate);
}
