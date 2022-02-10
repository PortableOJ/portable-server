package com.portable.server.mapper;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author shiroha
 */
@Repository
public interface SolutionMapper {

    /**
     * 根据提交类型统计数量
     * @param solutionType 提交的类型
     * @return 此提交类型的总数量
     */
    Integer countSolutionByType(@Param("solutionType") SolutionType solutionType);

    Integer countSolutionByContest(@Param("contestId") Long contestId);

    List<Solution> selectSolutionByTypeAndPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset, @Param("solutionType") SolutionType solutionType);

    List<Solution> selectSolutionByContestAndPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset, @Param("contestId") Long contestId);

    Solution selectSolutionById(@Param("id") Long id);

    Solution selectLastSolutionByUserIdAndProblemId(@Param("userId") Long userId, @Param("problemId") Long problemId);

    void insertSolution(Solution solution);

    void updateStatus(@Param("id") Long id, @Param("statusType") SolutionStatusType statusType);

    void updateCostAndStatus(@Param("id") Long id, @Param("statusType") SolutionStatusType statusType, @Param("timeCost") Integer timeCost, @Param("memoryCost") Integer memoryCost);
}
