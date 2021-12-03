package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.response.user.UserBasicInfoResponse;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;

/**
 * 用户服务模块
 *
 * @author shiroha
 */
public interface UserService {

    /**
     * 登陆用户
     *
     * @param loginRequest 登陆信息
     * @return 用户的信息
     * @throws PortableException 遇到意外情况抛出错误
     */
    UserBasicInfoResponse login(LoginRequest loginRequest) throws PortableException;

    /**
     * 注册普通用户
     *
     * @param registerRequest 注册信息
     * @return 注册成功的用户信息
     * @throws PortableException 遇到意外情况抛出错误
     */
    NormalUserInfoResponse register(RegisterRequest registerRequest) throws PortableException;

    /**
     * 修改用户所在组织
     *
     * @param targetId        被修改的用户
     * @param newOrganization 被修改至的组织
     * @throws PortableException 遇到意外情况抛出错误
     */
    void changeOrganization(Long targetId, OrganizationType newOrganization) throws PortableException;

    /**
     * 添加权限
     *
     * @param targetId      目标用户
     * @param newPermission 新增加的权限
     * @throws PortableException 遇到意外情况抛出错误
     */
    void addPermission(Long targetId, PermissionType newPermission) throws PortableException;

    /**
     * 移除权限
     *
     * @param targetId   目标用户
     * @param permission 移除的权限
     * @throws PortableException 遇到意外情况抛出错误
     */
    void removePermission(Long targetId, PermissionType permission) throws PortableException;
}
