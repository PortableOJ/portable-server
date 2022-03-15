package com.portable.server.manager.impl;

import com.portable.server.manager.BatchManager;
import com.portable.server.mapper.BatchMapper;
import com.portable.server.model.batch.Batch;
import com.portable.server.type.BatchStatusType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shiroha
 */
@Component
public class BatchManagerImpl implements BatchManager {

    @Resource
    private BatchMapper batchMapper;

    @Override
    public Batch newBatch() {
        return Batch.builder()
                .id(null)
                .owner(null)
                .contest(null)
                .prefix("")
                .count(0)
                .ipLock(false)
                .type(BatchStatusType.DISABLE)
                .build();
    }

    @Override
    public Integer countBatchListByOwnerId(Long ownerId) {
        return batchMapper.countBatchListByOwnerId(ownerId);
    }

    @Override
    public List<Batch> selectBatchByPage(Long ownerId, Integer pageSize, Integer offset) {
        return batchMapper.selectBatchByPage(ownerId, pageSize, offset);
    }

    @Override
    public Batch selectBatchByPrefix(String prefix) {
        return batchMapper.selectBatchByPrefix(prefix);
    }

    @Override
    public void insertBatch(Batch batch) {
        batchMapper.insertBatch(batch);
    }

    @Override
    public void updateBatchStatus(Long id, BatchStatusType newStatus) {
        batchMapper.updateBatchStatus(id, newStatus);
    }
}
