package com.portable.server.model.judge;

import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class ServerCode {

    /**
     * code 值
     */
    private String code;

    /**
     * 过期时间
     */
    private Long expireTime;

    public Boolean isExpired() {
        return expireTime != null && System.currentTimeMillis() > expireTime;
    }
}
