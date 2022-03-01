package com.portable.server.util;

import com.alibaba.fastjson.JSON;

/**
 * @author shiroha
 */
public class JsonUtils {

    public static <T> String toString(T object) {
        return JSON.toJSONString(object);
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return JSON.toJavaObject(JSON.parseObject(json), clazz);
    }
}
