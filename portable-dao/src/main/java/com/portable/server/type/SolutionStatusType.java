package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum SolutionStatusType implements ExceptionTextType {

    /**
     * 等待中
     */
    PENDING("Pending", false, false),

    /**
     * 编译中
     */
    COMPILING("Compiling", false, false),

    /**
     * 判题中
     */
    JUDGING("Judging", false, false),

    /**
     * 通过
     */
    ACCEPT("Accept", true, false),

    /**
     * 系统错误
     */
    SYSTEM_ERROR("System Error", true, false),

    /**
     * 运行错误
     */
    RUNTIME_ERROR("Runtime Error", true, true),

    /**
     * 超时
     */
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded", true, true),

    /**
     * 段错误
     */
    SEGMENT_FAIL("Segment Fail", true, true),

    /**
     * 运算符非法
     */
    ERRONEOUS_ARITHMETIC_OPERATION("Erroneous Arithmetic Operation", true, true),

    /**
     * 非法的系统调用
     */
    ILLEGAL_SYSTEM_CAL("Illegal System Call", true, true),

    /**
     * 返回值非零
     */
    RETURN_NOT_ZERO("Return Not Zero", true, true),

    /**
     * 编译错误
     */
    COMPILE_ERROR("Compile Error", true, false),

    /**
     * judge 程序编译错误
     */
    JUDGE_COMPILE_ERROR("Judge Compile Error", true, false),

    /**
     * judge 程序运行出错
     */
    JUDGE_FAIL("Judge Fail", true, false),

    /**
     * 答案错误
     */
    WRONG_ANSWER("Wrong Answer", true, true),
    ;

    private final String text;

    /**
     * 是否是一个结束的标准
     */
    private final Boolean endingResult;

    /**
     * 是否需要进行惩罚
     */
    private final Boolean penalty;

    SolutionStatusType(String text, Boolean endingResult, Boolean penalty) {
        this.text = text;
        this.endingResult = endingResult;
        this.penalty = penalty;
    }
}
