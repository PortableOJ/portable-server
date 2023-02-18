package com.portable.server.manager.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import com.portable.server.manager.BatchManager;
import com.portable.server.mapper.BatchRepo;
import com.portable.server.model.batch.Batch;
import com.portable.server.type.BatchStatusType;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class BatchManagerImpl implements BatchManager {

    @Resource
    private BatchRepo batchRepo;

    @Override
    public Integer countBatchListByOwnerId(Long ownerId) {
        return batchRepo.countBatchListByOwnerId(ownerId);
    }

    @Override
    public List<Batch> selectBatchByPage(Long ownerId, Integer pageSize, Integer offset) {
        return batchRepo.selectBatchByPage(ownerId, pageSize, offset);
    }

    @Override
    public Optional<Batch> selectBatchById(Long id) {
        return Optional.ofNullable(batchRepo.selectBatchById(id));
    }

    @Override
    public Optional<Batch> selectBatchByPrefix(String prefix) {
        return Optional.ofNullable(batchRepo.selectBatchByPrefix(prefix));
    }

    @Override
    public void insertBatch(Batch batch) {
        batchRepo.insertBatch(batch);
    }

    @Override
    public void updateBatchStatus(Long id, BatchStatusType newStatus) {
        batchRepo.updateBatchStatus(id, newStatus);
    }

    @Override
    public void updateBatchContest(Long id, Long newContest) {
        batchRepo.updateBatchContest(id, newContest);
    }

    @Override
    public void updateBatchIpLock(Long id, Boolean ipLock) {
        batchRepo.updateBatchIpLock(id, ipLock);
    }
}
