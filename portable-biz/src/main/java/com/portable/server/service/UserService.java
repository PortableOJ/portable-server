package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.request.user.UpdatePasswordRequest;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.response.user.UserBasicInfoResponse;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;

import java.io.InputStream;

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
     * 根据用户的 id 获取用户信息
     *
     * @param userId 用户的 id
     * @return 用户信息
     * @throws PortableException 不存在则抛出错误
     */
    UserBasicInfoResponse getUserInfo(Long userId) throws PortableException;

    /**
     * 根据用户的 handle 获取用户的信息
     *
     * @param handle 用户的 handle
     * @return 用户信息
     * @throws PortableException 不存在则抛出错误
     */
    UserBasicInfoResponse getUserInfo(String handle) throws PortableException;

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

    /**
     * 上传头像
     * @param inputStream 头像文件流
     * @param name 文件名
     * @param contentType 文件类型
     * @throws PortableException 类型不匹配则抛出
     */
    void uploadAvatar(InputStream inputStream, String name, String contentType) throws PortableException;

    /**
     * 更新用户的密码
     * @param updatePasswordRequest 密码
     * @throws PortableException 密码错误则抛出
     */
    void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws PortableException;
}
