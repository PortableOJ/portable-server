package com.portable.server.model.request.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author shiroha
 */
@Data
public class ResetPasswordRequest {

    /**
     * 目标用户的昵称
     */
    @NotNull(message = "A-01-001")
    private String handle;

    /**
     * 新密码
     */
    @NotBlank(message = "A-01-007")
    @Pattern(message = "A-01-005", regexp = "^[a-zA-Z0-9_\\-@#$%^&*~',./?:]{6,16}$")
    private String newPassword;
}
