package com.portable.server.manager.impl;

import com.portable.server.kit.RedisValueKit;
import com.portable.server.manager.ProblemManager;
import com.portable.server.mapper.ProblemMapper;
import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class ProblemManagerImpl implements ProblemManager {

    @Resource
    private ProblemMapper problemMapper;

    @Resource
    private RedisValueKit redisValueKit;

    /**
     * redis 的 key 和过期时间
     */
    private static final String REDIS_PREFIX = "SOLUTION_ID";
    private static final Long REDIS_TIME = 30L;

    @Override
    public @NotNull Problem newProblem() {
        return Problem.builder()
                .id(null)
                .dataId(null)
                .title(null)
                .statusType(ProblemStatusType.UNTREATED)
                .accessType(ProblemAccessType.PRIVATE)
                .submissionCount(0)
                .acceptCount(0)
                .owner(null)
                .build();
    }

    @Override
    public @NotNull Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessType, Long ownerId) {
        return problemMapper.countProblemListByTypeAndOwnerId(accessType, ownerId);
    }

    @Override
    public @NotNull List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessType, Long ownerId, Integer pageSize, Integer offset) {
        return problemMapper.selectProblemListByPageAndTypeAndOwnerId(accessType, ownerId, pageSize, offset);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByTypedAndKeyword(List<ProblemAccessType> accessTypeList, String keyword, Integer num) {
        return problemMapper.selectRecentProblemByTypeAndKeyword(accessTypeList, keyword, num);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByOwnerIdAndKeyword(Long ownerId, String keyword, Integer num) {
        return problemMapper.selectPrivateProblemByKeyword(ownerId, keyword, num);
    }

    @Override
    public @NotNull Optional<Problem> getProblemById(Long id) {
        if (ObjectUtils.isNull(id)) {
            return Optional.empty();
        }
        Problem problem = redisValueKit.get(REDIS_PREFIX, id, Problem.class)
                .orElse(problemMapper.selectProblemById(id));
        if (ObjectUtils.isNotNull(problem)) {
            redisValueKit.set(REDIS_PREFIX, id, problem, REDIS_TIME);
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
                .filter(ObjectUtils::isNotNull)
                .collect(Collectors.toList());
    }

    @Override
    public void insertProblem(Problem problem) {
        problemMapper.insertProblem(problem);
    }

    @Override
    public void updateProblemTitle(Long id, String newTitle) {
        problemMapper.updateProblemTitle(id, newTitle);
        redisValueKit.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setTitle(newTitle));
    }

    @Override
    public void updateProblemAccessStatus(Long id, ProblemAccessType newStatus) {
        problemMapper.updateProblemAccess(id, newStatus);
        redisValueKit.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setAccessType(newStatus));

    }

    @Override
    public void updateProblemStatus(Long id, ProblemStatusType statusType) {
        problemMapper.updateProblemStatus(id, statusType);
        redisValueKit.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setStatusType(statusType));
    }

    @Override
    public void updateProblemCount(Long id, Integer submitCount, Integer acceptCount) {
        problemMapper.updateProblemCount(id, submitCount, acceptCount);
        redisValueKit.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> {
            problem.setAcceptCount(problem.getSubmissionCount() + submitCount);
            problem.setAcceptCount(problem.getAcceptCount() + acceptCount);
        });
    }

    @Override
    public void updateProblemOwner(Long id, Long newOwner) {
        problemMapper.updateProblemOwner(id, newOwner);
        redisValueKit.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setOwner(newOwner));
    }

    @Override
    public void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus) {
        problemMapper.updateAllStatus(fromStatus, toStatus);
    }
}
