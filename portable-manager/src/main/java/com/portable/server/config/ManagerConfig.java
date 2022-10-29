package com.portable.server.config;

import com.portable.server.manager.UserManager;
import com.portable.server.manager.impl.dev.UserMapManagerImpl;
import com.portable.server.manager.impl.prod.UserManagerImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

/**
 * @author shiroha
 */
@Configuration
public class ManagerConfig {

    @Lazy
    @Profile("prod")
    @Bean(name = "userManager")
    public UserManager prodUserManager() {
        return new UserManagerImpl();
    }

    @Lazy
    @Profile("dev")
    @Bean(name = "userManager")
    public UserManager devUserManager() {
        return new UserMapManagerImpl();
    }
}
