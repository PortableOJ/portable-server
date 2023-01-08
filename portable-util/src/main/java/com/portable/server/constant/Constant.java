package com.portable.server.constant;

/**
 * @author shiroha
 */
public class Constant {

    // region 通用固定值

    public static final String UTF_8 = "UTF-8";

    /**
     * 固定字体
     */
    public static final String ARIAL = "Arial";

    /**
     * 验证码的内容标识
     */
    public static final String CAPTCHA_CONTENT_TYPE = "image/png";

    /**
     * 单个字节流 BUFFER 长度
     */
    public static final Integer BUFFER_LEN = 4096;

    // endregion

    // region 常见字符

    public static final Byte RETURN_BYTE = '\n';
    public static final Byte SPACE_BYTE = ' ';

    // endregion

    /// region 时间单位运算

    public static final Long MILLISECOND = 1L;
    public static final Long SECOND = 1000L * MILLISECOND;
    public static final Long MINUTE = 60L * SECOND;
    public static final Long HOUR = 60L * MINUTE;
    public static final Long DAY = 24L * HOUR;
    public static final Long WEEK = 7L * DAY;

    /// endregion

}
