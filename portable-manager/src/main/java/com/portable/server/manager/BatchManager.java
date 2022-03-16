package com.portable.server.manager;

import com.portable.server.model.batch.Batch;
import com.portable.server.type.BatchStatusType;

import java.util.List;

/**
 * @author shiroha
 */
public interface BatchManager {

    /**
     * 创建一个批量用户
     *
     * @return 新的批量用户
     */
    Batch newBatch();

    /**
     * 统计某个用户的所有批量用户数据
     *
     * @param ownerId 拥有者 ID
     * @return 总数量
     */
    Integer countBatchListByOwnerId(Long ownerId);

    /**
     * 分页获取批量用户的信息
     *
     * @param ownerId  用户 ID
     * @param pageSize 分页大小
     * @param offset   偏移量
     * @return 批量用户信息列表
     */
    List<Batch> selectBatchByPage(Long ownerId,
                                  Integer pageSize,
                                  Integer offset);

    /**
     * 基于批量用户 id 查找批量用户
     *
     * @param id 批量用户 ID
     * @return 批量用户
     */
    Batch selectBatchById(Long id);

    /**
     * 基于公共前缀查找批量用户
     *
     * @param prefix 前缀
     * @return 批量用户信息
     */
    Batch selectBatchByPrefix(String prefix);

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
    void updateBatchStatus(Long id, BatchStatusType newStatus);

    /**
     * 更新批量用户绑定至的比赛
     *
     * @param id        用户 ID
     * @param newContest 新比赛
     */
    void updateBatchContest(Long id, Long newContest);
}
