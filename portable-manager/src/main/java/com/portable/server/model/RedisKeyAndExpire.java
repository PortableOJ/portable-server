package com.portable.server.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class RedisKeyAndExpire<T> {

    private T data;

    private Boolean hasKey;

    private Long expireTime;
}
