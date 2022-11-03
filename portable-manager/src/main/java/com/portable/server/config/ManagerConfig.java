package com.portable.server.config;

import com.portable.server.helper.MemProtractedHelper;
import com.portable.server.helper.impl.MemProtractedHelperImpl;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.UserManager;
import com.portable.server.manager.impl.dev.ProblemDevManagerImpl;
import com.portable.server.manager.impl.dev.UserDevManagerImpl;
import com.portable.server.manager.impl.prod.ProblemManagerImpl;
import com.portable.server.manager.impl.prod.UserManagerImpl;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.User;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

/**
 * @author shiroha
 */
@Configuration
public class ManagerConfig {

    /// region mapper 层构建 dev

    @Lazy
    @Profile("dev")
    @Bean(name = "uerDevMapper")
    public MemProtractedHelper<User, Long> devUserMapper() {
        return new MemProtractedHelperImpl<>();
    }

    @Lazy
    @Profile("dev")
    @Bean(name = "uerDataDevMapper")
    public MemProtractedHelper<BaseUserData, String> devUserDataMapper() {
        return new MemProtractedHelperImpl<>();
    }

    @Lazy
    @Profile("dev")
    @Bean(name = "problemDevMapper")
    public MemProtractedHelper<Problem, Long> devProblemMapper() {
        return new MemProtractedHelperImpl<>();
    }

    @Lazy
    @Profile("dev")
    @Bean(name = "problemDataDevMapper")
    public MemProtractedHelper<ProblemData, String> devProblemDataMapper() {
        return new MemProtractedHelperImpl<>();
    }

    @Lazy
    @Profile("dev")
    @Bean(name = "solutionDevMapper")
    public MemProtractedHelper<Solution, Long> devSolutionMapper() {
        return new MemProtractedHelperImpl<>();
    }

    @Lazy
    @Profile("dev")
    @Bean(name = "solutionDataDevMapper")
    public MemProtractedHelper<SolutionData, String> devSolutionDataMapper() {
        return new MemProtractedHelperImpl<>();
    }

    /// endregion

    /// region manager 层构建

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
        return new UserDevManagerImpl();
    }

    @Lazy
    @Profile("prod")
    @Bean(name = "problemManager")
    public ProblemManager prodProblemManager() {
        return new ProblemManagerImpl();
    }

    @Lazy
    @Profile("dev")
    @Bean(name = "problemManager")
    public ProblemManager devProblemManager() {
        return new ProblemDevManagerImpl();
    }

    /// endregion
}
