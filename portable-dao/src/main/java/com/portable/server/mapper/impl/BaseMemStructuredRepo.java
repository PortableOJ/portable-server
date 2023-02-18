package com.portable.server.mapper.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.portable.server.model.BaseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiroha
 */
public abstract class BaseMemStructuredRepo<K extends Comparable<K>, E extends BaseEntity<K>> {

    /**
     * 实际存储的值
     */
    private Map<K, E> mapDb;

    /**
     * 下一个 id
     */
    private AtomicLong nextId;

    @PostConstruct
    public void init() {
        mapDb = new HashMap<>(0);
        nextId = new AtomicLong();
    }

    public @NotNull List<E> getAll() {
        return new ArrayList<>(mapDb.values());
    }

    public @Nullable E getDataById(@NotNull K id) {
        return mapDb.get(id);
    }

    public @Nullable E searchFirstAsc(@NotNull Predicate<E> function) {
        return searchFirst(function, BaseMemStructuredRepo.compareAsc());
    }

    public @Nullable E searchFirstDesc(@NotNull Predicate<E> function) {
        return searchFirst(function, BaseMemStructuredRepo.compareDesc());
    }

    public @Nullable E searchFirst(@NotNull Predicate<E> function, @NotNull Comparator<E> comparator) {
        return mapDb.values().stream()
                .filter(function)
                .min(comparator)
                .orElse(null);
    }

    public @NotNull Integer countAll() {
        return mapDb.size();
    }

    public @NotNull Integer countList(@NotNull Predicate<E> function) {
        return Math.toIntExact(mapDb.values().stream()
                .filter(function)
                .count());
    }

    public @NotNull List<E> searchListAsc(@NotNull Predicate<E> function) {
        return searchList(function, BaseMemStructuredRepo.compareAsc());
    }

    public @NotNull List<E> searchListDesc(@NotNull Predicate<E> function) {
        return searchList(function, BaseMemStructuredRepo.compareDesc());
    }

    public @NotNull List<E> searchList(@NotNull Predicate<E> function, @NotNull Comparator<E> comparator) {
        return mapDb.values().stream()
                .filter(function)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public @NotNull List<E> searchListByPageAsc(@NotNull Predicate<E> function, @NotNull Integer pageSize, @NotNull Integer offset) {
        return searchListByPage(function, pageSize, offset, BaseMemStructuredRepo.compareAsc());
    }

    public @NotNull List<E> searchListByPageDesc(@NotNull Predicate<E> function, @NotNull Integer pageSize, @NotNull Integer offset) {
        return searchListByPage(function, pageSize, offset, BaseMemStructuredRepo.compareDesc());
    }

    public @NotNull List<E> searchListByPage(@NotNull Predicate<E> function, @NotNull Integer pageSize, @NotNull Integer offset, @NotNull Comparator<E> comparator) {
        return mapDb.values().stream()
                .filter(function)
                .sorted(comparator)
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public void updateById(@NotNull E data) {
        mapDb.put(data.getId(), data);
    }

    public void updateByFunction(@NotNull Predicate<E> filter, @NotNull Consumer<E> consumer) {
        mapDb.values().stream()
                .filter(filter)
                .forEach(consumer);
    }

    public void updateByFunction(@NotNull K id, @NotNull Consumer<E> consumer) {
        consumer.accept(mapDb.get(id));
    }

    public void insert(@NotNull E data, @NotNull Function<Long, K> translate) {
        data.setId(translate.apply(nextId.incrementAndGet()));
        mapDb.put(data.getId(), data);
    }

    public void removeById(@NotNull K id) {
        mapDb.remove(id);
    }

    public void removeIf(@NotNull Predicate<E> filter) {
        mapDb.values().removeIf(filter);
    }

    private static <SK extends Comparable<SK>, SE extends BaseEntity<SK>> Comparator<SE> compareAsc() {
        return Comparator.comparing(BaseEntity::getId);
    }

    private static <SK extends Comparable<SK>, SE extends BaseEntity<SK>> Comparator<SE> compareDesc() {
        return (o1, o2) -> o2.getId().compareTo(o1.getId());
    }
}
