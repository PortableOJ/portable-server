package com.portable.server.manager;

import com.portable.server.model.problem.ProblemData;

/**
 * @author shiroha
 */
public interface ProblemDataManager {

    ProblemData newProblemData();

    ProblemData getProblemData(String dataId);

    void insertProblemData(ProblemData problemData);

    void updateProblemData(ProblemData problemData);
}
