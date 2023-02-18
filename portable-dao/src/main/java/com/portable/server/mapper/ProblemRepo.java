package com.portable.server.mapper;

import java.util.List;

import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;

import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * @author shiroha
 */
@Repository
public interface ProblemRepo {

    /**
     * 根据问题的访问权限和拥有者 ID 获取总题目数量
     *
     * @param accessTypeList 匹配的访问权限列表
     * @param userId         当前的用户 ID
     * @return 匹配的总量
     */
    @NotNull Integer countProblemListByTypeAndOwnerId(@NotNull @Param("accessTypeList") List<ProblemAccessType> accessTypeList,
                                                      @Nullable @Param("userId") Long userId);

    /**
     * 根据问题的访问权限和拥有者 ID，分页获取题目列表
     *
     * @param accessTypeList 匹配的访问权限列表
     * @param userId         当前的用户 ID
     * @param pageSize       单页数量
     * @param offset         偏移量
     * @return 问题列表
     */
    @NotNull List<Problem> selectProblemListByPageAndTypeAndOwnerId(@NotNull @Param("accessTypeList") List<ProblemAccessType> accessTypeList,
                                                                    @Nullable @Param("userId") Long userId,
                                                                    @NotNull @Param("pageSize") Integer pageSize,
                                                                    @NotNull @Param("offset") Integer offset);

    /**
     * 获取匹配标题的一定数量的公开最新题目
     *
     * @param accessTypeList 匹配的访问权限列表
     * @param keyword        关键字
     * @param num            总需要数量
     * @return 问题列表
     */
    @NotNull List<Problem> selectRecentProblemByTypeAndKeyword(@NotNull @Param("accessTypeList") List<ProblemAccessType> accessTypeList,
                                                               @NotNull @Param("keyword") String keyword,
                                                               @NotNull @Param("num") Integer num);

    /**
     * 获取匹配标题的一定数量的私人题库
     *
     * @param userId  用户的 id
     * @param keyword 关键字
     * @param num     总需要数量
     * @return 问题列表
     */
    @NotNull List<Problem> selectPrivateProblemByKeyword(@NotNull @Param("userId") Long userId,
                                                         @NotNull @Param("keyword") String keyword,
                                                         @NotNull @Param("num") Integer num);

    /**
     * 根据问题 ID 获取问题
     *
     * @param id 问题 ID
     * @return 问题内容
     */
    @Nullable Problem selectProblemById(@NotNull @Param("id") Long id);

    /**
     * 新增题目
     *
     * @param problem 题目信息，完成后题目将会被赋予 ID
     */
    void insertProblem(@NotNull Problem problem);

    /**
     * 更新题目的标题
     *
     * @param id    题目的 ID
     * @param title 题目的新标题
     */
    void updateProblemTitle(@NotNull @Param("id") Long id, @NotNull @Param("title") String title);

    /**
     * 更新题目的 Access 状态
     *
     * @param id     题目的 ID
     * @param status 题目的新 Access 状态
     */
    void updateProblemAccess(@NotNull @Param("id") Long id, @NotNull @Param("status") ProblemAccessType status);

    /**
     * 更新题目的状态
     *
     * @param id         题目的 ID
     * @param statusType 新的
     */
    void updateProblemStatus(@NotNull @Param("id") Long id, @NotNull @Param("status") ProblemStatusType statusType);

    /**
     * 更新题目的通过/提交数
     *
     * @param id          题目的 ID
     * @param submitCount 题目通过数量的变更值
     * @param acceptCount 题目通过数量的变更值
     */
    void updateProblemCount(@NotNull @Param("id") Long id, @NotNull @Param("submitCount") Integer submitCount, @NotNull @Param("acceptCount") Integer acceptCount);

    /**
     * 转交题目
     *
     * @param id       题目的 ID
     * @param newOwner 被转交对象
     */
    void updateProblemOwner(@NotNull @Param("id") Long id, @NotNull @Param("newOwner") Long newOwner);

    /**
     * 更新所有的状态
     *
     * @param fromStatus 需要更新的状态
     * @param toStatus   更新至的状态
     */
    void updateAllStatus(@NotNull @Param("fromStatus") ProblemStatusType fromStatus, @NotNull @Param("toStatus") ProblemStatusType toStatus);
}
