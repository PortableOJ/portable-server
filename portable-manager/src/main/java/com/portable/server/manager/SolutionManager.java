package com.portable.server.manager;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;

import java.util.List;

public interface SolutionManager {

    Solution newSolution();

    Integer countPublicSolution();

    Integer countSolutionByContest(Long contestId);

    List<Solution> selectPublicSolutionByPage(Integer pageSize, Integer offset);

    List<Solution> selectSolutionByContestAndPage(Integer pageSize, Integer offset, Long contestId);

    Solution selectSolutionById(Long id);

    Solution selectLastSolutionByUserIdAndProblemId(Long userId, Long problemId);

    void insertSolution(Solution solution);

    void updateStatus(Long id, SolutionStatusType statusType);

    void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost);
}
