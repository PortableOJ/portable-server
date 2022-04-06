package com.portable.server.mapper;

import com.portable.server.model.contest.Contest;
import com.portable.server.type.ContestAccessType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author shiroha
 */
@Repository
public interface ContestMapper {

    /**
     * 获取所有的比赛数目
     *
     * @return 总共的比赛数目
     */
    Integer getAllContestNumber();

    /**
     * 根据分页获取比赛列表
     *
     * @param pageSize 单页数量
     * @param offset   偏移量
     * @return 比赛列表
     */
    List<Contest> getContestByPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

    /**
     * 根据 id 获取比赛信息
     *
     * @param id 比赛 id
     * @return 比赛信息
     */
    Contest getContestById(Long id);

    /**
     * 创建一个新的比赛
     *
     * @param contest 比赛的信息
     */
    void newContest(Contest contest);

    /**
     * 更新比赛的所有者
     *
     * @param id       比赛的 id
     * @param newOwner 新的拥有者
     */
    void updateOwner(@Param("id") Long id, @Param("newOwner") Long newOwner);

    /**
     * 更新比赛开始时间
     *
     * @param id           比赛的 id
     * @param newStartTime 比赛的新开始时间
     */
    void updateStartTime(@Param("id") Long id, @Param("newStartTime") Date newStartTime);

    /**
     * 更新比赛的持续时间
     *
     * @param id          比赛的 id
     * @param newDuration 比赛的新的持续时间
     */
    void updateDuration(@Param("id") Long id, @Param("newDuration") Integer newDuration);

    /**
     * 更新比赛的访问权限
     *
     * @param id            比赛的 id
     * @param newAccessType 比赛的新的访问权限
     */
    void updateAccessType(@Param("id") Long id, @Param("newAccessType") ContestAccessType newAccessType);

    /**
     * 更新比赛的标题
     *
     * @param id    比赛的 id
     * @param title 比赛的标题
     */
    void updateTitle(@Param("id") Long id, @Param("title") String title);
}
