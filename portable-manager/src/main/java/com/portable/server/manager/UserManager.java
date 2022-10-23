package com.portable.server.manager;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import com.portable.server.exception.PortableException;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;

import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public interface UserManager {

    /**
     * 创建一个新的标准账号（不插入数据库）
     * @return 新的用户信息
     */
    User newNormalAccount();

    /**
     * 创建一个新的批量账号（不插入数据库）
     * @return 新的用户信息
     */
    User newBatchAccount();

    /**
     * 根据用户的 handle 获取账号
     *
     * @param handle 用户 handle
     * @return 用户信息
     */
    Optional<User> getAccountByHandle(String handle);

    /**
     * 将用户的昵称转为用户的 id
     *
     * @param handle 用户的昵称
     * @return 用户的 id
     */
    Optional<Long> changeHandleToUserId(String handle);

    /**
     * 批量转换用户的昵称为用户的 id
     *
     * @param handleList 用户昵称列表
     * @return 用户 id 列表
     */
    Set<Long> changeHandleToUserId(Collection<String> handleList);

    /**
     * 根据用户的 id 获取账号
     * @param id 用户 id
     * @return 用户信息
     */
    Optional<User> getAccountById(Long id);

    /**
     * 新增一个账号
     * @param user 用户信息
     */
    void insertAccount(User user);

    /**
     * 更新用户的 handle
     * @param id 用户 ID
     * @param handle 用户的 handle
     */
    void updateHandle(Long id, String handle);

    /**
     * 更新用户的密码
     * @param id 用户的 ID
     * @param password 用户的密码
     */
    void updatePassword(Long id, String password);

    /**
     * 更新用户类型
     *
     * @param id          用户的 ID
     * @param accountType 账号类型
     */
    void updateUserType(@Param("id") Long id, @Param("newStatus") AccountType accountType);

    /**
     * 新建一个普通用户数据实体
     *
     * @return 普通用户数据实体
     */
    @NotNull
    NormalUserData newNormalUserData();

    /**
     * 新建一个批量用户数据实体
     *
     * @return 普通用户数据实体
     */
    @NotNull
    BatchUserData newBatchUserData();

    /**
     * 通过用户的数据 id 获取普通用户
     *
     * @param dataId 用户的数据 id
     * @return 用户数据
     * @throws PortableException ID 不存在时则抛出
     */
    @NotNull
    NormalUserData getNormalUserDataById(String dataId);

    /**
     * 通过用户的数据 id 获取批量用户账号
     *
     * @param dataId 用户的数据 id
     * @return 用户数据
     * @throws PortableException ID 不存在时则抛出
     */
    @NotNull
    BatchUserData getBatchUserDataById(String dataId);

    /**
     * 新增一个普通用户数据
     *
     * @param baseUserData 普通用户数据
     */
    void insertUserData(BaseUserData baseUserData);

    /**
     * 更新普通用户数据
     *
     * @param baseUserData 更新后的用户数据
     */
    void updateUserData(BaseUserData baseUserData);
}
