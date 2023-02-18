package com.portable.server.mapper.impl;

import java.util.List;
import java.util.Objects;

import com.portable.server.mapper.SolutionRepo;
import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.BasicTranslateUtils;
import com.portable.server.util.PortableUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolutionRepoImpl extends BaseMemStructuredRepo<Long, Solution> implements SolutionRepo {

    @Override
    public @NotNull Integer countSolution(@Nullable SolutionType solutionType, @Nullable Long userId, @Nullable Long contestId, @Nullable Long problemId, @Nullable SolutionStatusType statusType) {
        return super.countList(solution -> this.filter(solution, solutionType, userId, contestId, problemId,
                PortableUtils.singletonListIfNotNull(statusType), null, null));
    }

    @Override
    public @NotNull List<Solution> selectSolutionByPage(@NotNull Integer pageSize, @NotNull Integer offset, @Nullable SolutionType solutionType, @Nullable Long userId, @Nullable Long contestId, @Nullable Long problemId, @Nullable List<SolutionStatusType> statusType, @Nullable Long beforeId, @Nullable Long afterId) {
        return super.searchListByPageAsc(
                solution -> this.filter(solution, solutionType, userId, contestId, problemId, statusType, beforeId, afterId),
                pageSize, offset);
    }

    @Override
    public @NotNull List<Solution> selectNotUserSolution(@NotNull Integer num, @NotNull Long userId) {
        return super.searchListByPageAsc(solution -> !Objects.equals(userId, solution.getUserId()), num, 0);
    }

    @Override
    public @Nullable Solution selectSolutionById(@NotNull Long id) {
        return super.getDataById(id);
    }

    @Override
    public @Nullable Solution selectLastSolutionByUserIdAndProblemId(@NotNull Long userId, @NotNull Long problemId) {
        return super.searchFirstAsc(
                solution -> this.filter(solution, null, userId, null, problemId, null, null, null));
    }

    @Override
    public @Nullable Solution selectLastSolutionByUserIdAndProblemIdAndContestId(@NotNull Long userId, @NotNull Long problemId, @NotNull Long contestId) {
        return super.searchFirstAsc(
                solution -> this.filter(solution, null, userId, contestId, problemId, null, null, null));
    }

    @Override
    public void insertSolution(@NotNull Solution solution) {
        super.insert(solution, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateStatus(@NotNull Long id, @NotNull SolutionStatusType statusType) {
        super.updateByFunction(id, solution -> solution.setStatus(statusType));
    }

    @Override
    public void updateCostAndStatus(@NotNull Long id, @NotNull SolutionStatusType statusType, @Nullable Integer timeCost, @Nullable Integer memoryCost) {
        super.updateByFunction(id, solution -> {
            solution.setStatus(statusType);
            solution.setTimeCost(timeCost == null ? solution.getTimeCost() : Integer.max(timeCost, solution.getTimeCost()));
            solution.setMemoryCost(memoryCost == null ? solution.getMemoryCost() : Integer.max(memoryCost, solution.getMemoryCost()));
        });
    }

    @Override
    public void updateAllStatus(@NotNull List<SolutionStatusType> fromStatus, @NotNull SolutionStatusType toStatus) {
        super.updateByFunction(solution -> fromStatus.contains(solution.getStatus()), solution -> solution.setStatus(toStatus));
    }

    private @NotNull Boolean filter(@NotNull Solution solution, @Nullable SolutionType solutionType, @Nullable Long userId, @Nullable Long contestId, @Nullable Long problemId, @Nullable List<SolutionStatusType> statusTypeList, @Nullable Long beforeId, @Nullable Long afterId) {
        return PortableUtils.equalOrNull(solutionType, solution.getSolutionType())
                && PortableUtils.equalOrNull(userId, solution.getUserId())
                && PortableUtils.equalOrNull(contestId, solution.getContestId())
                && PortableUtils.equalOrNull(problemId, solution.getProblemId())
                && (statusTypeList == null || statusTypeList.contains(solution.getStatus()))
                && (beforeId == null || beforeId.compareTo(solution.getId()) > 0)
                && (afterId == null || afterId.compareTo(solution.getId()) < 0);
    }
}
