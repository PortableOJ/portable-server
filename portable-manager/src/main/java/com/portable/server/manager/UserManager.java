package com.portable.server.manager;

import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.stream.Stream;

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
     * @param handle 用户 handle
     * @return 用户信息
     */
    User getAccountByHandle(String handle);

    /**
     * 批量转换用户的昵称为用户的 id
     * @param handleList 用户昵称列表
     * @return 用户 id 列表
     */
    Stream<Long> changeUserHandleToUserId(Collection<String> handleList);

    /**
     * 根据用户的 id 获取账号
     * @param id 用户 id
     * @return 用户信息
     */
    User getAccountById(Long id);

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
}
