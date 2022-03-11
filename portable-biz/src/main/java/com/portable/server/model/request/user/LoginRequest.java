package com.portable.server.model.request.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author shiroha
 */
@Data
public class LoginRequest {

    /**
     * 登陆账号
     */
    @NotBlank(message = "A-01-006")
    private String handle;

    /**
     * 密码
     */
    @NotBlank(message = "A-01-007")
    private String password;
}
