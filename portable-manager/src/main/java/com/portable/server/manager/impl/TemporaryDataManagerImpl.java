package com.portable.server.manager.impl;

import com.portable.server.manager.TemporaryDataManager;
import com.portable.server.model.ServiceVerifyCode;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class TemporaryDataManagerImpl implements TemporaryDataManager {

    @Override
    public ServiceVerifyCode getServiceCode() {
        return null;
    }
}
