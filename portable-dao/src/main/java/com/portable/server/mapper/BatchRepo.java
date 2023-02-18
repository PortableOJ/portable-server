package com.portable.server.mapper;

import java.util.List;

import com.portable.server.model.batch.Batch;
import com.portable.server.type.BatchStatusType;

import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * @author shiroha
 */
@Repository
public interface BatchRepo {

    /**
     * 统计某个用户的所有批量用户数据
     *
     * @param ownerId 拥有者 ID
     * @return 总数量
     */
    @NotNull Integer countBatchListByOwnerId(@NotNull @Param("ownerId") Long ownerId);

    /**
     * 分页获取批量用户的信息
     *
     * @param ownerId  用户 ID
     * @param pageSize 分页大小
     * @param offset   偏移量
     * @return 批量用户信息列表
     */
    @NotNull List<Batch> selectBatchByPage(@NotNull @Param("ownerId") Long ownerId,
                                           @NotNull @Param("pageSize") Integer pageSize,
                                           @NotNull @Param("offset") Integer offset);

    /**
     * 基于批量用户 id 查找批量用户
     *
     * @param id 批量用户 ID
     * @return 批量用户
     */
    @Nullable Batch selectBatchById(@NotNull @Param("id") Long id);

    /**
     * 基于公共前缀查找批量用户
     *
     * @param prefix 前缀
     * @return 批量用户信息
     */
    @Nullable Batch selectBatchByPrefix(@NotNull @Param("prefix") String prefix);

    /**
     * 新增一个批量用户
     *
     * @param batch 批量用户信息
     */
    void insertBatch(@NotNull Batch batch);

    /**
     * 更新批量用户状态
     *
     * @param id        用户 ID
     * @param newStatus 新状态
     */
    void updateBatchStatus(@NotNull @Param("id") Long id, @NotNull @Param("newStatus") BatchStatusType newStatus);

    /**
     * 更新批量用户绑定至的比赛
     *
     * @param id         用户 ID
     * @param newContest 新比赛
     */
    void updateBatchContest(@NotNull @Param("id") Long id, @NotNull @Param("newContest") Long newContest);

    /**
     * 修改批量用户组的 IP 锁状态
     *
     * @param id     用户组 ID
     * @param ipLock 新的锁状态
     */
    void updateBatchIpLock(@NotNull @Param("id") Long id, @NotNull @Param("ipLock") Boolean ipLock);
}
