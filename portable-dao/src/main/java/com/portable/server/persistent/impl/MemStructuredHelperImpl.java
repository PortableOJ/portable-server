package com.portable.server.persistent.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.portable.server.model.BaseEntity;
import com.portable.server.persistent.StructuredHelper;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;

/**
 * @author shiroha
 */
@Lazy
public class MemStructuredHelperImpl<E extends BaseEntity<K>, K extends Comparable<K>> implements StructuredHelper<E, K> {

    /**
     * 实际存储的值
     */
    private Map<K, E> mapDb;

    /**
     * 下一个 id
     */
    private AtomicLong nextId;

    @PostConstruct
    private void init() {
        mapDb = new HashMap<>(0);
        nextId = new AtomicLong();
    }

    @Override
    public @NotNull List<E> getAll() {
        return new ArrayList<>(mapDb.values());
    }

    @Override
    public Optional<E> getDataById(K id) {
        return Optional.ofNullable(mapDb.get(id));
    }

    @Override
    public Optional<E> searchFirst(Predicate<E> function, Comparator<E> comparator) {
        return mapDb.values().stream()
                .filter(function)
                .min(comparator);
    }

    @Override
    public @NotNull Integer countList(Predicate<E> function) {
        return Math.toIntExact(mapDb.values().stream()
                .filter(function)
                .count());
    }

    @Override
    public @NotNull List<E> searchList(Predicate<E> function, Comparator<E> comparator) {
        return mapDb.values().stream()
                .filter(function)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull List<E> searchListByPage(Predicate<E> function, Integer pageSize, Integer offset, Comparator<E> comparator) {
        return mapDb.values().stream()
                .filter(function)
                .sorted(comparator)
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public void updateById(E data) {
        mapDb.put(data.getId(), data);
    }

    @Override
    public void updateByFunction(Predicate<E> filter, Consumer<E> consumer) {
        mapDb.values().stream()
                .filter(filter)
                .forEach(consumer);
    }

    @Override
    public void updateByFunction(K id, Consumer<E> consumer) {
        consumer.accept(mapDb.get(id));
    }

    @Override
    public void insert(E data, Function<Long, K> translate) {
        data.setId(translate.apply(nextId.incrementAndGet()));
        mapDb.put(data.getId(), data);
    }

    @Override
    public void removeById(K id) {
        mapDb.remove(id);
    }

    @Override
    public void removeIf(Predicate<E> filter) {
        mapDb.values().removeIf(filter);
    }
}
