package com.portable.server.manager.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.repo.SolutionDataRepo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author shiroha
 */
@Component
public class SolutionDataManagerImpl implements SolutionDataManager {

    @Resource
    private SolutionDataRepo solutionDataRepo;

    @Override
    public @NotNull SolutionData newSolutionData(ProblemData problemData) {
        return SolutionData.builder()
                ._id(null)
                .code(null)
                .compileMsg(null)
                .runningMsg(new HashMap<>(problemData.getTestName().size()))
                .runOnVersion(problemData.getVersion())
                .build();
    }

    @Override
    public @NotNull SolutionData getSolutionData(String dataId) throws PortableException {
        return Optional.ofNullable(solutionDataRepo.getSolutionData(dataId)).orElseThrow(PortableException.from("S-05-001"));
    }

    @Override
    public void insertSolutionData(SolutionData solutionData) {
        solutionDataRepo.insertSolutionData(solutionData);
    }

    @Override
    public void saveSolutionData(SolutionData solutionData) {
        solutionDataRepo.saveSolutionData(solutionData);
    }
}
