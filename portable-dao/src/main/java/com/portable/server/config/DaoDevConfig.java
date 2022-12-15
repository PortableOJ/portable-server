package com.portable.server.config;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.User;
import com.portable.server.persistent.StructuredHelper;
import com.portable.server.persistent.impl.MemStructuredHelperImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

/**
 * @author shiroha
 */
@Configuration
@Profile("dev")
public class DaoDevConfig {

    @Lazy
    @Bean(name = "uerDevMapper")
    public StructuredHelper<User, Long> devUserMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "uerDataDevMapper")
    public StructuredHelper<BaseUserData, String> devUserDataMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "problemDevMapper")
    public StructuredHelper<Problem, Long> devProblemMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "problemDataDevMapper")
    public StructuredHelper<ProblemData, String> devProblemDataMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "solutionDevMapper")
    public StructuredHelper<Solution, Long> devSolutionMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "solutionDataDevMapper")
    public StructuredHelper<SolutionData, String> devSolutionDataMapper() {
        return new MemStructuredHelperImpl<>();
    }

}
