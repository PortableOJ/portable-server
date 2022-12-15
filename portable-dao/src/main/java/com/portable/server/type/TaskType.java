package com.portable.server.type;

import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum TaskType {

    /**
     * 比赛中的提交任务
     */
    CONTEST(-20),

    /**
     * 题库中的提交任务
     */
    PROBLEM(0),

    /**
     * 测试数据生成的任务
     */
    TEST(20),

    /**
     * 校验题目的任务
     */
    CHECK_PROBLEM(15),

    /**
     * 测试比赛的提交
     */
    TEST_CONTEST(10),
    ;

    private final Integer weightGrade;

    TaskType(Integer weightGrade) {
        this.weightGrade = weightGrade;
    }

    public static TaskType toJudgeWorkType(SolutionType solutionType) {
        switch (solutionType) {
            case CONTEST:
                return CONTEST;
            case PROBLEM_PROCESS:
                return CHECK_PROBLEM;
            case TEST_CONTEST:
                return TEST_CONTEST;
            case PUBLIC:
            default:
                return PROBLEM;
        }
    }
}
