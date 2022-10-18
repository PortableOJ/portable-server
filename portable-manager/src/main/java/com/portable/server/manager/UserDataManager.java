package com.portable.server.manager;

import com.portable.server.exception.PortableException;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public interface UserDataManager {

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
