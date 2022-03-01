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
     * @param userId 用户的 ID，若为 null，则为请求所有的
     * @param problemId 题目的 ID，若为 null，则为请求所有的
     * @param statusType 状态，若为 null，则为请求所有的
     * @return 此提交类型的总数量
     */
    Integer countPublicSolution(@Param("userId") Long userId, @Param("problemId") Long problemId, @Param("statusType") SolutionStatusType statusType);

    /**
     * 根据提交至的比赛 id 统计数量
     * @param contestId 比赛 ID
     * @return 此比赛中的总提交数量
     */
    Integer countSolutionByContest(@Param("contestId") Long contestId);

    /**
     * 根据提交至的比赛 id 统计测试数量
     * @param contestId 比赛 ID
     * @return 此比赛中的总测试提交数量
     */
    Integer countSolutionByTestContest(@Param("contestId") Long contestId);

    /**
     * 根据提交类型，分页获取提交信息
     * @param pageSize 每页数量
     * @param offset 偏移量
     * @param userId 用户 ID
     * @param problemId 问题 ID
     * @param statusType 状态，若为 null，则为请求所有的
     * @return 提交列表
     */
    List<Solution> selectPublicSolutionByPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset, @Param("userId") Long userId, @Param("problemId") Long problemId, @Param("statusType") SolutionStatusType statusType);

    /**
     * 根据比赛获取提交列表
     * @param pageSize 单页数量
     * @param offset 偏移量
     * @param contestId 比赛的 id
     * @return 提交列表
     */
    List<Solution> selectSolutionByContestAndPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset, @Param("contestId") Long contestId);

    /**
     * 根据比赛获取测试提交列表
     * @param pageSize 单页数量
     * @param offset 偏移量
     * @param contestId 比赛的 id
     * @return 提交列表
     */
    List<Solution> selectSolutionByTestContestAndPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset, @Param("contestId") Long contestId);

    /**
     * 根据提交的 id 获取提交信息
     * @param id 提交 id
     * @return 提交信息
     */
    Solution selectSolutionById(@Param("id") Long id);

    /**
     * 获取用户在公开提交中的最后一次提交
     * @param userId 用户 id
     * @param problemId 题目 id
     * @return 用户的最后一次提交
     */
    Solution selectLastSolutionByUserIdAndProblemId(@Param("userId") Long userId, @Param("problemId") Long problemId);

    /**
     * 获取用户在比赛中的最后一次提交信息
     * @param userId 用户 id
     * @param problemId 题目 id
     * @param contestId 比赛 id
     * @return 用户在对应比赛中的最后一次提交信息
     */
    Solution selectLastSolutionByUserIdAndProblemIdAndContestId(@Param("userId") Long userId, @Param("problemId") Long problemId, @Param("contestId") Long contestId);

    void insertSolution(Solution solution);

    void updateStatus(@Param("id") Long id, @Param("statusType") SolutionStatusType statusType);

    void updateCostAndStatus(@Param("id") Long id, @Param("statusType") SolutionStatusType statusType, @Param("timeCost") Integer timeCost, @Param("memoryCost") Integer memoryCost);
}
