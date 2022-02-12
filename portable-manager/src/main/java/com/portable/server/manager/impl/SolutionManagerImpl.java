package com.portable.server.manager.impl;

import com.portable.server.manager.SolutionManager;
import com.portable.server.mapper.SolutionMapper;
import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
    public Integer countPublicSolution(Long userId, Long problemId, SolutionStatusType statusType) {
        return solutionMapper.countPublicSolution(userId, problemId, statusType);
    }

    @Override
    public Integer countSolutionByContest(Long contestId) {
        return solutionMapper.countSolutionByContest(contestId);
    }

    @Override
    public List<Solution> selectPublicSolutionByPage(Integer pageSize, Integer offset, Long userId, Long problemId, SolutionStatusType statusType) {
        return solutionMapper.selectPublicSolutionByPage(pageSize, offset, userId, problemId, statusType);
    }

    @Override
    public List<Solution> selectSolutionByContestAndPage(Integer pageSize, Integer offset, Long contestId) {
        return solutionMapper.selectSolutionByContestAndPage(pageSize, offset, contestId);
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
}
