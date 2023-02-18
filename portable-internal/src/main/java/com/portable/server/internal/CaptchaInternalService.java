package com.portable.server.internal;

import java.io.OutputStream;

/**
 * @author shiroha
 */
public interface CaptchaInternalService {

    /**
     * 获取验证码
     *
     * @param outputStream 写入文件流
     * @return 验证值
     */
    String getCaptcha(OutputStream outputStream);
}
