package com.portable.server.manager.impl.prod;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.cache.CacheKvHelper;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.ProblemManager;
import com.portable.server.mapper.ProblemRepo;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.repo.ProblemDataRepo;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class ProblemManagerImpl implements ProblemManager {

    @Resource
    private ProblemRepo problemRepo;

    @Resource
    private CacheKvHelper<Long> cacheKvHelper;

    @Resource
    private ProblemDataRepo problemDataRepo;

    @Override
    public @NotNull Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessTypeList, Long ownerId) {
        return problemRepo.countProblemListByTypeAndOwnerId(accessTypeList, ownerId);
    }

    @Override
    public @NotNull List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessTypeList, Long ownerId, Integer pageSize, Integer offset) {
        return problemRepo.selectProblemListByPageAndTypeAndOwnerId(accessTypeList, ownerId, pageSize, offset);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByTypedAndKeyword(List<ProblemAccessType> accessTypeList, String keyword, Integer num) {
        return problemRepo.selectRecentProblemByTypeAndKeyword(accessTypeList, keyword, num);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByOwnerIdAndKeyword(Long ownerId, String keyword, Integer num) {
        return problemRepo.selectPrivateProblemByKeyword(ownerId, keyword, num);
    }

    @Override
    public @NotNull Optional<Problem> getProblemById(Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        Problem problem = cacheKvHelper.get(id, Problem.class)
                .orElseGet(() -> problemRepo.selectProblemById(id));
        if (Objects.nonNull(problem)) {
            cacheKvHelper.set(id, problem);
        }
        return Optional.ofNullable(problem);
    }

    @Override
    public @NotNull List<Long> checkProblemListExist(List<Long> problemList) {
        return problemList.stream()
                .map(aLong -> {
                    Optional<Problem> problem = getProblemById(aLong);
                    return !problem.isPresent() ? aLong : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void insertProblem(Problem problem) {
        problemRepo.insertProblem(problem);
    }

    @Override
    public void updateProblemTitle(Long id, String newTitle) {
        problemRepo.updateProblemTitle(id, newTitle);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemAccessStatus(Long id, ProblemAccessType newStatus) {
        problemRepo.updateProblemAccess(id, newStatus);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemStatus(Long id, ProblemStatusType statusType) {
        problemRepo.updateProblemStatus(id, statusType);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemCount(Long id, Integer submitCount, Integer acceptCount) {
        problemRepo.updateProblemCount(id, submitCount, acceptCount);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemOwner(Long id, Long newOwner) {
        problemRepo.updateProblemOwner(id, newOwner);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus) {
        problemRepo.updateAllStatus(fromStatus, toStatus);
    }

    @Override
    public @NotNull ProblemData getProblemData(String dataId) {
        return Optional.ofNullable(problemDataRepo.getProblemData(dataId))
                .orElseThrow(PortableException.from("S-03-001"));
    }

    @Override
    public void insertProblemData(ProblemData problemData) {
        problemDataRepo.insertProblemData(problemData);
    }

    @Override
    public void updateProblemData(ProblemData problemData) {
        problemDataRepo.saveProblemData(problemData);
    }
}
