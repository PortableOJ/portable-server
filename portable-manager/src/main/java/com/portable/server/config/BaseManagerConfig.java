package com.portable.server.config;

import com.portable.server.manager.JudgeManager;
import com.portable.server.manager.impl.JudgeManagerImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * @author shiroha
 */
public abstract class BaseManagerConfig {

    @Lazy
    @Bean("judgeContainerManager")
    public JudgeManager judgeManager() {
        return new JudgeManagerImpl();
    }
}
