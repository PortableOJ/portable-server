package com.portable.server.config;

import com.portable.server.manager.BatchManager;
import com.portable.server.manager.ContestManager;
import com.portable.server.manager.ImageManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserManager;
import com.portable.server.manager.impl.prod.BatchManagerImpl;
import com.portable.server.manager.impl.prod.ContestManagerImpl;
import com.portable.server.manager.impl.prod.ImageManagerImpl;
import com.portable.server.manager.impl.prod.ProblemManagerImpl;
import com.portable.server.manager.impl.prod.SolutionManagerImpl;
import com.portable.server.manager.impl.prod.UserManagerImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

/**
 * @author shiroha
 */
@Configuration
@Profile("prod")
public class ManagerProdConfig extends BaseManagerConfig {

    @Lazy
    @Bean(name = "userManager")
    public UserManager userManager() {
        return new UserManagerImpl();
    }

    @Lazy
    @Bean(name = "problemManager")
    public ProblemManager problemManager() {
        return new ProblemManagerImpl();
    }

    @Lazy
    @Bean(name = "solutionManager")
    public SolutionManager solutionManager() {
        return new SolutionManagerImpl();
    }

    @Lazy
    @Bean(name = "contestManager")
    public ContestManager contestManager() {
        return new ContestManagerImpl();
    }

    @Lazy
    @Bean(name = "batchManager")
    public BatchManager batchManager() {
        return new BatchManagerImpl();
    }

    @Lazy
    @Bean(name = "imageManager")
    public ImageManager imageManager() {
        return new ImageManagerImpl();
    }
}
