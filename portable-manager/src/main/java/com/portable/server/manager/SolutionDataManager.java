package com.portable.server.manager;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.solution.SolutionData;

/**
 * @author shiroha
 */
public interface SolutionDataManager {

    /**
     * 创建一个新的提交内容
     * @param problemData 目标题目的信息
     * @return 一个新的空白的提交内容
     */
    SolutionData newSolutionData(ProblemData problemData);

    /**
     * 根据 ID 获取一个提交内容
     * @param dataId 提交的数据 ID
     * @return 提交的内容
     */
    SolutionData getSolutionData(String dataId);

    /**
     * 新增一个提交内容
     * @param solutionData 提交的内容
     */
    void insertSolutionData(SolutionData solutionData);

    /**
     * 更新一个提交内容
     * @param solutionData 提交的内容
     */
    void saveSolutionData(SolutionData solutionData);
}
