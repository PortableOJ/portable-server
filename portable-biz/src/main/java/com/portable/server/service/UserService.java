package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.request.user.UpdatePasswordRequest;
import com.portable.server.model.response.user.BaseUserInfoResponse;
import com.portable.server.model.response.user.BatchAdminUserInfoResponse;
import com.portable.server.model.response.user.NormalUserInfoResponse;
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
     * @param ip           本次登录的 IP
     * @return 用户的信息
     * @throws PortableException 遇到意外情况抛出错误
     */
    BaseUserInfoResponse login(LoginRequest loginRequest, String ip) throws PortableException;

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
    @Deprecated
    BaseUserInfoResponse getUserInfo(Long userId) throws PortableException;

    /**
     * 根据用户的 handle 获取用户的信息
     *
     * @param handle 用户的 handle
     * @return 用户信息
     * @throws PortableException 不存在则抛出错误
     */
    BaseUserInfoResponse getUserInfo(String handle) throws PortableException;

    /**
     * 根据用户的 handle 获取批量用户的管理员级别信息
     *
     * @param handle 用户的 handle
     * @return 用户信息
     * @throws PortableException 不存在则抛出错误
     */
    BatchAdminUserInfoResponse getBatchUserInfo(String handle) throws PortableException;

    /**
     * 修改用户所在组织
     *
     * @param targetId        被修改的用户
     * @param newOrganization 被修改至的组织
     * @throws PortableException 遇到意外情况抛出错误
     */
    @Deprecated
    void changeOrganization(Long targetId, OrganizationType newOrganization) throws PortableException;

    /**
     * 添加权限
     *
     * @param targetId      目标用户
     * @param newPermission 新增加的权限
     * @throws PortableException 遇到意外情况抛出错误
     */
    @Deprecated
    void addPermission(Long targetId, PermissionType newPermission) throws PortableException;

    /**
     * 移除权限
     *
     * @param targetId   目标用户
     * @param permission 移除的权限
     * @throws PortableException 遇到意外情况抛出错误
     */
    @Deprecated
    void removePermission(Long targetId, PermissionType permission) throws PortableException;

    /**
     * 上传头像
     *
     * @param inputStream 头像文件流
     * @param name        文件名
     * @param contentType 文件类型
     * @param left        左侧边距
     * @param top         上侧边距
     * @param width       宽度
     * @param height      长度
     * @return 返回新的头像 id
     * @throws PortableException 类型不匹配则抛出
     */
    String uploadAvatar(InputStream inputStream,
                        String name,
                        String contentType,
                        Integer left,
                        Integer top,
                        Integer width,
                        Integer height) throws PortableException;

    /**
     * 更新用户的密码
     *
     * @param updatePasswordRequest 密码
     * @throws PortableException 密码错误则抛出
     */
    void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws PortableException;

    /**
     * 清理批量用户的 IP 记录（这会导致之前的 IP 记录被清理）
     *
     * @param handle 用户的 handle
     * @throws PortableException 用户不是批量用户时抛出
     */
    void clearBatchUserIpList(String handle) throws PortableException;
}
