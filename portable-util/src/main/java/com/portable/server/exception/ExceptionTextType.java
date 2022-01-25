package com.portable.server.exception;

/**
 * 文案接口，表示此类型有文案版本。在抛出错误时，会自动替换为对应的文案，其他无用
 * @author shiroha
 */
public interface ExceptionTextType {

    /**
     * 获取文字内容
     * @return 文字内容
     */
    String getText();
}
