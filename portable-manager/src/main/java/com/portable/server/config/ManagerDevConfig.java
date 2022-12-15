package com.portable.server.config;

import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserManager;
import com.portable.server.manager.impl.dev.ProblemDevManagerImpl;
import com.portable.server.manager.impl.dev.SolutionDevManagerImpl;
import com.portable.server.manager.impl.dev.UserDevManagerImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

/**
 * @author shiroha
 */
@Configuration
@Profile("dev")
public class ManagerDevConfig extends BaseManagerConfig {

    @Lazy
    @Bean(name = "userManager")
    public UserManager devUserManager() {
        return new UserDevManagerImpl();
    }

    @Lazy
    @Bean(name = "problemManager")
    public ProblemManager devProblemManager() {
        return new ProblemDevManagerImpl();
    }

    @Lazy
    @Bean(name = "solutionManager")
    public SolutionManager devSolutionManager() {
        return new SolutionDevManagerImpl();
    }

}
