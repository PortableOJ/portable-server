package com.portable.server.service.impl;

import com.portable.server.manager.impl.BatchManagerImpl;
import com.portable.server.manager.impl.ContestDataManagerImpl;
import com.portable.server.manager.impl.ContestManagerImpl;
import com.portable.server.manager.impl.ProblemDataManagerImpl;
import com.portable.server.manager.impl.ProblemManagerImpl;
import com.portable.server.manager.impl.SolutionDataManagerImpl;
import com.portable.server.manager.impl.SolutionManagerImpl;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.model.user.User;
import com.portable.server.support.impl.ContestSupportImpl;
import com.portable.server.support.impl.JudgeSupportImpl;
import com.portable.server.util.test.UserContextBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContestServiceImplTest {

    @InjectMocks
    private ContestServiceImpl contestService;

    @Mock
    private ContestManagerImpl contestManager;

    @Mock
    private ContestDataManagerImpl contestDataManager;

    @Mock
    private UserManagerImpl userManager;

    @Mock
    private ProblemManagerImpl problemManager;

    @Mock
    private ProblemDataManagerImpl problemDataManager;

    @Mock
    private SolutionManagerImpl solutionManager;

    @Mock
    private SolutionDataManagerImpl solutionDataManager;

    @Mock
    private BatchManagerImpl batchManager;

    @Mock
    private JudgeSupportImpl judgeSupport;

    @Mock
    private ContestSupportImpl contestSupport;

    private User user;
    private Contest contest;
    private PublicContestData publicContestData;
    private PasswordContestData passwordContestData;
    private PrivateContestData privateContestData;
    private BatchContestData batchContestData;

    private static final Long MOCKED_USER_ID = 1L;
    private static final Long MOCKED_CONTEST_ID = 2L;
    private static final Long MOCKED_SOLUTION_ID = 3L;

    private UserContextBuilder userContextBuilder;

    @BeforeEach
    void setUp() {
        userContextBuilder = new UserContextBuilder();
        userContextBuilder.setup();
    }

    @AfterEach
    void tearDown() {
        userContextBuilder.tearDown();
    }

    @Test
    void getContestList() {
    }

    @Test
    void authorizeContest() {
    }

    @Test
    void getContestInfo() {
    }

    @Test
    void getContestData() {
    }

    @Test
    void getContestAdminData() {
    }

    @Test
    void getContestProblem() {
    }

    @Test
    void getContestStatusList() {
    }

    @Test
    void getContestSolution() {
    }

    @Test
    void getContestTestStatusList() {
    }

    @Test
    void getContestTestSolution() {
    }

    @Test
    void getContestRank() {
    }

    @Test
    void submit() {
    }

    @Test
    void createContest() {
    }

    @Test
    void updateContest() {
    }

    @Test
    void addContestProblem() {
    }
}