package com.portable.server.config;

import com.portable.server.manager.BatchManager;
import com.portable.server.manager.ContestManager;
import com.portable.server.manager.ImageManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserManager;
import com.portable.server.manager.impl.dev.BatchDevManagerImpl;
import com.portable.server.manager.impl.dev.ContestDevManagerImpl;
import com.portable.server.manager.impl.dev.ProblemDevManagerImpl;
import com.portable.server.manager.impl.dev.SolutionDevManagerImpl;
import com.portable.server.manager.impl.dev.UserDevManagerImpl;
import com.portable.server.manager.impl.prod.ImageManagerImpl;

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
    public UserManager userManager() {
        return new UserDevManagerImpl();
    }

    @Lazy
    @Bean(name = "problemManager")
    public ProblemManager problemManager() {
        return new ProblemDevManagerImpl();
    }

    @Lazy
    @Bean(name = "solutionManager")
    public SolutionManager solutionManager() {
        return new SolutionDevManagerImpl();
    }

    @Lazy
    @Bean(name = "contestManager")
    public ContestManager contestManager() {
        return new ContestDevManagerImpl();
    }

    @Lazy
    @Bean(name = "batchManager")
    public BatchManager batchManager() {
        return new BatchDevManagerImpl();
    }

    @Lazy
    @Bean(name = "imageManager")
    public ImageManager imageManager() {
        return new ImageManagerImpl();
    }
}