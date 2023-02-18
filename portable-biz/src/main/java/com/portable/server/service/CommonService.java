package com.portable.server.service;

import java.io.OutputStream;
import java.util.Map;

/**
 * @author shiroha
 */
public interface CommonService {

    /**
     * 获取本次服务启动后的一个固定值
     *
     * @return 本次服务的值
     */
    String getVersionName();

    /**
     * 获取枚举类型的详细信息
     *
     * @param name 枚举名称
     * @return 所有详细的信息
     */
    Map<String, Map<String, Object>> getEnumDesc(String name);

    /**
     * 获取验证码
     * @param outputStream 验证码写入
     * @return 验证码的值
     */
    String getCaptcha(OutputStream outputStream);
}
