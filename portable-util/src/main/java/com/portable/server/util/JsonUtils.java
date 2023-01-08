package com.portable.server.util;

import com.portable.server.exception.PortableErrors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author shiroha
 */
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> String toString(T object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw PortableErrors.of(e, "B-02-001", e.getMessage());
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw PortableErrors.of(e, "B-02-002", e.getMessage());
        }
    }
}
