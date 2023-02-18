package com.portable.server.service;

import java.io.InputStream;

import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.request.user.UpdatePasswordRequest;
import com.portable.server.model.response.user.BaseUserInfoResponse;
import com.portable.server.model.response.user.BatchAdminUserInfoResponse;
import com.portable.server.model.response.user.NormalUserInfoResponse;
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
     * @param ip           本次登录的 IP
     * @return 用户的信息
     */
    BaseUserInfoResponse login(LoginRequest loginRequest, String ip);

    /**
     * 注册普通用户
     *
     * @param registerRequest 注册信息
     * @return 注册成功的用户信息
     */
    NormalUserInfoResponse register(RegisterRequest registerRequest);

    /**
     * 获取当前登录的用户
     *
     * @return 用户信息
     */
    BaseUserInfoResponse check();

    /**
     * 根据用户的 handle 获取用户的信息
     *
     * @param handle 用户的 handle
     * @return 用户信息
     */
    BaseUserInfoResponse getUserInfo(String handle);

    /**
     * 根据用户的 handle 获取批量用户的管理员级别信息
     *
     * @param handle 用户的 handle
     * @return 用户信息
     */
    BatchAdminUserInfoResponse getBatchUserInfo(String handle);

    /**
     * 修改用户所在组织
     *
     * @param targetHandle    被修改的用户
     * @param newOrganization 被修改至的组织
     */
    void changeOrganization(String targetHandle, OrganizationType newOrganization);

    /**
     * 添加权限
     *
     * @param targetHandle  目标用户
     * @param newPermission 新增加的权限
     */
    void addPermission(String targetHandle, PermissionType newPermission);

    /**
     * 移除权限
     *
     * @param targetHandle 目标用户
     * @param permission   移除的权限
     */
    void removePermission(String targetHandle, PermissionType permission);

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
     */
    String uploadAvatar(InputStream inputStream,
                        String name,
                        String contentType,
                        Integer left,
                        Integer top,
                        Integer width,
                        Integer height);

    /**
     * 更新用户的密码
     *
     * @param updatePasswordRequest 密码
     */
    void updatePassword(UpdatePasswordRequest updatePasswordRequest);

    /**
     * 重制其他用户的密码
     *
     * @param handle      用户昵称
     * @param newPassword 用户密码
     */
    void resetPassword(String handle, String newPassword);

    /**
     * 清理批量用户的 IP 记录（这会导致之前的 IP 记录被清理）
     *
     * @param handle 用户的 handle
     */
    void clearBatchUserIpList(String handle);
}
