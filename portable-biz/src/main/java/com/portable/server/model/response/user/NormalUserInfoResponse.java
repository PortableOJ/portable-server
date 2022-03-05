package com.portable.server.model.response.user;

import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * 普通用户信息内容
 *
 * @author shiroha
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NormalUserInfoResponse extends UserBasicInfoResponse {

    /**
     * 所属组织
     */
    private OrganizationType organizationType;

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

    /**
     * 头像
     */
    private String avatar;

    private NormalUserInfoResponse(User user, NormalUserData normalUserData) {
        super(user);
        this.organizationType = normalUserData.getOrganization();
        this.submission = normalUserData.getSubmission();
        this.accept = normalUserData.getAccept();
        this.permissionTypeSet = normalUserData.getPermissionTypeSet();
        this.email = normalUserData.getEmail();
        this.avatar = normalUserData.getAvatar();
    }

    public static NormalUserInfoResponse of(User user, NormalUserData normalUserData) {
        return new NormalUserInfoResponse(user, normalUserData);
    }
}
