package com.portable.server.mapper.impl;

import java.util.Date;
import java.util.List;

import com.portable.server.mapper.ContestRepo;
import com.portable.server.model.contest.Contest;
import com.portable.server.type.ContestAccessType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContestRepoImpl extends BaseMemStructuredRepo<Long, Contest> implements ContestRepo {

    @Override
    public @NotNull Integer getAllContestNumber() {
        return super.countAll();
    }

    @Override
    public @NotNull List<Contest> getContestByPage(@NotNull Integer pageSize, @NotNull Integer offset) {
        return super.searchListByPageDesc(contest -> true, pageSize, offset);
    }

    @Override
    public @Nullable Contest getContestById(@NotNull Long id) {
        return super.getDataById(id);
    }

    @Override
    public void insertContest(@NotNull Contest contest) {
        super.insert(contest, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateOwner(@NotNull Long id, @NotNull Long newOwner) {
        super.updateByFunction(id, contest -> contest.setOwner(newOwner));
    }

    @Override
    public void updateStartTime(@NotNull Long id, @NotNull Date newStartTime) {
        super.updateByFunction(id, contest -> contest.setStartTime(newStartTime));
    }

    @Override
    public void updateDuration(@NotNull Long id, @NotNull Integer newDuration) {
        super.updateByFunction(id, contest -> contest.setDuration(newDuration));
    }

    @Override
    public void updateAccessType(@NotNull Long id, @NotNull ContestAccessType newAccessType) {
        super.updateByFunction(id, contest -> contest.setAccessType(newAccessType));
    }

    @Override
    public void updateTitle(@NotNull Long id, @NotNull String title) {
        super.updateByFunction(id, contest -> contest.setTitle(title));
    }
}
