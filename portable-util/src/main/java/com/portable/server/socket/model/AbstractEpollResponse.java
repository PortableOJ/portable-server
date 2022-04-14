package com.portable.server.socket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author shiroha
 */
@Data
public abstract class AbstractEpollResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class KeyValue {
        private String key;
        private String value;
    }

    private List<KeyValue> data;

    public AbstractEpollResponse() {
        this.data = new LinkedList<>();
    }

    protected void add(String key, Object value) {
        data.add(KeyValue.builder()
                .key(key)
                .value(value.toString())
                .build());
    }

    public byte[] toResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        data.forEach(keyValue -> stringBuilder.append(String.format("%s %s\n", keyValue.getKey(), keyValue.getValue())));
        return stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}
