package com.portable.server.mapper;

import com.portable.server.model.batch.Batch;
import com.portable.server.type.BatchStatusType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author shiroha
 */
@Repository
public interface BatchMapper {

    /**
     * 统计某个用户的所有批量用户数据
     *
     * @param ownerId 拥有者 ID
     * @return 总数量
     */
    Integer countBatchListByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 分页获取批量用户的信息
     *
     * @param ownerId  用户 ID
     * @param pageSize 分页大小
     * @param offset   偏移量
     * @return 批量用户信息列表
     */
    List<Batch> selectBatchByPage(@Param("ownerId") Long ownerId,
                                  @Param("pageSize") Integer pageSize,
                                  @Param("offset") Integer offset);

    /**
     * 基于批量用户 id 查找批量用户
     *
     * @param id 批量用户 ID
     * @return 批量用户
     */
    Batch selectBatchById(@Param("id") Long id);

    /**
     * 基于公共前缀查找批量用户
     *
     * @param prefix 前缀
     * @return 批量用户信息
     */
    Batch selectBatchByPrefix(@Param("prefix") String prefix);

    /**
     * 新增一个批量用户
     *
     * @param batch 批量用户信息
     */
    void insertBatch(Batch batch);

    /**
     * 更新批量用户状态
     *
     * @param id        用户 ID
     * @param newStatus 新状态
     */
    void updateBatchStatus(@Param("id") Long id, @Param("newStatus") BatchStatusType newStatus);

    /**
     * 更新批量用户绑定至的比赛
     *
     * @param id         用户 ID
     * @param newContest 新比赛
     */
    void updateBatchContest(@Param("id") Long id, @Param("newContest") Long newContest);

    /**
     * 修改批量用户组的 IP 锁状态
     *
     * @param id     用户组 ID
     * @param ipLock 新的锁状态
     */
    void updateBatchIpLock(@Param("id") Long id, @Param("ipLock") Boolean ipLock);
}
