package com.portable.server.model.request.problem;

import com.portable.server.type.PermissionType;
import lombok.Data;

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
