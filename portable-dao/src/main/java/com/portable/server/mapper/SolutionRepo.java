package com.portable.server.mapper;

import java.util.List;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;

import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * @author shiroha
 */
@Repository
public interface SolutionRepo {

    /**
     * 根据提交类型统计数量
     *
     * @param solutionType 需要获取的提交类型
     * @param userId       用户的 ID，若为 null，则为请求所有的
     * @param contestId    比赛的 id
     * @param problemId    题目的 ID，若为 null，则为请求所有的
     * @param statusType   状态，若为 null，则为请求所有的
     * @return 此提交类型的总数量
     */
    @NotNull Integer countSolution(@Nullable @Param("solutionType") SolutionType solutionType,
                                   @Nullable @Param("userId") Long userId,
                                   @Nullable @Param("contestId") Long contestId,
                                   @Nullable @Param("problemId") Long problemId,
                                   @Nullable @Param("statusType") SolutionStatusType statusType);

    /**
     * 根据提交类型，分页获取提交信息
     *
     * @param pageSize     每页数量
     * @param offset       偏移量
     * @param solutionType 需要获取的提交类型
     * @param userId       用户 ID，若为 null，则为请求所有的
     * @param contestId    比赛的 id，若为 null，则为请求所有的
     * @param problemId    问题 ID，若为 null，则为请求所有的
     * @param statusType   状态，若为 null，则为请求所有的
     * @param beforeId     前序情况，表示只需要此值之前发生的新的提交，若为 null，则忽略
     * @param afterId      后续情况，表示只需要此值之后发生的新的提交，若为 null，则忽略
     * @return 提交列表
     */
    @NotNull List<Solution> selectSolutionByPage(@NotNull @Param("pageSize") Integer pageSize,
                                                 @NotNull @Param("offset") Integer offset,
                                                 @Nullable @Param("solutionType") SolutionType solutionType,
                                                 @Nullable @Param("userId") Long userId,
                                                 @Nullable @Param("contestId") Long contestId,
                                                 @Nullable @Param("problemId") Long problemId,
                                                 @Nullable @Param("statusType") List<SolutionStatusType> statusType,
                                                 @Nullable @Param("beforeId") Long beforeId,
                                                 @Nullable @Param("afterId") Long afterId);

    /**
     * 过滤不是此用户的提交
     *
     * @param num    数量
     * @param userId 用户 ID，表示不是此用户的提交
     * @return 提交列表
     */
    @NotNull List<Solution> selectNotUserSolution(@NotNull @Param("num") Integer num,
                                                  @NotNull @Param("userId") Long userId);

    /**
     * 根据提交的 id 获取提交信息
     *
     * @param id 提交 id
     * @return 提交信息
     */
    @Nullable Solution selectSolutionById(@NotNull @Param("id") Long id);

    /**
     * 获取用户在公开提交中的最后一次提交
     *
     * @param userId    用户 id
     * @param problemId 题目 id
     * @return 用户的最后一次提交
     */
    @Nullable Solution selectLastSolutionByUserIdAndProblemId(@NotNull @Param("userId") Long userId, @NotNull @Param("problemId") Long problemId);

    /**
     * 获取用户在比赛中的最后一次提交信息
     *
     * @param userId    用户 id
     * @param problemId 题目 id
     * @param contestId 比赛 id
     * @return 用户在对应比赛中的最后一次提交信息
     */
    @Nullable Solution selectLastSolutionByUserIdAndProblemIdAndContestId(@NotNull @Param("userId") Long userId, @NotNull @Param("problemId") Long problemId, @NotNull @Param("contestId") Long contestId);

    /**
     * 新增一个提交
     *
     * @param solution 提交值
     */
    void insertSolution(@NotNull Solution solution);

    /**
     * 更新提交的状态
     *
     * @param id         提交的 id
     * @param statusType 提交的新状态
     */
    void updateStatus(@NotNull @Param("id") Long id, @NotNull @Param("statusType") SolutionStatusType statusType);

    /**
     * 更新提交的状态
     *
     * @param id         提交的 id
     * @param statusType 提交的新状态
     * @param timeCost   提交的新耗时（自动取历史最大）
     * @param memoryCost 提交的新内存消耗（自动取历史最大）
     */
    void updateCostAndStatus(@NotNull @Param("id") Long id, @NotNull @Param("statusType") SolutionStatusType statusType, @NotNull @Param("timeCost") Integer timeCost, @NotNull @Param("memoryCost") Integer memoryCost);

    /**
     * 将所有给定状态全部转为指定状态
     *
     * @param fromStatus 给定状态
     * @param toStatus   指定状态
     */
    void updateAllStatus(@NotNull @Param("fromStatus") List<SolutionStatusType> fromStatus, @NotNull @Param("toStatus") SolutionStatusType toStatus);
}
