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
     * 注册普通用户
     *
     * @param loginRequest 注册信息
     * @return 注册成功的用户信息
     * @throws PortableException 账号不存在、密码错误，数据出错、用户类型错误
     */
    UserBasicInfoResponse login(LoginRequest loginRequest) throws PortableException;

    NormalUserInfoResponse register(RegisterRequest registerRequest) throws PortableException;

    void changeOrganization(Long targetId, OrganizationType newOrganization) throws PortableException;

    void addPermission(Long targetId, PermissionType newPermission) throws PortableException;

    void removePermission(Long targetId, PermissionType permission) throws PortableException;
}
