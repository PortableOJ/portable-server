package com.portable.server.manager.impl.dev;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.helper.MemProtractedHelper;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class SolutionDevManagerImpl implements SolutionManager {

    @Resource
    private MemProtractedHelper<Solution, Long> solutionDevMapper;

    @Resource
    private MemProtractedHelper<SolutionData, String> solutionDataDevMapper;

    @Override
    public Integer countSolution(SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType) {
        return solutionDevMapper.countList(solution -> isMatch(solution, solutionType, userId, contestId, problemId, Collections.singleton(statusType)));
    }

    @Override
    public List<Solution> selectSolutionByPage(Integer pageSize, Integer offset, SolutionType solutionType, Long userId,
                                               Long contestId, Long problemId, SolutionStatusType statusType, Long beforeId, Long afterId) {
        return solutionDevMapper.searchListByPage(solution -> isMatch(solution, solutionType, userId, contestId,
                        problemId, Collections.singleton(statusType)), pageSize, offset,
                (o1, o2) -> o2.getId().compareTo(o1.getId()));
    }

    @Override
    public List<Solution> selectSolutionLastNotEndSolution(Integer pageSize) {
        List<SolutionStatusType> solutionStatusTypeList = Arrays.stream(SolutionStatusType.values())
                .filter(solutionStatusType -> !solutionStatusType.getEndingResult())
                .collect(Collectors.toList());
        return solutionDevMapper.searchListByPage(solution -> isMatch(solution, null, null,
                        null, null, solutionStatusTypeList), pageSize, 0,
                (o1, o2) -> o2.getId().compareTo(o1.getId()));
    }

    @Override
    public Optional<Solution> selectSolutionById(Long id) {
        return solutionDevMapper.getDataById(id);
    }

    @Override
    public Optional<Solution> selectLastSolution(Long userId, Long problemId) {
        return solutionDevMapper.searchFirst(solution -> isMatch(solution, null, userId, null, problemId, null));
    }

    @Override
    public Optional<Solution> selectContestLastSolution(Long userId, Long problemId, Long contestId) {
        return solutionDevMapper.searchFirst(solution -> isMatch(solution, null, userId, contestId, problemId, null));
    }

    @Override
    public void insertSolution(Solution solution) {
        solutionDevMapper.insert(solution, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateStatus(Long id, SolutionStatusType statusType) {
        solutionDevMapper.updateByFunction(id, solution -> solution.setStatus(statusType));
    }

    @Override
    public void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost) {
        solutionDevMapper.updateByFunction(id, solution -> {
            solution.setStatus(statusType);
            solution.setTimeCost(timeCost);
            solution.setMemoryCost(memoryCost);
        });
    }

    @Override
    public void updateAllStatus(List<SolutionStatusType> fromStatus, SolutionStatusType toStatus) {
        solutionDevMapper.updateByFunction(solution -> fromStatus.contains(solution.getStatus()), solution -> solution.setStatus(toStatus));
    }

    @Override
    public @NotNull SolutionData getSolutionData(String dataId) {
        return solutionDataDevMapper.getDataById(dataId).orElseThrow(PortableException.from("S-05-001"));
    }

    @Override
    public void insertSolutionData(SolutionData solutionData) {
        solutionDataDevMapper.insert(solutionData, BasicTranslateUtils::reString);
    }

    @Override
    public void saveSolutionData(SolutionData solutionData) {
        solutionDataDevMapper.updateById(solutionData);
    }

    private Boolean isMatch(Solution solution, SolutionType solutionType, Long userId, Long contestId, Long problemId, Collection<SolutionStatusType> statusType) {
        if (solutionType != null && !solutionType.equals(solution.getSolutionType())) {
            return false;
        } else if (userId != null && !userId.equals(solution.getUserId())) {
            return false;
        } else if (contestId != null && !contestId.equals(solution.getContestId())) {
            return false;
        } else if (problemId != null && !problemId.equals(solution.getProblemId())) {
            return false;
        } else {
            return statusType == null || statusType.contains(solution.getStatus());
        }
    }
}
