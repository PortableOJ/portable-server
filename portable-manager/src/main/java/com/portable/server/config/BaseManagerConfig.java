package com.portable.server.config;

import com.portable.server.manager.JudgeManager;
import com.portable.server.manager.TaskManager;
import com.portable.server.manager.impl.JudgeManagerImpl;
import com.portable.server.manager.impl.TaskManagerImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * @author shiroha
 */
public abstract class BaseManagerConfig {

    @Lazy
    @Bean("judgeManager")
    public JudgeManager judgeManager() {
        return new JudgeManagerImpl();
    }

    @Lazy
    @Bean("taskManager")
    public TaskManager taskManager() {
        return new TaskManagerImpl();
    }
}
