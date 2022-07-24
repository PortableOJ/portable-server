package com.portable.server.util;

import java.util.Comparator;

/**
 * @author shiroha
 */
public class ObjectUtils {

    public static <T> T max(T a, T b, Comparator<? super T> c) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return c.compare(a, b) < 0 ? b : a;
        }
    }
}
