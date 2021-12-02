package com.portable.server.model.request.user;

import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class LoginRequest {

    /**
     * 登陆账号
     */
    private String handle;

    /**
     * 密码
     */
    private String password;
}
