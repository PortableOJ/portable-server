package com.portable.server.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisKeyAndExpire<T> {

    private T data;

    private Boolean hasKey;

    private Long expireTime;
}
