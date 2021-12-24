package com.portable.server.manager;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;

import java.util.List;

/**
 * @author shiroha
 */
public interface SolutionManager {

    /**
     * 创建一个新的提交
     * @return 一个新的提交
     */
    Solution newSolution();

    /**
     * 统计所有公开的提交数量
     * @return 公开提交的总量
     */
    Integer countPublicSolution();

    /**
     * 统计所有此比赛的提交数量
     * @param contestId 比赛 ID
     * @return 此比赛的提交数量
     */
    Integer countSolutionByContest(Long contestId);

    /**
     * 分页获取提交的列表
     * @param pageSize 每页内容数量
     * @param offset 偏移量
     * @return 提交列表
     */
    List<Solution> selectPublicSolutionByPage(Integer pageSize, Integer offset);

    List<Solution> selectSolutionByContestAndPage(Integer pageSize, Integer offset, Long contestId);

    Solution selectSolutionById(Long id);

    /**
     * 获取目标用户的目标题目的最后一次提交的结果
     * @param userId 用户 ID
     * @param problemId 题目的 ID
     * @return 提交的问题的
     */
    Solution selectLastSolutionByUserIdAndProblemId(Long userId, Long problemId);

    void insertSolution(Solution solution);

    void updateStatus(Long id, SolutionStatusType statusType);

    void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost);
}
