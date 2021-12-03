package com.portable.server.model.request.user;

import com.portable.server.type.PermissionType;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class PermissionRequest {

    /**
     * 目标用户
     */
    private Long targetId;

    /**
     * 权力
     */
    private PermissionType permissionType;
}
