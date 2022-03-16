package com.portable.server.manager;

import com.portable.server.model.contest.Contest;
import com.portable.server.type.ContestAccessType;

import java.util.Date;
import java.util.List;

/**
 * @author shiroha
 */
public interface ContestManager {

    /**
     * 创建新的比赛实体
     * @return 比赛实体
     */
    Contest insertContest();

    /**
     * 获取所有的比赛数目
     * @return 总共的比赛数目
     */
    Integer getAllContestNumber();

    /**
     * 根据分页获取比赛列表
     * @param pageSize 单页数量
     * @param offset 偏移量
     * @return 比赛列表
     */
    List<Contest> getContestByPage(Integer pageSize, Integer offset);

    /**
     * 根据 id 获取比赛信息
     * @param id 比赛 id
     * @return 比赛信息
     */
    Contest getContestById(Long id);

    /**
     * 创建一个新的比赛
     * @param contest 比赛的信息
     */
    void insertContest(Contest contest);

    /**
     * 更新比赛的所有者
     * @param id 比赛的 id
     * @param newOwner 新的拥有者
     */
    void updateOwner(Long id, Long newOwner);

    /**
     * 更新比赛开始时间
     * @param id 比赛的 id
     * @param newStartTime 比赛的新开始时间
     */
    void updateStartTime(Long id, Date newStartTime);

    /**
     * 更新比赛的持续时间
     * @param id 比赛的 id
     * @param newDuration 比赛的新的持续时间
     */
    void updateDuration(Long id, Integer newDuration);

    /**
     * 更新比赛的访问权限
     * @param id 比赛的 id
     * @param newAccessType 比赛的新的访问权限
     */
    void updateAccessType(Long id, ContestAccessType newAccessType);

}
