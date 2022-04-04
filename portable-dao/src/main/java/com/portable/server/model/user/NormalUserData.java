package com.portable.server.model.user;

import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NormalUserData extends BaseUserData {

    /**
     * 头像
     */
    private String avatar;

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
