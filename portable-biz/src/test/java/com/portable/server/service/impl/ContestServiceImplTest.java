package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.BatchManagerImpl;
import com.portable.server.manager.impl.ContestDataManagerImpl;
import com.portable.server.manager.impl.ContestManagerImpl;
import com.portable.server.manager.impl.ProblemDataManagerImpl;
import com.portable.server.manager.impl.ProblemManagerImpl;
import com.portable.server.manager.impl.SolutionDataManagerImpl;
import com.portable.server.manager.impl.SolutionManagerImpl;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAuth;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestInfoResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.user.User;
import com.portable.server.support.impl.ContestSupportImpl;
import com.portable.server.support.impl.JudgeSupportImpl;
import com.portable.server.type.ContestAccessType;
import com.portable.server.type.ContestVisitType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.util.test.TestMockedValueMaker;
import com.portable.server.util.test.UserContextBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    private Problem problem;
    private Solution solution;
    private List<Contest> contestList;
    private PublicContestData publicContestData;
    private PasswordContestData passwordContestData;
    private PrivateContestData privateContestData;
    private BatchContestData batchContestData;

    private static final Long MOCKED_USER_ID = TestMockedValueMaker.mLong();
    private static final Long MOCKED_CONTEST_ID = TestMockedValueMaker.mLong();
    private static final Long MOCKED_SOLUTION_ID = TestMockedValueMaker.mLong();
    private static final Long MOCKED_PROBLEM_ID = TestMockedValueMaker.mLong();

    private static final String MOCKED_CONTEST_MONGO_ID = TestMockedValueMaker.mString();
    private static final String MOCKED_CONTEST_TITLE = TestMockedValueMaker.mString();
    private static final String MOCKED_USER_HANDLE = TestMockedValueMaker.mString();
    private static final String MOCKED_PROBLEM_TITLE = TestMockedValueMaker.mString();

    private UserContextBuilder userContextBuilder;

    private MockedStatic<ContestVisitType> contestVisitTypeMockedStatic;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(MOCKED_USER_ID)
                .build();
        contest = Contest.builder()
                .id(MOCKED_CONTEST_ID)
                .build();
        problem = Problem.builder()
                .id(MOCKED_PROBLEM_ID)
                .build();
        solution = Solution.builder()
                .id(MOCKED_SOLUTION_ID)
                .build();
        publicContestData = PublicContestData.builder().build();
        passwordContestData = PasswordContestData.builder().build();
        privateContestData = PrivateContestData.builder().build();
        batchContestData = BatchContestData.builder().build();

        contestList = new ArrayList<>();
        userContextBuilder = new UserContextBuilder();
        userContextBuilder.setup();

        contestVisitTypeMockedStatic = Mockito.mockStatic(ContestVisitType.class);
    }

    @AfterEach
    void tearDown() {
        userContextBuilder.tearDown();
        contestVisitTypeMockedStatic.close();
    }

    @Test
    void testGetContestList() {
        contestList.add(Contest.builder()
                .id(MOCKED_CONTEST_ID)
                .build());
        contestList.add(Contest.builder()
                .id(MOCKED_CONTEST_ID + 1)
                .build());
        contestList.add(Contest.builder()
                .id(MOCKED_CONTEST_ID + 2)
                .build());

        Mockito.when(contestManager.getAllContestNumber()).thenReturn(100);
        Mockito.when(contestManager.getContestByPage(10, 0)).thenReturn(contestList);

        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(1)
                .pageSize(10)
                .build();

        PageResponse<ContestListResponse, Void> retVal = contestService.getContestList(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(3, retVal.getData().size());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getData().get(0).getId());
        Assertions.assertEquals(MOCKED_CONTEST_ID + 1, retVal.getData().get(1).getId());
        Assertions.assertEquals(MOCKED_CONTEST_ID + 2, retVal.getData().get(2).getId());
        Assertions.assertEquals(100, retVal.getTotalNum());

        /// endregion
    }

    @Test
    void testAuthorizeContestWithNoContest() {
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.empty());

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(null)
                .build();

        try {
            contestService.authorizeContest(contestAuth);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-002", e.getCode());
        }
    }

    @Test
    void testAuthorizeContestWithNoContestData() throws PortableException {
        contest.setAccessType(ContestAccessType.PRIVATE);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPrivateContestDataById(MOCKED_CONTEST_MONGO_ID)).thenThrow(PortableException.of("A-08-001", contest.getAccessType()));

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(null)
                .build();

        try {
            contestService.authorizeContest(contestAuth);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-001", e.getCode());
        }
    }

    @Test
    void testAuthorizeContestWithNoPasswordVisit() throws PortableException {
        contest.setAccessType(ContestAccessType.BATCH);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getBatchContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(batchContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(null)
                .build();

        ContestVisitType retVal = contestService.authorizeContest(contestAuth);

        Assertions.assertEquals(ContestVisitType.VISIT, retVal);
    }

    @Test
    void testAuthorizeContestWithPasswordPublicVisit() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(TestMockedValueMaker.mString())
                .build();

        ContestVisitType retVal = contestService.authorizeContest(contestAuth);

        Assertions.assertEquals(ContestVisitType.VISIT, retVal);
    }

    @Test
    void testAuthorizeContestWithNoPasswordAdmin() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(null)
                .build();

        ContestVisitType retVal = contestService.authorizeContest(contestAuth);

        Assertions.assertEquals(ContestVisitType.ADMIN, retVal);
    }

    @Test
    void testAuthorizeContestWithPasswordFailAdmin() throws PortableException {
        contest.setAccessType(ContestAccessType.PASSWORD);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        passwordContestData.setPassword(TestMockedValueMaker.mString());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPasswordContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(passwordContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(TestMockedValueMaker.mString())
                .build();

        ContestVisitType retVal = contestService.authorizeContest(contestAuth);

        Assertions.assertEquals(ContestVisitType.ADMIN, retVal);
    }

    @Test
    void testAuthorizeContestWithPasswordFail() throws PortableException {
        contest.setAccessType(ContestAccessType.PASSWORD);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        passwordContestData.setPassword(TestMockedValueMaker.mString());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPasswordContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(passwordContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(TestMockedValueMaker.mString())
                .build();

        try {
            contestService.authorizeContest(contestAuth);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-003", e.getCode());
        }
    }

    @Test
    void testAuthorizeContestWithPasswordSuccess() throws PortableException {
        contest.setAccessType(ContestAccessType.PASSWORD);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        passwordContestData.setPassword(TestMockedValueMaker.mString());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPasswordContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(passwordContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(passwordContestData.getPassword())
                .build();

        ContestVisitType retVal = contestService.authorizeContest(contestAuth);

        Assertions.assertEquals(ContestVisitType.PARTICIPANT, retVal);
    }

    @Test
    void testGetContestInfoWithNoAccess() throws PortableException {
        contest.setAccessType(ContestAccessType.PRIVATE);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        passwordContestData.setPassword(TestMockedValueMaker.mString());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPrivateContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(privateContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.NO_ACCESS);

        try {
            contestService.getContestInfo(MOCKED_CONTEST_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-004", e.getCode());
        }
    }

    @Test
    void testGetContestInfoWithSuccess() throws PortableException {
        contest.setAccessType(ContestAccessType.PRIVATE);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setTitle(MOCKED_CONTEST_TITLE);
        contest.setOwner(MOCKED_USER_ID);
        privateContestData.setCoAuthor(new HashSet<>());
        user.setHandle(MOCKED_USER_HANDLE);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPrivateContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(privateContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));

        ContestInfoResponse retVal = contestService.getContestInfo(MOCKED_CONTEST_ID);

        /// region 校验返回值

        Assertions.assertEquals(ContestAccessType.PRIVATE, retVal.getAccessType());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getId());
        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getOwnerHandle());
        Assertions.assertEquals(new HashSet<>(), retVal.getCoAuthor());

        /// endregion
    }

    @Test
    void testGetContestDataWithNotStart() throws PortableException {
        contest.setAccessType(ContestAccessType.PRIVATE);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setTitle(MOCKED_CONTEST_TITLE);
        contest.setOwner(MOCKED_USER_ID);
        contest.setStartTime(new Date(new Date().getTime() + 10000));
        privateContestData.setCoAuthor(new HashSet<>());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPrivateContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(privateContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        try {
            contestService.getContestData(MOCKED_CONTEST_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-019", e.getCode());
        }
    }

    @Test
    void testGetContestDataWithErrorProblem() throws PortableException {
        contest.setAccessType(ContestAccessType.PRIVATE);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setTitle(MOCKED_CONTEST_TITLE);
        contest.setOwner(MOCKED_USER_ID);
        contest.setStartTime(new Date(0));
        privateContestData.setCoAuthor(new HashSet<>());
        privateContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        user.setHandle(MOCKED_USER_HANDLE);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPrivateContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(privateContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.empty());

        try {
            contestService.getContestData(MOCKED_CONTEST_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-07-001", e.getCode());
        }
    }

    @Test
    void testGetContestDataWithSuccess() throws PortableException {
        Long loginUserId = TestMockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(loginUserId);
        contest.setAccessType(ContestAccessType.PRIVATE);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setTitle(MOCKED_CONTEST_TITLE);
        contest.setOwner(MOCKED_USER_ID);
        contest.setStartTime(new Date(0));
        privateContestData.setCoAuthor(new HashSet<>());
        privateContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        user.setHandle(MOCKED_USER_HANDLE);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        solution.setStatus(SolutionStatusType.WRONG_ANSWER);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPrivateContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(privateContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(solutionManager.selectContestLastSolution(loginUserId, MOCKED_PROBLEM_ID, MOCKED_CONTEST_ID)).thenReturn(Optional.of(solution));


        ContestDetailResponse retVal = contestService.getContestData(MOCKED_CONTEST_ID);

        /// region 校验返回值

        Assertions.assertEquals(ContestAccessType.PRIVATE, retVal.getAccessType());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getId());
        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getOwnerHandle());
        Assertions.assertEquals(new HashSet<>(), retVal.getCoAuthor());
        Assertions.assertEquals(1, retVal.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getProblemList().get(0).getTitle());
        Assertions.assertEquals(5, retVal.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(10, retVal.getProblemList().get(0).getSubmissionCount());

        /// endregion
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