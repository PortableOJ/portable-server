package com.portable.server.manager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.type.ContestAccessType;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public interface ContestManager {

    /**
     * 创建新的比赛实体
     *
     * @return 比赛实体
     */
    @NotNull
    Contest newContest();

    /**
     * 创建一个新的比赛实体
     *
     * @param accessType 比赛类型
     * @return 实体信息
     * @throws PortableException 不存在此类型的比赛时候抛出
     */
    @NotNull
    BaseContestData newContestData(ContestAccessType accessType);

    /**
     * 获取所有的比赛数目
     *
     * @return 总共的比赛数目
     */
    @NotNull
    Integer getAllContestNumber();

    /**
     * 根据分页获取比赛列表
     *
     * @param pageSize 单页数量
     * @param offset   偏移量
     * @return 比赛列表
     */
    @NotNull
    List<Contest> getContestByPage(Integer pageSize, Integer offset);

    /**
     * 根据 id 获取比赛信息
     *
     * @param id 比赛 id
     * @return 比赛信息
     */
    Optional<Contest> getContestById(Long id);

    /**
     * 创建一个新的比赛
     *
     * @param contest 比赛的信息
     */
    void insertContest(Contest contest);

    /**
     * 更新比赛的所有者
     *
     * @param id       比赛的 id
     * @param newOwner 新的拥有者
     */
    void updateOwner(Long id, Long newOwner);

    /**
     * 更新比赛开始时间
     *
     * @param id           比赛的 id
     * @param newStartTime 比赛的新开始时间
     */
    void updateStartTime(Long id, Date newStartTime);

    /**
     * 更新比赛的持续时间
     *
     * @param id          比赛的 id
     * @param newDuration 比赛的新的持续时间
     */
    void updateDuration(Long id, Integer newDuration);

    /**
     * 更新比赛的访问权限
     *
     * @param id            比赛的 id
     * @param newAccessType 比赛的新的访问权限
     */
    void updateAccessType(Long id, ContestAccessType newAccessType);

    /**
     * 更新比赛的标题
     *
     * @param id    比赛的 id
     * @param title 比赛的新标题
     */
    void updateTitle(Long id, String title);

    /**
     * 获取通用的比赛信息
     *
     * @param datId      比赛的 id
     * @param accessType 比赛类型
     * @return 比赛信息
     * @throws PortableException 比赛类型错误则抛出
     */
    BaseContestData getBaseContestDataById(String datId, ContestAccessType accessType);

    /**
     * 获取公开的比赛信息
     *
     * @param datId 比赛的 id
     * @return 比赛信息
     * @throws PortableException 不存在则抛出
     */
    PublicContestData getPublicContestDataById(String datId);

    /**
     * 获取带密码的比赛信息
     *
     * @param datId 比赛的 id
     * @return 比赛信息
     * @throws PortableException 不存在则抛出
     */
    PasswordContestData getPasswordContestDataById(String datId);

    /**
     * 获取私有的比赛信息
     *
     * @param datId 比赛的 id
     * @return 比赛信息
     * @throws PortableException 不存在则抛出
     */
    PrivateContestData getPrivateContestDataById(String datId);

    /**
     * 获取提供账号邀请制的比赛信息
     *
     * @param datId 比赛的 id
     * @return 比赛信息
     * @throws PortableException 不存在则抛出
     */
    BatchContestData getBatchContestDataById(String datId);

    /**
     * 新建比赛
     *
     * @param contestData 比赛的信息
     */
    void insertContestData(BaseContestData contestData);

    /**
     * 更新比赛信息
     *
     * @param contestData 比赛的信息
     */
    void saveContestData(BaseContestData contestData);
}
