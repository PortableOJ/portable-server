package com.portable.server.mapper;

import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author shiroha
 */
@Repository
public interface ProblemMapper {

    /**
     * 根据问题的访问权限和拥有者 ID 获取总题目数量
     *
     * @param accessTypeList 匹配的访问权限列表
     * @param userId         当前的用户 ID
     * @return 匹配的总量
     */
    Integer countProblemListByTypeAndOwnerId(@Param("accessTypeList") List<ProblemAccessType> accessTypeList, @Param("userId") Long userId);

    /**
     * 根据问题的访问权限和拥有者 ID，分页获取题目列表
     *
     * @param accessTypeList 匹配的访问权限列表
     * @param userId         当前的用户 ID
     * @param pageSize       单页数量
     * @param offset         偏移量
     * @return 问题列表
     */
    List<Problem> selectProblemListByPageAndTypeAndOwnerId(@Param("accessTypeList") List<ProblemAccessType> accessTypeList, @Param("userId") Long userId,
                                                           @Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

    /**
     * 根据问题 ID 获取问题
     *
     * @param id 问题 ID
     * @return 问题内容
     */
    Problem selectProblemById(@Param("id") Long id);

    /**
     * 新增题目
     *
     * @param problem 题目信息，完成后题目将会被赋予 ID
     */
    void insertProblem(Problem problem);

    /**
     * 更新题目的标题
     * @param id 题目的 ID
     * @param title 题目的新标题
     */
    void updateProblemTitle(@Param("id") Long id, @Param("title") String title);

    /**
     * 更新题目的 Access 状态
     * @param id 题目的 ID
     * @param status 题目的新 Access 状态
     */
    void updateProblemAccess(@Param("id") Long id, @Param("status") ProblemAccessType status);

    /**
     * 更新题目的状态
     * @param id 题目的 ID
     * @param statusType 新的
     */
    void updateProblemStatus(@Param("id") Long id, @Param("status") ProblemStatusType statusType);

    /**
     * 更新题目的通过/提交数
     * @param id 题目的 ID
     * @param submitCount 题目通过数量的变更值
     * @param acceptCount 题目通过数量的变更值
     */
    void updateProblemCount(@Param("id") Long id, @Param("submitCount") Integer submitCount, @Param("acceptCount") Integer acceptCount);

    /**
     * 转交题目
     * @param id 题目的 ID
     * @param newOwner 被转交对象
     */
    void updateProblemOwner(@Param("id") Long id, @Param("newOwner") Long newOwner);
}
