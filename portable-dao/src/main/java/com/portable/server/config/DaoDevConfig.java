package com.portable.server.config;

import com.portable.server.model.batch.Batch;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.User;
import com.portable.server.persistent.PartitionHelper;
import com.portable.server.persistent.StructuredHelper;
import com.portable.server.persistent.impl.FilePartitionHelperImpl;
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
    public StructuredHelper<Long, User> devUserMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "uerDataDevMapper")
    public StructuredHelper<String, BaseUserData> devUserDataMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "problemDevMapper")
    public StructuredHelper<Long, Problem> devProblemMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "problemDataDevMapper")
    public StructuredHelper<String, ProblemData> devProblemDataMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "solutionDevMapper")
    public StructuredHelper<Long, Solution> devSolutionMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "solutionDataDevMapper")
    public StructuredHelper<String, SolutionData> devSolutionDataMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "contestDevMapper")
    public StructuredHelper<Long, Contest> devContestMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "contestDataDevMapper")
    public StructuredHelper<String, BaseContestData> devContestDataMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "batchDevMapper")
    public StructuredHelper<Long, Batch> devBatchMapper() {
        return new MemStructuredHelperImpl<>();
    }

    @Lazy
    @Bean(name = "imageMapper")
    public PartitionHelper devImageMapper() {
        return new FilePartitionHelperImpl();
    }
}
