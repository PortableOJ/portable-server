package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * 题目的访问权限
 *
 * @author shiroha
 */
@Getter
public enum ProblemAccessType implements ExceptionTextType {

    /**
     * 公开的
     * <p>
     * 可以在题库中看到
     * <br>
     * 接受来自任何比赛的提交
     * <br>
     * 任何比赛可以随意链接此题
     * <br>
     * 所有拥有浏览权限的的均可以随意查看
     * </p>
     */
    PUBLIC("公开的"),

    /**
     * 被隐藏
     * <p>
     * 不能在题库中看到
     * <br>
     * 接受来自任何比赛的提交
     * <br>
     * 任何比赛可以随意链接此题
     * <br>
     * 所有拥有浏览权限的的均可以随意查看
     * </p>
     */
    HIDDEN("隐藏的"),

    /**
     * 私有的题目
     * <p>
     * 不能在题库中看到
     * <br>
     * 仅接受来自指定比赛的提交
     * <br>
     * 仅第一次链接此题的比赛可以链接此题
     * <br>
     * 非题目拥有者且非第一次链接比赛的主办者无法查看修改此题
     * </p>
     */
    PRIVATE("私有的"),
    ;

    private final String text;

    ProblemAccessType(String text) {
        this.text = text;
    }
}
