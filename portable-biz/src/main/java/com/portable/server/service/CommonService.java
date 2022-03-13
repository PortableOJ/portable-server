package com.portable.server.service;

import com.alibaba.fastjson.JSONObject;
import com.portable.server.exception.PortableException;

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
     * @throws PortableException 找不到类则抛出
     */
    Map<String, JSONObject> getEnumDesc(String name) throws PortableException;

    /**
     * 获取验证码
     * @param outputStream 验证码写入
     * @return 验证码的值
     * @throws PortableException 获取失败则抛出
     */
    String getCaptcha(OutputStream outputStream) throws PortableException;
}
