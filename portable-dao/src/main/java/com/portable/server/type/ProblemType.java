package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * 题目类别
 *
 * @author shiroha
 */
@Getter
public enum ProblemType implements ExceptionTextType {

    /**
     * 标准题目
     */
    STANDARD("标准");
//    INTERACTIVE("交互题");

    private final String text;

    ProblemType(String text) {
        this.text = text;
    }
}
