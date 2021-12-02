package com.portable.server.manager;

import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;

import java.util.List;

/**
 * @author shiroha
 */
public interface ProblemManager {

    /**
     * 创建一个新的题目
     *
     * @return 新题目
     */
    Problem newProblem();

    /**
     * 根据题目的访问类型和当前用户的 ID 获取题目总数
     *
     * @param accessType 访问权限
     * @param ownerId    所拥有的用户 ID
     * @return 总匹配的题目数量
     */
    Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessType, Long ownerId);

    /**
     * 分页获取匹配的题目访问类型和当前用户的 ID 的题目
     *
     * @param accessType 访问类型
     * @param ownerId    所拥有的用户 ID
     * @param pageSize   单页数量
     * @param offset     偏移量
     * @return 题目的列表
     */
    List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessType, Long ownerId, Integer pageSize, Integer offset);

    /**
     * 获取对应 ID 的题目内容
     *
     * @param id 题目的 ID
     * @return 题目内容
     */
    Problem getProblemById(Long id);

    /**
     * 插入题目，强调是新增
     *
     * @param problem 需要新增的题目，其中的 id 字段会被新的值覆盖
     */
    void insertProblem(Problem problem);

    /**
     * 更新题目的标题
     * @param id 题目的 ID
     * @param newTitle 题目的新标题
     */
    void updateProblemTitle(Long id, String newTitle);

    /**
     * 更新题目的访问状态
     * @param id 题目的 ID
     * @param newStatus 新的访问状态
     */
    void updateProblemAccessStatus(Long id, ProblemAccessType newStatus);

    /**
     * 更新题目的状态
     * @param id 题目的 ID
     * @param statusType 新的状态
     */
    void updateProblemStatus(Long id, ProblemStatusType statusType);

    void updateProblemCount(Long id, Integer submitCount, Integer acceptCount);

    void updateProblemOwner(Long id, Long newOwner);
}
