package com.portable.server.helper.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.portable.server.helper.MemProtractedHelper;
import com.portable.server.model.BaseEntity;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Lazy
@Component
public class MemProtractedHelperImpl<T extends BaseEntity<V>, V extends Serializable> implements MemProtractedHelper<T, V> {

    private Map<V, T> mapDb;

    private AtomicLong nextId;

    @PostConstruct
    private void init() {
        mapDb = new HashMap<>(0);
        nextId = new AtomicLong();
    }

    @Override
    public Optional<T> getDataById(V id) {
        return Optional.ofNullable(mapDb.get(id));
    }

    @Override
    public Optional<T> searchFirst(Function<T, Boolean> function) {
        return mapDb.values().stream()
                .filter(function::apply)
                .findFirst();
    }

    @Override
    public List<T> searchList(Function<T, Boolean> function) {
        return mapDb.values().stream()
                .filter(function::apply)
                .collect(Collectors.toList());
    }

    @Override
    public void updateById(T data) {
        mapDb.put(data.getId(), data);
    }

    @Override
    public void updateByFunction(Function<T, Boolean> filter, Consumer<T> consumer) {
        mapDb.values().stream()
                .filter(filter::apply)
                .forEach(consumer);
    }

    @Override
    public void updateByFunction(V id, Consumer<T> consumer) {
        consumer.accept(mapDb.get(id));
    }

    @Override
    public void insert(T data, Function<Long, V> translate) {
        data.setId(translate.apply(nextId.incrementAndGet()));
    }
}
