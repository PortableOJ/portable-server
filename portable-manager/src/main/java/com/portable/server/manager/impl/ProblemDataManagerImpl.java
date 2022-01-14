package com.portable.server.manager.impl;

import com.portable.server.manager.ProblemDataManager;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.repo.ProblemDataRepo;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.ProblemType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ProblemDataManagerImpl implements ProblemDataManager {

    @Resource
    private ProblemDataRepo problemDataRepo;

    @Override
    public ProblemData newProblemData() {
        return ProblemData.builder()
                ._id(null)
                .defaultTimeLimit(null)
                .defaultMemoryLimit(null)
                .specialTimeLimit(new HashMap<>())
                .specialMemoryLimit(new HashMap<>())
                .supportLanguage(new ArrayList<>())
                .description(null)
                .input(null)
                .output(null)
                .example(new ArrayList<>())
                .type(ProblemType.STANDARD)
                .judgeCodeType(JudgeCodeType.IGNORE_END_OF_FILE)
                .testName(new ArrayList<>())
                .shareTest(false)
                .testCodeList(new ArrayList<>())
                .build();
    }

    @Override
    public ProblemData getProblemData(String dataId) {
        return problemDataRepo.getProblemData(dataId);
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
