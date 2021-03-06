package com.portable.server.manager.impl;

import com.portable.server.kit.RedisValueKit;
import com.portable.server.manager.SolutionManager;
import com.portable.server.mapper.SolutionMapper;
import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class SolutionManagerImpl implements SolutionManager {

    @Resource
    private SolutionMapper solutionMapper;

    @Resource
    private RedisValueKit redisValueKit;

    /**
     * redis 的 key 和过期时间
     */
    private static final String REDIS_PREFIX = "SOLUTION_ID";
    private static final Long REDIS_TIME = 5L;

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
    public List<Solution> selectSolutionByPage(Integer pageSize, Integer offset, SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType, Long beforeId, Long afterId) {
        return solutionMapper.selectSolutionByPage(pageSize, offset, solutionType, userId, contestId, problemId, statusType == null ? null : Collections.singletonList(statusType), beforeId, afterId);
    }

    @Override
    public List<Solution> selectSolutionLastNotEndSolution(Integer pageSize) {
        List<SolutionStatusType> solutionStatusTypeList = Arrays.stream(SolutionStatusType.values())
                .filter(solutionStatusType -> !solutionStatusType.getEndingResult())
                .collect(Collectors.toList());
        return solutionMapper.selectSolutionByPage(pageSize, 0, null, null, null, null, solutionStatusTypeList, null, null);
    }

    @Override
    public Optional<Solution> selectSolutionById(Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        Solution solution = redisValueKit.get(REDIS_PREFIX, id, Solution.class).orElseGet(() -> solutionMapper.selectSolutionById(id));
        if (Objects.nonNull(solution)) {
            redisValueKit.set(REDIS_PREFIX, id, solution, REDIS_TIME);
        }
        return Optional.ofNullable(solution);
    }

    @Override
    public Optional<Solution> selectLastSolution(Long userId, Long problemId) {
        return Optional.ofNullable(solutionMapper.selectLastSolutionByUserIdAndProblemId(userId, problemId));
    }

    @Override
    public Optional<Solution> selectContestLastSolution(Long userId, Long problemId, Long contestId) {
        return Optional.ofNullable(solutionMapper.selectLastSolutionByUserIdAndProblemIdAndContestId(userId, problemId, contestId));
    }

    @Override
    public void insertSolution(Solution solution) {
        solutionMapper.insertSolution(solution);
    }

    @Override
    public void updateStatus(Long id, SolutionStatusType statusType) {
        solutionMapper.updateStatus(id, statusType);
        redisValueKit.getPeek(REDIS_PREFIX, id, Solution.class, REDIS_TIME, solution -> solution.setStatus(statusType));
    }

    @Override
    public void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost) {
        solutionMapper.updateCostAndStatus(id, statusType, timeCost, memoryCost);
        redisValueKit.getPeek(REDIS_PREFIX, id, Solution.class, REDIS_TIME, solution -> {
            solution.setStatus(statusType);
            solution.setTimeCost(ObjectUtils.max(solution.getTimeCost(), timeCost, Integer::compareTo));
            solution.setMemoryCost(ObjectUtils.max(solution.getMemoryCost(), memoryCost, Integer::compareTo));
        });
    }

    @Override
    public void updateAllStatus(List<SolutionStatusType> fromStatus, SolutionStatusType toStatus) {
        solutionMapper.updateAllStatus(fromStatus, toStatus);
    }
}
