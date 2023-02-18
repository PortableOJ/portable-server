package com.portable.server.manager.impl;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.cache.CacheKvHelper;
import com.portable.server.exception.PortableErrors;
import com.portable.server.manager.ProblemManager;
import com.portable.server.mapper.ProblemRepo;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.repo.ProblemDataRepo;
import com.portable.server.struct.PartitionHelper;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class ProblemManagerImpl implements ProblemManager {

    @Resource
    private ProblemRepo problemRepo;

    @Resource
    private CacheKvHelper<Long> cacheKvHelper;

    @Resource
    private ProblemDataRepo problemDataRepo;

    @Resource(name = "problemPartitionHelper")
    private PartitionHelper problemPartitionHelper;

    @Override
    public @NotNull Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessTypeList, Long ownerId) {
        return problemRepo.countProblemListByTypeAndOwnerId(accessTypeList, ownerId);
    }

    @Override
    public @NotNull List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessTypeList, Long ownerId, Integer pageSize, Integer offset) {
        return problemRepo.selectProblemListByPageAndTypeAndOwnerId(accessTypeList, ownerId, pageSize, offset);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByTypedAndKeyword(List<ProblemAccessType> accessTypeList, String keyword, Integer num) {
        return problemRepo.selectRecentProblemByTypeAndKeyword(accessTypeList, keyword, num);
    }

    @Override
    public @NotNull List<Problem> searchRecentProblemByOwnerIdAndKeyword(Long ownerId, String keyword, Integer num) {
        return problemRepo.selectPrivateProblemByKeyword(ownerId, keyword, num);
    }

    @Override
    public @NotNull Optional<Problem> getProblemById(Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        Problem problem = cacheKvHelper.get(id, Problem.class)
                .orElseGet(() -> problemRepo.selectProblemById(id));
        if (Objects.nonNull(problem)) {
            cacheKvHelper.set(id, problem);
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
        problemRepo.insertProblem(problem);
    }

    @Override
    public void updateProblemTitle(Long id, String newTitle) {
        problemRepo.updateProblemTitle(id, newTitle);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemAccessStatus(Long id, ProblemAccessType newStatus) {
        problemRepo.updateProblemAccess(id, newStatus);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemStatus(Long id, ProblemStatusType statusType) {
        problemRepo.updateProblemStatus(id, statusType);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemCount(Long id, Integer submitCount, Integer acceptCount) {
        problemRepo.updateProblemCount(id, submitCount, acceptCount);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateProblemOwner(Long id, Long newOwner) {
        problemRepo.updateProblemOwner(id, newOwner);
        cacheKvHelper.delete(id);
    }

    @Override
    public void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus) {
        problemRepo.updateAllStatus(fromStatus, toStatus);
    }

    @Override
    public @NotNull ProblemData getProblemData(String dataId) {
        return Optional.ofNullable(problemDataRepo.getProblemData(dataId))
                .orElseThrow(PortableErrors.from("S-03-001"));
    }

    @Override
    public void insertProblemData(ProblemData problemData) {
        problemDataRepo.insertProblemData(problemData);
    }

    @Override
    public void updateProblemData(ProblemData problemData) {
        problemDataRepo.saveProblemData(problemData);
    }

    @Override
    public void createProblem(@NotNull Long problemId) {
        problemPartitionHelper.createDirIfNotExist(String.valueOf(problemId));
    }

    @Override
    public InputStream getTestInput(Long problemId, String testName) {
        return problemPartitionHelper.getFileInput(getProblemInput(problemId, testName));
    }

    @Override
    public InputStream getTestOutput(Long problemId, String testName) {
        return problemPartitionHelper.getFileInput(getProblemOutput(problemId, testName));
    }

    @Override
    public void saveTestInput(Long problemId, String testName, InputStream inputStream) {
        problemPartitionHelper.saveFileOrOverwrite(getProblemInput(problemId, testName), inputStream);
    }

    @Override
    public void saveTestOutput(Long problemId, String testName, InputStream inputStream) {
        problemPartitionHelper.saveFileOrOverwrite(getProblemOutput(problemId, testName), inputStream);
    }

    @Override
    public void createTestOutput(Long problemId, String testName, byte[] value) {
        problemPartitionHelper.saveFileOrOverwrite(getProblemOutput(problemId, testName), value);
    }

    @Override
    public void appendTestOutput(Long problemId, String testName, byte[] value) {
        problemPartitionHelper.appendFile(getProblemOutput(problemId, testName), value);
    }

    @Override
    public void removeTest(Long problemId, String testName) {
        problemPartitionHelper.deleteFileIfExist(getProblemInput(problemId, testName));
        problemPartitionHelper.deleteFileIfExist(getProblemOutput(problemId, testName));
    }

    private String getProblemInput(Long problemId, String testName) {
        return String.format("%d%s%s.in", problemId, File.separator, testName);
    }

    private String getProblemOutput(Long problemId, String testName) {
        return String.format("%d%s%s.out", problemId, File.separator, testName);
    }
}
