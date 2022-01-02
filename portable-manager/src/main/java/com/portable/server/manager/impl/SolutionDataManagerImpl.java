package com.portable.server.manager.impl;

import com.portable.server.manager.SolutionDataManager;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.repo.SolutionDataRepo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author shiroha
 */
@Component
public class SolutionDataManagerImpl implements SolutionDataManager {

    @Resource
    private SolutionDataRepo solutionDataRepo;

    @Override
    public SolutionData newSolutionData(ProblemData problemData) {
        return SolutionData.builder()
                ._id(null)
                .code(null)
                .compileMsg(null)
                .runningMsg(new ArrayList<>())
                .runOnVersion(problemData.getVersion())
                .build();
    }

    @Override
    public SolutionData getSolutionData(String dataId) {
        return solutionDataRepo.getSolutionData(dataId);
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
