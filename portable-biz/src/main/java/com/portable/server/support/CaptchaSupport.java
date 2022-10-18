package com.portable.server.support;

import java.io.OutputStream;

import com.portable.server.exception.PortableException;

/**
 * @author shiroha
 */
public interface CaptchaSupport {

    /**
     * 写入验证码
     * @param outputStream 写入位置
     * @return 验证值
     * @throws PortableException 获取失败则抛出
     */
    String getCaptcha(OutputStream outputStream);
}
