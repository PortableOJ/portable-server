package com.portable.server.model.request.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author shiroha
 */
@Data
@Builder
public class UpdatePasswordRequest {

    /**
     * 旧密码
     */
    @NotBlank(message = "A-01-007")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "A-01-007")
    @Pattern(message = "A-01-005", regexp = "^[a-zA-Z0-9_\\-@#$%^&*~',./?:]{6,16}$")
    private String newPassword;
}
