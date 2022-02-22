package com.portable.server.manager;

import com.portable.server.model.user.NormalUserData;

/**
 * @author shiroha
 */
public interface UserDataManager {

    /**
     * 新建一个普通用户数据实体
     *
     * @return 普通用户数据实体
     */
    NormalUserData newUserData();

    /**
     * 通过用户的数据 id 获取普通用户
     *
     * @param dataId 用户的数据 id
     * @return 用户数据
     */
    NormalUserData getUserDataById(String dataId);

    /**
     * 新增一个普通用户数据
     *
     * @param normalUserData 普通用户数据
     */
    void insertNormalUserData(NormalUserData normalUserData);

    /**
     * 更新普通用户数据
     *
     * @param normalUserData 更新后的用户数据
     */
    void updateNormalUserData(NormalUserData normalUserData);
}
