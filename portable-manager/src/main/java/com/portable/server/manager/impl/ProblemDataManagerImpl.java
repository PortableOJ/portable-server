package com.portable.server.manager.impl;

import com.portable.server.manager.ProblemDataManager;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.repo.ProblemDataRepo;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.ProblemType;
import com.portable.server.type.SolutionStatusType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author shiroha
 */
@Component
public class ProblemDataManagerImpl implements ProblemDataManager {

    @Resource
    private ProblemDataRepo problemDataRepo;

    @Override
    public ProblemData newProblemData() {
        return ProblemData.builder()
                ._id(null)
                .contestId(null)
                .defaultTimeLimit(1000)
                .defaultMemoryLimit(128)
                .specialTimeLimit(new HashMap<>(0))
                .specialMemoryLimit(new HashMap<>(0))
                .supportLanguage(new ArrayList<>())
                .description(null)
                .input(null)
                .output(null)
                .example(new ArrayList<>())
                .type(ProblemType.STANDARD)
                .judgeCodeType(JudgeCodeType.IGNORE_END_OF_FILE)
                .judgeCode(null)
                .testName(new ArrayList<>())
                .shareTest(false)
                .stdCode(ProblemData.StdCode.builder()
                        .name("STD")
                        .code(null)
                        .expectResultType(SolutionStatusType.ACCEPT)
                        .languageType(null)
                        .solutionId(null)
                        .build())
                .testCodeList(new ArrayList<>())
                .version(0)
                .gmtModifyTime(new Date())
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
