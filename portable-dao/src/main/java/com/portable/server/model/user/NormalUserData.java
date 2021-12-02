package com.portable.server.model.user;

import com.portable.server.type.PermissionType;
import com.portable.server.type.OrganizationType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@Builder
public class NormalUserData {

    /**
     * 数据库主键
     */
    @Id
    private String _id;

    /**
     * 所属组织
     */
    private OrganizationType organization;

    /**
     * 总提交数量
     */
    private Integer submission;

    /**
     * 总通过数量
     */
    private Integer accept;

    /**
     * 权限列表
     */
    private Set<PermissionType> permissionTypeSet;

    /**
     * 邮箱
     */
    private String email;
}
