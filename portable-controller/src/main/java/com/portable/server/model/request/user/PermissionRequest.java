package com.portable.server.model.request.user;

import com.portable.server.type.PermissionType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Data
public class PermissionRequest {

    /**
     * 目标用户的昵称
     */
    @NotNull(message = "A-01-001")
    private String targetHandle;

    /**
     * 权力
     */
    @NotNull(message = "A-01-009")
    private PermissionType permissionType;
}
