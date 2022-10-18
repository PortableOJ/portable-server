package com.portable.server.manager;

import com.portable.server.exception.PortableException;
import com.portable.server.model.problem.ProblemData;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public interface ProblemDataManager {

    /**
     * 新建一个问题数据
     *
     * @return 新问题数据
     */
    @NotNull
    ProblemData newProblemData();

    /**
     * 根据 dataID 获取题目数据
     *
     * @param dataId 题目数据 ID
     * @return 题目数据
     * @throws PortableException ID 不存在则抛出
     */
    @NotNull
    ProblemData getProblemData(String dataId);

    /**
     * 新增题目数据
     *
     * @param problemData 题目数据信息
     */
    void insertProblemData(ProblemData problemData);

    /**
     * 更新题目数据
     *
     * @param problemData 题目数据
     */
    void updateProblemData(ProblemData problemData);
}
