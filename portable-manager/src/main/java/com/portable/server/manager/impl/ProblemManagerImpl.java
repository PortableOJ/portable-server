package com.portable.server.manager.impl;

import com.portable.server.manager.ProblemManager;
import com.portable.server.mapper.ProblemMapper;
import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
    public Problem getProblemById(Long id) {
        return problemMapper.selectProblemById(id);
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
}
