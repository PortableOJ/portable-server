package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

@Getter
public enum SolutionType implements ExceptionTextType {

    PUBLIC("公开提交"),

    CONTEST("在比赛中提交"),

    PROBLEM_PROCESS("处理题目时的提交"),
    ;

    private final String text;

    SolutionType(String text) {
        this.text = text;
    }
}
