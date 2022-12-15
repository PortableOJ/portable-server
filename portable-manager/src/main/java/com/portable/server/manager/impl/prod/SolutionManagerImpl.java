package com.portable.server.manager.impl.prod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.cache.CacheKvHelper;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.SolutionManager;
import com.portable.server.mapper.SolutionRepo;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.repo.SolutionDataRepo;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class SolutionManagerImpl implements SolutionManager {

    @Resource
    private SolutionRepo solutionRepo;

    @Resource
    private CacheKvHelper<Long> solutionCacheKvHelper;

    @Resource
    private SolutionDataRepo solutionDataRepo;

    @Override
    public Integer countSolution(SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType) {
        return solutionRepo.countSolution(solutionType, userId, contestId, problemId, statusType);
    }

    @Override
    public List<Solution> selectSolutionByPage(Integer pageSize, Integer offset, SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType, Long beforeId, Long afterId) {
        return solutionRepo.selectSolutionByPage(pageSize, offset, solutionType, userId, contestId, problemId, statusType == null ? null : Collections.singletonList(statusType), beforeId, afterId);
    }

    @Override
    public List<Solution> selectSolutionLastNotEndSolution(Integer pageSize) {
        List<SolutionStatusType> solutionStatusTypeList = Arrays.stream(SolutionStatusType.values())
                .filter(solutionStatusType -> !solutionStatusType.getEndingResult())
                .collect(Collectors.toList());
        return solutionRepo.selectSolutionByPage(pageSize, 0, null, null, null, null, solutionStatusTypeList, null, null);
    }

    @Override
    public Optional<Solution> selectSolutionById(Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        Solution solution = solutionCacheKvHelper.get(id, Solution.class).orElseGet(() -> solutionRepo.selectSolutionById(id));
        if (Objects.nonNull(solution)) {
            solutionCacheKvHelper.set(id, solution);
        }
        return Optional.ofNullable(solution);
    }

    @Override
    public Optional<Solution> selectLastSolution(Long userId, Long problemId) {
        return Optional.ofNullable(solutionRepo.selectLastSolutionByUserIdAndProblemId(userId, problemId));
    }

    @Override
    public Optional<Solution> selectContestLastSolution(Long userId, Long problemId, Long contestId) {
        return Optional.ofNullable(solutionRepo.selectLastSolutionByUserIdAndProblemIdAndContestId(userId, problemId, contestId));
    }

    @Override
    public void insertSolution(Solution solution) {
        solutionRepo.insertSolution(solution);
    }

    @Override
    public void updateStatus(Long id, SolutionStatusType statusType) {
        solutionRepo.updateStatus(id, statusType);
        solutionCacheKvHelper.delete(id);
    }

    @Override
    public void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost) {
        solutionRepo.updateCostAndStatus(id, statusType, timeCost, memoryCost);
        solutionCacheKvHelper.delete(id);
    }

    @Override
    public void updateAllStatus(List<SolutionStatusType> fromStatus, SolutionStatusType toStatus) {
        solutionRepo.updateAllStatus(fromStatus, toStatus);
    }

    @Override
    public @NotNull SolutionData getSolutionData(String dataId) {
        return Optional.ofNullable(solutionDataRepo.getSolutionData(dataId)).orElseThrow(PortableException.from("S-05-001"));
    }

    @Override
    public void insertSolutionData(SolutionData solutionData) {
        solutionDataRepo.insertSolutionData(solutionData);
    }

    @Override
    public void saveSolutionData(SolutionData solutionData) {
        solutionDataRepo.saveSolutionData(solutionData);
    }
}
