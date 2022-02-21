package com.portable.server.manager;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.BasicContestData;
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
     */
    BasicContestData newContestData(ContestAccessType accessType) throws PortableException;

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
     * 新建比赛
     * @param contestData 比赛的信息
     */
    void insertContestData(BasicContestData contestData);

    /**
     * 更新比赛信息
     * @param contestData 比赛的信息
     */
    void saveContestData(BasicContestData contestData);
}
