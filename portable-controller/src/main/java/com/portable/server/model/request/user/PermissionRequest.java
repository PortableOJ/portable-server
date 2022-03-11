package com.portable.server.model.request.user;

import com.portable.server.type.PermissionType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author shiroha
 */
@Data
public class PermissionRequest {

    /**
     * 目标用户
     */
    @Positive(message = "A-01-001")
    private Long targetId;

    /**
     * 权力
     */
    @NotNull(message = "A-01-009")
    private PermissionType permissionType;
}
