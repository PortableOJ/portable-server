package com.portable.server.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@AllArgsConstructor
public class Response<T> {
    private Boolean success;
    private String code;
    private String msg;
    private T data;

    public static <T> Response<T> ofOk(T data) {
        return new Response<>(true,null, null, data);
    }

    public static <T> Response<T> ofOk() {
        return new Response<>(true,null, null, null);
    }

    public static <T> Response<T> ofFail(String code, String msg) {
        return new Response<>(false, code, msg, null);
    }
}
