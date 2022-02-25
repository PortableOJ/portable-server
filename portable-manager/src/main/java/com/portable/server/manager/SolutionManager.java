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
     * @param userId 只查询某个用户，若为 null 则查询所有
     * @param problemId 只查询某个题目的提交，若为 null 则查询所有
     * @param statusType 只查询某个状态的提交，若为 null 则查询所有
     * @return 公开提交的总量
     */
    Integer countPublicSolution(Long userId, Long problemId, SolutionStatusType statusType);

    /**
     * 统计所有此比赛的提交数量
     * @param contestId 比赛 ID
     * @return 此比赛的提交数量
     */
    Integer countSolutionByContest(Long contestId);

    /**
     * 统计所有此比赛的测试提交数量
     * @param contestId 比赛 ID
     * @return 此比赛的测试提交数量
     */
    Integer countSolutionByTestContest(Long contestId);

    /**
     * 分页获取提交的列表
     * @param pageSize 每页内容数量
     * @param offset 偏移量
     * @param userId 只查询某个用户，若为 null 则查询所有
     * @param problemId 只查询某个题目的提交，若为 null 则查询所有
     * @param statusType 只查询某个状态，若为 null 则查询所有
     * @return 提交列表
     */
    List<Solution> selectPublicSolutionByPage(Integer pageSize, Integer offset, Long userId, Long problemId, SolutionStatusType statusType);

    /**
     * 分页获取比赛的提交列表
     * @param pageSize 单页数量
     * @param offset 偏移量
     * @param contestId 比赛 id
     * @return 提交列表
     */
    List<Solution> selectSolutionByContestAndPage(Integer pageSize, Integer offset, Long contestId);

    /**
     * 分页获取比赛的测试提交列表
     * @param pageSize 单页数量
     * @param offset 偏移量
     * @param contestId 比赛 id
     * @return 提交列表
     */
    List<Solution> selectSolutionByTestContestAndPage(Integer pageSize, Integer offset, Long contestId);

    /**
     * 根据 ID 获取 solution
     * @param id solution 的 ID
     * @return 对应的 solution
     */
    Solution selectSolutionById(Long id);

    /**
     * 获取目标用户的目标题目的最后一次提交的结果
     * @param userId 用户 ID
     * @param problemId 题目的 ID
     * @return 提交的问题的
     */
    Solution selectLastSolutionByUserIdAndProblemId(Long userId, Long problemId);

    /**
     * 获取目标用户的目标题目以及目标比赛的最后一次提交的结果
     * @param userId 用户 ID
     * @param problemId 题目的 ID
     * @param contestId 比赛的 ID
     * @return 提交的问题的
     */
    Solution selectLastSolutionByUserIdAndProblemIdAndContestId(Long userId, Long problemId, Long contestId);

    /**
     * 新增一个 solution
     * @param solution 需要新增的 solution
     */
    void insertSolution(Solution solution);

    /**
     * 更新 solution 的状态
     * @param id solution 的 ID
     * @param statusType 新的 solution 的状态
     */
    void updateStatus(Long id, SolutionStatusType statusType);

    /**
     * 更新提交的时间和内存消耗以及状态，内存和时间的消耗取较大值
     * @param id solution 的 ID
     * @param statusType 新的 solution 的状态
     * @param timeCost 耗时
     * @param memoryCost 内存消耗
     */
    void updateCostAndStatus(Long id, SolutionStatusType statusType, Integer timeCost, Integer memoryCost);
}
