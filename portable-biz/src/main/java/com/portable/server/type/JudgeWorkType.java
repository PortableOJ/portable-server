package com.portable.server.type;

import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum JudgeWorkType {

    /**
     * 比赛中的提交任务
     */
    CONTEST(-1),

    /**
     * 题库中的提交任务
     */
    PROBLEM(0),

    /**
     * 测试数据生成的任务
     */
    TEST(2),

    /**
     * 校验题目的任务
     */
    CHECK_PROBLEM(2),

    /**
     * 测试比赛的提交
     */
    TEST_CONTEST(1),
    ;

    private final Integer weight;

    JudgeWorkType(Integer weight) {
        this.weight = weight;
    }

    public static JudgeWorkType toJudgeWorkType(SolutionType solutionType) {
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
