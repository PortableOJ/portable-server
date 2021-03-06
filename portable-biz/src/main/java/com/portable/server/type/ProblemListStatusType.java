package com.portable.server.type;

import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum ProblemListStatusType {

    /**
     * 未提交过
     */
    NEVER_SUBMIT("未提交过"),

    /**
     * 正在判题中
     */
    ON_JUDGE("正在判题中"),

    /**
     * 没有通过
     */
    NOT_PASS("没有通过"),

    /**
     * 通过
     */
    PASS("通过"),
    ;

    private final String text;

    ProblemListStatusType(String text) {
        this.text = text;
    }

    public static ProblemListStatusType of(SolutionStatusType solutionStatusType) {
        switch (solutionStatusType) {
            case PENDING:
            case COMPILING:
            case JUDGING:
                return ON_JUDGE;
            case ACCEPT:
                return PASS;
            case SYSTEM_ERROR:
            case RUNTIME_ERROR:
            case TIME_LIMIT_EXCEEDED:
            case SEGMENT_FAIL:
            case ERRONEOUS_ARITHMETIC_OPERATION:
            case ILLEGAL_SYSTEM_CAL:
            case RETURN_NOT_ZERO:
            case COMPILE_ERROR:
            case JUDGE_COMPILE_ERROR:
            case JUDGE_FAIL:
            case WRONG_ANSWER:
                return NOT_PASS;
            default:
                return NEVER_SUBMIT;
        }
    }
}
