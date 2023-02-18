package com.portable.server.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author shiroha
 */
public class PortableUtils {

    public static <T> T max(T a, T b, Comparator<? super T> c) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return c.compare(a, b) < 0 ? b : a;
        }
    }

    public static <T> @NotNull Boolean equalOrNull(@Nullable T filter, @Nullable T o) {
        return filter == null || filter.equals(o);
    }

    public static <T> @Nullable List<T> singletonListIfNotNull(@Nullable T o) {
        return o == null ? null : Collections.singletonList(o);
    }
}
