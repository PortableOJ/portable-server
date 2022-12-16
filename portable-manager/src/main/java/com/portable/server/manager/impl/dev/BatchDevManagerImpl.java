package com.portable.server.manager.impl.dev;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;

import com.portable.server.manager.BatchManager;
import com.portable.server.model.batch.Batch;
import com.portable.server.persistent.StructuredHelper;
import com.portable.server.type.BatchStatusType;
import com.portable.server.util.BasicTranslateUtils;

/**
 * @author shiroha
 */
public class BatchDevManagerImpl implements BatchManager {

    @Resource(name = "batchDevMapper")
    private StructuredHelper<Long, Batch> batchDevMapper;

    @Override
    public Integer countBatchListByOwnerId(Long ownerId) {
        return batchDevMapper.countList(batch -> Objects.equals(batch.getOwner(), ownerId));
    }

    @Override
    public List<Batch> selectBatchByPage(Long ownerId, Integer pageSize, Integer offset) {
        return batchDevMapper.searchListByPage(batch -> Objects.equals(batch.getOwner(), ownerId), pageSize, offset);
    }

    @Override
    public Optional<Batch> selectBatchById(Long id) {
        return batchDevMapper.getDataById(id);
    }

    @Override
    public Optional<Batch> selectBatchByPrefix(String prefix) {
        return batchDevMapper.searchFirst(batch -> Objects.equals(batch.getPrefix(), prefix));
    }

    @Override
    public void insertBatch(Batch batch) {
        batchDevMapper.insert(batch, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateBatchStatus(Long id, BatchStatusType newStatus) {
        batchDevMapper.updateByFunction(id, batch -> batch.setStatus(newStatus));
    }

    @Override
    public void updateBatchContest(Long id, Long newContest) {
        batchDevMapper.updateByFunction(id, batch -> batch.setContestId(newContest));
    }

    @Override
    public void updateBatchIpLock(Long id, Boolean ipLock) {
        batchDevMapper.updateByFunction(id, batch -> batch.setIpLock(ipLock));
    }
}
