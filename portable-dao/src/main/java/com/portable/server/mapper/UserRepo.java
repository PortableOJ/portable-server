package com.portable.server.mapper;

import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;

import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * @author shiroha
 */
@Repository
public interface UserRepo {

    /**
     * 根据用户 ID 获取用户账号信息
     *
     * @param id 用户的 ID
     * @return 用户信息
     */
    @Nullable User selectAccountById(@NotNull Long id);

    /**
     * 根据用户的用户名获取用户账号信息
     *
     * @param handle 用户名
     * @return 用户的账号信息
     */
    @Nullable User selectAccountByHandle(@NotNull String handle);

    /**
     * 新增一个用户
     *
     * @param user 新增的用户信息
     */
    void insertAccount(@NotNull User user);

    /**
     * 更新用户的用户名
     *
     * @param id     用户的 ID
     * @param handle 用户的新用户名
     */
    void updateHandle(@NotNull @Param("id") Long id, @NotNull @Param("handle") String handle);

    /**
     * 更新密码
     *
     * @param id       用户的 ID
     * @param password 更新后的密码
     */
    void updatePassword(@NotNull @Param("id") Long id, @NotNull @Param("password") String password);

    /**
     * 更新用户类型
     *
     * @param id          用户的 ID
     * @param accountType 账号类型
     */
    void updateUserType(@NotNull @Param("id") Long id, @NotNull @Param("newStatus") AccountType accountType);
}
