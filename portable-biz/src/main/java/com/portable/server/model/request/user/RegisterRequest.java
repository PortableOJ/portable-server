package com.portable.server.model.request.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author shiroha
 */
@Data
public class RegisterRequest {

    /**
     * 账号
     */
    @NotBlank(message = "A-01-006")
    @Pattern(message = "A-01-004", regexp = "^[a-zA-Z0-9_\\-]{4,15}$")
    private String handle;

    /**
     * 密码
     */
    @NotBlank(message = "A-01-007")
    @Pattern(message = "A-01-005", regexp = "^[a-zA-Z0-9_\\-@#$%^&*~',./?:]{6,16}$")
    private String password;
}
