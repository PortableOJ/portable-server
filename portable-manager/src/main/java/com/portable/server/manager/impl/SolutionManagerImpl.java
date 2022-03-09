package com.portable.server.manager.impl;

import com.portable.server.manager.SolutionManager;
import com.portable.server.mapper.SolutionMapper;
import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class SolutionManagerImpl implements SolutionManager {

    @Resource
    private SolutionMapper solutionMapper;

    @Override
    public Solution newSolution() {
        return Solution.builder()
                .id(null)
                .dataId(null)
                .submitTime(new Date())
                .userId(null)
                .problemId(null)
                .contestId(null)
                .languageType(null)
                .status(SolutionStatusType.PENDING)
                .solutionType(null)
                .timeCost(null)
                .memoryCost(null)
                .build();
    }

    @Override
    public Integer countSolution(SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType) {
        return solutionMapper.countSolution(solutionType, userId, contestId, problemId, statusType);
    }

    @Override
    public List<Solution> selectSolutionByPage(Integer pageSize, Integer offset, SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType) {
        return solutionMapper.selectSolutionByPage(pageSize, offset, solutionType, userId, contestId, problemId, Collections.singletonList(statusType));
    }

    @Override
    public List<Solution> selectSolutionLastNotEndSolution(Integer pageSize) {
        List<SolutionStatusType> solutionStatusTypeList = Arrays.stream(SolutionStatusType.values())
                .filter(solutionStatusType -> !solutionStatusType.getEndingResult())
                .collect(Collectors.toList());
        return solutionMapper.selectSolutionByPage(pageSize, 0, null, null, null, null, solutionStatusTypeList);
    }

    @Override
    public Solution selectSolutionById(Long id) {
        return solutionMapper.selectSolutionById(id);
    }

    @Override
    public Solution selectLastSolutionByUserIdAndProblemId(Long userId, Long problemId) {
        return solutionMapper.selectLastSolutionByUserIdAndProblemId(userId, problemId);
    }

    @Override
    public Solution selectLastSolutionByUserIdAndProblemIdAndContestId(Long userId, Long problemId, Long contestId) {
        return solutionMapper.selectLastSolutionByUserIdAndProblemIdAndContestId(userId, problemId, contestId);
    }

    @Override
    public void insertSolution(Solution solution) {
        solutionMapper.insertSolution(solution);
    }

    @Override
    public void updateStatus(Long id, SolutionStatusType statusType) {
        solutionMapper.updateStatus(id, statusType);
    }

    @Override
    public void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost) {
        solutionMapper.updateCostAndStatus(id, statusType, timeCost, memoryCost);
    }

    @Override
    public void updateAllStatus(List<SolutionStatusType> fromStatus, SolutionStatusType toStatus) {
        solutionMapper.updateAllStatus(fromStatus, toStatus);
    }
}
