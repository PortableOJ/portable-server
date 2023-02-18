package com.portable.server.mapper.impl;

import java.util.List;
import java.util.Objects;

import com.portable.server.mapper.ProblemRepo;
import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProblemRepoImpl extends BaseMemStructuredRepo<Long, Problem> implements ProblemRepo {

    @Override
    public @NotNull Integer countProblemListByTypeAndOwnerId(@NotNull List<ProblemAccessType> accessTypeList, @Nullable Long userId) {
        return super.countList(problem -> ProblemRepoImpl.filter(problem, accessTypeList, userId));
    }

    @Override
    public @NotNull List<Problem> selectProblemListByPageAndTypeAndOwnerId(@NotNull List<ProblemAccessType> accessTypeList, @Nullable Long userId, @NotNull Integer pageSize, @NotNull Integer offset) {
        return super.searchListByPageAsc(problem -> ProblemRepoImpl.filter(problem, accessTypeList, userId), pageSize, offset);
    }

    @Override
    public @NotNull List<Problem> selectRecentProblemByTypeAndKeyword(@NotNull List<ProblemAccessType> accessTypeList, @NotNull String keyword, @NotNull Integer num) {
        return super.searchListByPageDesc(problem -> {
            if (!accessTypeList.contains(problem.getAccessType())) {
                return false;
            }

            return problem.getTitle() != null && problem.getTitle().contains(keyword);
        }, num, 0);
    }

    @Override
    public @NotNull List<Problem> selectPrivateProblemByKeyword(@NotNull Long userId, @NotNull String keyword, @NotNull Integer num) {
        return super.searchListByPageDesc(problem -> {
            if (Objects.equals(userId, problem.getOwner())) {
                return false;
            }

            return problem.getTitle() != null && problem.getTitle().contains(keyword);
        }, num, 0);
    }

    @Override
    public @Nullable Problem selectProblemById(@NotNull Long id) {
        return super.getDataById(id);
    }

    @Override
    public void insertProblem(@NotNull Problem problem) {
        super.insert(problem, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateProblemTitle(@NotNull Long id, @NotNull String title) {
        super.updateByFunction(id, problem -> problem.setTitle(title));
    }

    @Override
    public void updateProblemAccess(@NotNull Long id, @NotNull ProblemAccessType status) {
        super.updateByFunction(id, problem -> problem.setAccessType(status));
    }

    @Override
    public void updateProblemStatus(@NotNull Long id, @NotNull ProblemStatusType statusType) {
        super.updateByFunction(id, problem -> problem.setStatusType(statusType));
    }

    @Override
    public void updateProblemCount(@NotNull Long id, @NotNull Integer submitCount, @NotNull Integer acceptCount) {
        super.updateByFunction(id, problem -> {
            problem.setAcceptCount(problem.getAcceptCount() + acceptCount);
            problem.setSubmissionCount(problem.getSubmissionCount() + submitCount);
        });
    }

    @Override
    public void updateProblemOwner(@NotNull Long id, @NotNull Long newOwner) {
        super.updateByFunction(id, problem -> problem.setOwner(newOwner));
    }

    @Override
    public void updateAllStatus(@NotNull ProblemStatusType fromStatus, @NotNull ProblemStatusType toStatus) {
        super.updateByFunction(problem -> fromStatus.equals(problem.getStatusType()), problem -> problem.setStatusType(toStatus));
    }

    private static @NotNull Boolean filter(@NotNull Problem problem, @NotNull List<ProblemAccessType> accessTypeList, @Nullable Long userId) {
        return accessTypeList.contains(problem.getAccessType())
                || (userId != null && userId.equals(problem.getOwner()));
    }
}
