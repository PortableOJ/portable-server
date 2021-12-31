package com.portable.server.manager;

import com.portable.server.model.ServiceVerifyCode;

/**
 * @author shiroha
 */
public interface TemporaryDataManager {

    /**
     * 获取当前 service 的验证码
     * @return service 的验证码，若验证码最近
     */
    ServiceVerifyCode getServiceCode();
}
