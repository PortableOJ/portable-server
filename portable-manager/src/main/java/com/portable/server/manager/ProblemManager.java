package com.portable.server.manager;

import com.portable.server.model.problem.Problem;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;

import java.util.List;
import java.util.Optional;

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
     * 获取匹配标题的一定数量的最新题目
     * @param accessTypeList 匹配的访问权限列表
     * @param keyword 关键字
     * @param num 总需要数量
     * @return 问题列表
     */
    List<Problem> searchRecentProblemByTypedAndKeyword(List<ProblemAccessType> accessTypeList, String keyword, Integer num);

    /**
     * 获取匹配标题的一定数量的最新私人题目
     * @param ownerId 用户 id
     * @param keyword 关键字
     * @param num 总需要数量
     * @return 问题列表
     */
    List<Problem> searchRecentProblemByOwnerIdAndKeyword(Long ownerId, String keyword, Integer num);

    /**
     * 获取对应 ID 的题目内容
     *
     * @param id 题目的 ID
     * @return 题目内容
     */
    Optional<Problem> getProblemById(Long id);

    /**
     * 校验题目列表是否存在
     * @param problemList 题目列表
     * @return 不存在的题目列表
     */
    List<Long> checkProblemListExist(List<Long> problemList);

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

    /**
     * 更新题目的提交情况
     * @param id 题目 ID
     * @param submitCount 新的提交量
     * @param acceptCount 新的通过量
     */
    void updateProblemCount(Long id, Integer submitCount, Integer acceptCount);

    /**
     * 更新题目所有者
     * @param id 题目 ID
     * @param newOwner 新的所有者
     */
    void updateProblemOwner(Long id, Long newOwner);

    /**
     * 更新所有的状态
     *
     * @param fromStatus 需要更新的状态
     * @param toStatus   更新至的状态
     */
    void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus);
}
