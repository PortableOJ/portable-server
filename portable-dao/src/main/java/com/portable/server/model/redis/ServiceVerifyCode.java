package com.portable.server.model.redis;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * service 的编码
 *
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceVerifyCode {

    /**
     * 编码
     */
    private String code;

    /**
     * 是否是临时的编码
     */
    private Boolean temporary;

    /**
     * 使用有效期，截止时间
     */
    private Date endTime;
}
