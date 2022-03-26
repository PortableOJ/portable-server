package com.portable.server.manager.impl;

import com.portable.server.manager.ProblemManager;
import com.portable.server.mapper.ProblemMapper;
import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class ProblemManagerImpl implements ProblemManager {

    @Resource
    private ProblemMapper problemMapper;

    @Override
    public Problem newProblem() {
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
    public Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessType, Long ownerId) {
        return problemMapper.countProblemListByTypeAndOwnerId(accessType, ownerId);
    }

    @Override
    public List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessType, Long ownerId, Integer pageSize, Integer offset) {
        return problemMapper.selectProblemListByPageAndTypeAndOwnerId(accessType, ownerId, pageSize, offset);
    }

    @Override
    public List<Problem> searchRecentProblemByTypedAndKeyword(List<ProblemAccessType> accessTypeList, String keyword, Integer num) {
        return problemMapper.selectRecentProblemByTypeAndKeyword(accessTypeList, keyword, num);
    }

    @Override
    public List<Problem> searchRecentProblemByOwnerIdAndKeyword(Long ownerId, String keyword, Integer num) {
        return problemMapper.selectPrivateProblemByKeyword(ownerId, keyword, num);
    }

    @Override
    public Optional<Problem> getProblemById(Long id) {
        return Optional.ofNullable(problemMapper.selectProblemById(id));
    }

    @Override
    public List<Long> checkProblemListExist(List<Long> problemList) {
        return problemList.stream()
                .map(aLong -> {
                    Optional<Problem> problem = getProblemById(aLong);
                    return !problem.isPresent() ? aLong : null;
                })
                .filter(aLong -> !Objects.isNull(aLong))
                .collect(Collectors.toList());
    }

    @Override
    public void insertProblem(Problem problem) {
        problemMapper.insertProblem(problem);
    }

    @Override
    public void updateProblemTitle(Long id, String newTitle) {
        problemMapper.updateProblemTitle(id, newTitle);
    }

    @Override
    public void updateProblemAccessStatus(Long id, ProblemAccessType newStatus) {
        problemMapper.updateProblemAccess(id, newStatus);
    }

    @Override
    public void updateProblemStatus(Long id, ProblemStatusType statusType) {
        problemMapper.updateProblemStatus(id, statusType);
    }

    @Override
    public void updateProblemCount(Long id, Integer submitCount, Integer acceptCount) {
        problemMapper.updateProblemCount(id, submitCount, acceptCount);
    }

    @Override
    public void updateProblemOwner(Long id, Long newOwner) {
        problemMapper.updateProblemOwner(id, newOwner);
    }

    @Override
    public void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus) {
        problemMapper.updateAllStatus(fromStatus, toStatus);
    }
}
