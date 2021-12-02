package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * 使用的 Judge 模式
 *
 * @author shiroha
 */
@Getter
public enum JudgeCodeType implements ExceptionTextType {

    /**
     * 自定义
     */
    DIY("自定义", ""),
    ALL_SAME("完全一致", ""),
    IGNORE_END_OF_LINE_AND_FILE("无视行尾的空格和文件尾的换行", ""),
    SAME_AFTER_REMOVE_SEPARATOR("删除分隔符后完全相同", ""),
    SAME_YES_NO("仅 yes 和 no 的，逻辑相同", ""),
    DOUBLE_4("按顺序比较浮点数，允许 1e-4 的精度误差", ""),
    DOUBLE_6("按顺序比较浮点数，允许 1e-6 的精度误差", "");

    private final String text;
    private final String path;

    JudgeCodeType(String text, String path) {
        this.text = text;
        this.path = path;
    }
}
