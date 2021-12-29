package com.portable.server.util;

/**
 * @author shiroha
 */
public class Constant {

    // region 通用固定值

    public static final String UTF_8 = "UTF-8";

    /**
     * 单个字节流 BUFFER 长度
     */
    public static final Integer BUFFER_LEN = 4096;

    /**
     * 默认的 judge 机器数量
     */
    public static final Integer DEFAULT_JUDGE_NUM = 1;

    /**
     * 有界的任务队列最大容量
     */
    public static final Integer QUEUE_WAIT_NUM = 1000;

    // endregion

    // region 常见字符

    public static final Byte RETURN_BYTE = '\n';
    public static final Byte SPACE_BYTE = ' ';

    // endregion

}
