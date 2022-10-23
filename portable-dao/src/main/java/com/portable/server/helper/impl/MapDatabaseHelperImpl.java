package com.portable.server.helper.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import com.portable.server.helper.MapDatabaseHelper;
import com.portable.server.model.BaseEntity;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class MapDatabaseHelperImpl<T extends BaseEntity<V>, V extends Serializable> implements MapDatabaseHelper<T, V> {

    private Map<V, T> mapDb;

    private AtomicLong nextId;

    @PostConstruct
    private void init() {
        mapDb = new HashMap<>(0);
        nextId = new AtomicLong();
    }

    @Override
    public T getDataById(V id) {
        return mapDb.get(id);
    }

    @Override
    public void updateById(T data) {
        mapDb.put(data.getId(), data);
    }

    @Override
    public void insert(T data, Function<Long, V> translate) {
        data.setId(translate.apply(nextId.incrementAndGet()));
    }
}
