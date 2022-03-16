package com.portable.server.manager;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.type.ContestAccessType;

/**
 * @author shiroha
 */
public interface ContestDataManager {

    /**
     * 创建一个新的比赛实体
     * @param accessType 比赛类型
     * @return 实体信息
     * @throws PortableException 不存在此类型的比赛时候抛出
     */
    BaseContestData newContestData(ContestAccessType accessType) throws PortableException;

    /**
     * 获取通用的比赛信息
     * @param datId 比赛的 id
     * @param accessType 比赛类型
     * @return 比赛信息
     * @throws PortableException 比赛类型错误则抛出
     */
    BaseContestData getBaseContestDataById(String datId, ContestAccessType accessType) throws PortableException;

    /**
     * 获取公开的比赛信息
     * @param datId 比赛的 id
     * @return 比赛信息
     */
    PublicContestData getPublicContestDataById(String datId);

    /**
     * 获取带密码的比赛信息
     * @param datId 比赛的 id
     * @return 比赛信息
     */
    PasswordContestData getPasswordContestDataById(String datId);

    /**
     * 获取私有的比赛信息
     * @param datId 比赛的 id
     * @return 比赛信息
     */
    PrivateContestData getPrivateContestDataById(String datId);

    /**
     * 获取提供账号邀请制的比赛信息
     * @param datId 比赛的 id
     * @return 比赛信息
     */
    BatchContestData getBatchContestDataById(String datId);

    /**
     * 新建比赛
     * @param contestData 比赛的信息
     */
    void insertContestData(BaseContestData contestData);

    /**
     * 更新比赛信息
     * @param contestData 比赛的信息
     */
    void saveContestData(BaseContestData contestData);
}
