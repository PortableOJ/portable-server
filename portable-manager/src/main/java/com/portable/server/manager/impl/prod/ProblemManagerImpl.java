package com.portable.server.manager.impl.prod;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.helper.RedisValueHelper;
import com.portable.server.manager.ProblemManager;
import com.portable.server.mapper.ProblemMapper;
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
    private ProblemMapper problemMapper;

    @Resource
    private RedisValueHelper redisValueHelper;

    @Resource
    private ProblemDataRepo problemDataRepo;

    /**
     * redis 的 key 和过期时间
     */
    private static final String REDIS_PREFIX = "PROBLEM_ID";
    private static final Long REDIS_TIME = 30L;

    @Override
    public @NotNull Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessTypeList, Long ownerId) {
        return problemMapper.countProblemListByTypeAndOwnerId(accessTypeList, ownerId);
    }

    @Override
    public @NotNull List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessTypeList, Long ownerId, Integer pageSize, Integer offset) {
        return problemMapper.selectProblemListByPageAndTypeAndOwnerId(accessTypeList, ownerId, pageSize, offset);
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
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        Problem problem = redisValueHelper.get(REDIS_PREFIX, id, Problem.class)
                .orElseGet(() -> problemMapper.selectProblemById(id));
        if (Objects.nonNull(problem)) {
            redisValueHelper.set(REDIS_PREFIX, id, problem, REDIS_TIME);
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
        problemMapper.insertProblem(problem);
    }

    @Override
    public void updateProblemTitle(Long id, String newTitle) {
        problemMapper.updateProblemTitle(id, newTitle);
        redisValueHelper.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setTitle(newTitle));
    }

    @Override
    public void updateProblemAccessStatus(Long id, ProblemAccessType newStatus) {
        problemMapper.updateProblemAccess(id, newStatus);
        redisValueHelper.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setAccessType(newStatus));

    }

    @Override
    public void updateProblemStatus(Long id, ProblemStatusType statusType) {
        problemMapper.updateProblemStatus(id, statusType);
        redisValueHelper.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setStatusType(statusType));
    }

    @Override
    public void updateProblemCount(Long id, Integer submitCount, Integer acceptCount) {
        problemMapper.updateProblemCount(id, submitCount, acceptCount);
        redisValueHelper.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> {
            problem.setAcceptCount(problem.getSubmissionCount() + submitCount);
            problem.setAcceptCount(problem.getAcceptCount() + acceptCount);
        });
    }

    @Override
    public void updateProblemOwner(Long id, Long newOwner) {
        problemMapper.updateProblemOwner(id, newOwner);
        redisValueHelper.getPeek(REDIS_PREFIX, id, Problem.class, REDIS_TIME, problem -> problem.setOwner(newOwner));
    }

    @Override
    public void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus) {
        problemMapper.updateAllStatus(fromStatus, toStatus);
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
