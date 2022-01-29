package com.portable.server.manager;

import com.portable.server.model.user.User;

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
     * 根据用户的 handle 获取账号
     * @param handle 用户 handle
     * @return 用户信息
     */
    User getAccountByHandle(String handle);

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
}
