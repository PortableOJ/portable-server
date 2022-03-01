package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum SolutionType implements ExceptionTextType {

    /**
     * 公开题库提交
     */
    PUBLIC("公开提交"),

    /**
     * 在比赛中提交
     */
    CONTEST("在比赛中提交"),

    /**
     * 处理题目时产生的提交
     */
    PROBLEM_PROCESS("处理题目时的提交"),

    /**
     * 测试比赛的提交
     */
    TEST_CONTEST("测试比赛的提交"),
    ;

    private final String text;

    SolutionType(String text) {
        this.text = text;
    }
}
