package com.portable.server.manager.impl.dev;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ProblemManager;
import com.portable.server.model.BaseEntity;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.persistent.StructuredHelper;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class ProblemDevManagerImpl implements ProblemManager {

    @Resource
    private StructuredHelper<Problem, Long> problemDevMapper;

    @Resource
    private StructuredHelper<ProblemData, String> problemDataDevMapper;

    @Override
    public @NotNull Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessTypeList, Long ownerId) {
        return problemDevMapper.countList(problem -> accessTypeList.contains(problem.getAccessType()) || Objects.equals(problem.getOwner(), ownerId));
    }

    @Override
    public @NotNull List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessTypeList, Long ownerId, Integer pageSize, Integer offset) {
        return problemDevMapper.searchListByPage(problem -> accessTypeList.contains(problem.getAccessType()) || Objects.equals(problem.getOwner(), ownerId), pageSize, offset);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByTypedAndKeyword(List<ProblemAccessType> accessTypeList, String keyword, Integer num) {
        return problemDevMapper.searchListByPage(problem -> accessTypeList.contains(problem.getAccessType()) && problem.getTitle().contains(keyword), num, 0);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByOwnerIdAndKeyword(Long ownerId, String keyword, Integer num) {
        return problemDevMapper.searchListByPage(problem -> Objects.equals(problem.getOwner(), ownerId) && problem.getTitle().contains(keyword), num, 0);
    }

    @Override
    public @NotNull Optional<Problem> getProblemById(Long id) {
        return problemDevMapper.getDataById(id);
    }

    @Override
    public @NotNull List<Long> checkProblemListExist(List<Long> problemList) {
        Set<Long> existProblemIdSet = problemDevMapper.searchList(problem -> problemList.contains(problem.getId()))
                .stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());

        return problemList.stream()
                .filter(id -> !existProblemIdSet.contains(id))
                .collect(Collectors.toList());
    }

    @Override
    public void insertProblem(Problem problem) {
        problemDevMapper.insert(problem, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateProblemTitle(Long id, String newTitle) {
        problemDevMapper.updateByFunction(id, problem -> problem.setTitle(newTitle));
    }

    @Override
    public void updateProblemAccessStatus(Long id, ProblemAccessType newStatus) {
        problemDevMapper.updateByFunction(id, problem -> problem.setAccessType(newStatus));
    }

    @Override
    public void updateProblemStatus(Long id, ProblemStatusType statusType) {
        problemDevMapper.updateByFunction(id, problem -> problem.setStatusType(statusType));
    }

    @Override
    public void updateProblemCount(Long id, Integer submitCount, Integer acceptCount) {
        problemDevMapper.updateByFunction(id, problem -> {
            problem.setSubmissionCount(problem.getSubmissionCount() + submitCount);
            problem.setAcceptCount(problem.getAcceptCount() + acceptCount);
        });
    }

    @Override
    public void updateProblemOwner(Long id, Long newOwner) {
        problemDevMapper.updateByFunction(id, problem -> problem.setOwner(newOwner));
    }

    @Override
    public void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus) {
        problemDevMapper.updateByFunction(problem -> fromStatus.equals(problem.getStatusType()), problem -> problem.setStatusType(toStatus));
    }

    @Override
    public @NotNull ProblemData getProblemData(String dataId) {
        return problemDataDevMapper.getDataById(dataId).orElseThrow(PortableException.from("S-03-001"));
    }

    @Override
    public void insertProblemData(ProblemData problemData) {
        problemDataDevMapper.insert(problemData, BasicTranslateUtils::reString);
    }

    @Override
    public void updateProblemData(ProblemData problemData) {
        problemDataDevMapper.updateById(problemData);
    }
}
