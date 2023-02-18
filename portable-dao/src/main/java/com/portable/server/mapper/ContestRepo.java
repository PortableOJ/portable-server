package com.portable.server.mapper;

import java.util.Date;
import java.util.List;

import com.portable.server.model.contest.Contest;
import com.portable.server.type.ContestAccessType;

import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * @author shiroha
 */
@Repository
public interface ContestRepo {

    /**
     * 获取所有的比赛数目
     *
     * @return 总共的比赛数目
     */
    @NotNull Integer getAllContestNumber();

    /**
     * 根据分页获取比赛列表
     *
     * @param pageSize 单页数量
     * @param offset   偏移量
     * @return 比赛列表
     */
    @NotNull List<Contest> getContestByPage(@NotNull @Param("pageSize") Integer pageSize,
                                            @NotNull @Param("offset") Integer offset);

    /**
     * 根据 id 获取比赛信息
     *
     * @param id 比赛 id
     * @return 比赛信息
     */
    @Nullable Contest getContestById(@NotNull Long id);

    /**
     * 创建一个新的比赛
     *
     * @param contest 比赛的信息
     */
    void insertContest(@NotNull Contest contest);

    /**
     * 更新比赛的所有者
     *
     * @param id       比赛的 id
     * @param newOwner 新的拥有者
     */
    void updateOwner(@NotNull @Param("id") Long id, @NotNull @Param("newOwner") Long newOwner);

    /**
     * 更新比赛开始时间
     *
     * @param id           比赛的 id
     * @param newStartTime 比赛的新开始时间
     */
    void updateStartTime(@NotNull @Param("id") Long id, @NotNull @Param("newStartTime") Date newStartTime);

    /**
     * 更新比赛的持续时间
     *
     * @param id          比赛的 id
     * @param newDuration 比赛的新的持续时间
     */
    void updateDuration(@NotNull @Param("id") Long id, @NotNull @Param("newDuration") Integer newDuration);

    /**
     * 更新比赛的访问权限
     *
     * @param id            比赛的 id
     * @param newAccessType 比赛的新的访问权限
     */
    void updateAccessType(@NotNull @Param("id") Long id, @NotNull @Param("newAccessType") ContestAccessType newAccessType);

    /**
     * 更新比赛的标题
     *
     * @param id    比赛的 id
     * @param title 比赛的标题
     */
    void updateTitle(@NotNull @Param("id") Long id, @NotNull @Param("title") String title);
}
