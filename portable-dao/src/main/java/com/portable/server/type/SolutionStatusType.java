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
    PENDING("Pending", false),

    /**
     * 编译中
     */
    COMPILING("Compiling", false),

    /**
     * 判题中
     */
    JUDGING("Judging", false),

    /**
     * 通过
     */
    ACCEPT("Accept", true),

    /**
     * 系统错误
     */
    SYSTEM_ERROR("System Error", true),

    /**
     * 运行错误
     */
    RUNTIME_ERROR("Runtime Error", true),

    /**
     * 超时
     */
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded", true),

    /**
     * 段错误
     */
    SEGMENT_FAIL("Segment Fail", true),

    /**
     * 运算符非法
     */
    ERRONEOUS_ARITHMETIC_OPERATION("Erroneous Arithmetic Operation", true),

    /**
     * 非法的系统调用
     */
    ILLEGAL_SYSTEM_CAL("Illegal System Call", true),

    /**
     * 返回值非零
     */
    RETURN_NOT_ZERO("Return Not Zero", true),

    /**
     * 编译错误
     */
    COMPILE_ERROR("Compile Error", true),

    /**
     * judge 程序编译错误
     */
    JUDGE_COMPILE_ERROR("Judge Compile Error", true),

    /**
     * judge 程序运行出错
     */
    JUDGE_FAIL("Judge Fail", true),

    /**
     * 答案错误
     */
    WRONG_ANSWER("Wrong Answer", true),
    ;

    private final String text;

    /**
     * 是否是一个结束的标准
     */
    private final Boolean endingResult;

    SolutionStatusType(String text, Boolean endingResult) {
        this.text = text;
        this.endingResult = endingResult;
    }
}
