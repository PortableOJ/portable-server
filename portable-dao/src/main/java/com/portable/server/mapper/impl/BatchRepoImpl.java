package com.portable.server.mapper.impl;

import java.util.List;

import com.portable.server.mapper.BatchRepo;
import com.portable.server.model.batch.Batch;
import com.portable.server.type.BatchStatusType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatchRepoImpl extends BaseMemStructuredRepo<Long, Batch> implements BatchRepo {

    @Override
    public @NotNull Integer countBatchListByOwnerId(@NotNull Long ownerId) {
        return super.countList(batch -> ownerId.equals(batch.getOwner()));
    }

    @Override
    public @NotNull List<Batch> selectBatchByPage(@NotNull Long ownerId, @NotNull Integer pageSize, @NotNull Integer offset) {
        return super.searchListByPageDesc(batch -> ownerId.equals(batch.getOwner()), pageSize, offset);
    }

    @Override
    public @Nullable Batch selectBatchById(@NotNull Long id) {
        return super.getDataById(id);
    }

    @Override
    public @Nullable Batch selectBatchByPrefix(@NotNull String prefix) {
        return super.searchFirstDesc(batch -> prefix.equals(batch.getPrefix()));
    }

    @Override
    public void insertBatch(@NotNull Batch batch) {
        super.insert(batch, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateBatchStatus(@NotNull Long id, @NotNull BatchStatusType newStatus) {
        super.updateByFunction(id, batch -> batch.setStatus(newStatus));
    }

    @Override
    public void updateBatchContest(@NotNull Long id, @NotNull Long newContest) {
        super.updateByFunction(id, batch -> batch.setContestId(newContest));
    }

    @Override
    public void updateBatchIpLock(@NotNull Long id, @NotNull Boolean ipLock) {
        super.updateByFunction(id, batch -> batch.setIpLock(ipLock));
    }
}
