package com.portable.server.manager.impl.prod;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import com.portable.server.manager.BatchManager;
import com.portable.server.mapper.BatchMapper;
import com.portable.server.model.batch.Batch;
import com.portable.server.type.BatchStatusType;

import org.springframework.stereotype.Component;

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
                .contestId(null)
                .prefix("")
                .count(0)
                .ipLock(false)
                .status(BatchStatusType.DISABLE)
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
    public Optional<Batch> selectBatchById(Long id) {
        return Optional.ofNullable(batchMapper.selectBatchById(id));
    }

    @Override
    public Optional<Batch> selectBatchByPrefix(String prefix) {
        return Optional.ofNullable(batchMapper.selectBatchByPrefix(prefix));
    }

    @Override
    public void insertBatch(Batch batch) {
        batchMapper.insertBatch(batch);
    }

    @Override
    public void updateBatchStatus(Long id, BatchStatusType newStatus) {
        batchMapper.updateBatchStatus(id, newStatus);
    }

    @Override
    public void updateBatchContest(Long id, Long newContest) {
        batchMapper.updateBatchContest(id, newContest);
    }

    @Override
    public void updateBatchIpLock(Long id, Boolean ipLock) {
        batchMapper.updateBatchIpLock(id, ipLock);
    }
}
