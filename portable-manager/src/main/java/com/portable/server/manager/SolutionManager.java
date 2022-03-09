package com.portable.server.manager;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;

import java.util.List;

/**
 * @author shiroha
 */
public interface SolutionManager {

    /**
     * 创建一个新的提交
     *
     * @return 一个新的提交
     */
    Solution newSolution();

    /**
     * 统计提交数量
     *
     * @param solutionType 提交类型
     * @param userId       用户的 id
     * @param contestId    比赛 id
     * @param problemId    题目 id
     * @param statusType   状态过滤
     * @return 提交数量
     */
    Integer countSolution(SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType);

    /**
     * 分页获取提交信息
     *
     * @param pageSize     单页大小
     * @param offset       偏移量
     * @param solutionType 提交类型
     * @param userId       用户 id
     * @param contestId    比赛 id
     * @param problemId    问题 id
     * @param statusType   状态
     * @return 提交列表
     */
    List<Solution> selectSolutionByPage(Integer pageSize, Integer offset, SolutionType solutionType, Long userId, Long contestId, Long problemId, SolutionStatusType statusType);

    /**
     * 分页获取提交信息
     *
     * @param pageSize     单页大小
     * @return 提交列表
     */
    List<Solution> selectSolutionLastNotEndSolution(Integer pageSize);

    /**
     * 根据 ID 获取 solution
     *
     * @param id solution 的 ID
     * @return 对应的 solution
     */
    Solution selectSolutionById(Long id);

    /**
     * 获取目标用户的目标题目的最后一次提交的结果
     *
     * @param userId    用户 ID
     * @param problemId 题目的 ID
     * @return 提交的问题的
     */
    Solution selectLastSolutionByUserIdAndProblemId(Long userId, Long problemId);

    /**
     * 获取目标用户的目标题目以及目标比赛的最后一次提交的结果
     *
     * @param userId    用户 ID
     * @param problemId 题目的 ID
     * @param contestId 比赛的 ID
     * @return 提交的问题的
     */
    Solution selectLastSolutionByUserIdAndProblemIdAndContestId(Long userId, Long problemId, Long contestId);

    /**
     * 新增一个 solution
     *
     * @param solution 需要新增的 solution
     */
    void insertSolution(Solution solution);

    /**
     * 更新 solution 的状态
     *
     * @param id         solution 的 ID
     * @param statusType 新的 solution 的状态
     */
    void updateStatus(Long id, SolutionStatusType statusType);

    /**
     * 更新提交的时间和内存消耗以及状态，内存和时间的消耗取较大值
     *
     * @param id         solution 的 ID
     * @param statusType 新的 solution 的状态
     * @param timeCost   耗时
     * @param memoryCost 内存消耗
     */
    void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost);
}
