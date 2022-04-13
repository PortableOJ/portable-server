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
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAuth;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestInfoResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.user.User;
import com.portable.server.support.impl.ContestSupportImpl;
import com.portable.server.support.impl.JudgeSupportImpl;
import com.portable.server.type.ContestAccessType;
import com.portable.server.type.ContestVisitType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.test.MockedValueMaker;
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
import java.util.Collections;
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
    private ProblemData problemData;
    private Solution solution;
    private List<Contest> contestList;
    private PublicContestData publicContestData;
    private PasswordContestData passwordContestData;
    private PrivateContestData privateContestData;
    private BatchContestData batchContestData;

    private static final Integer MOCKED_PROBLEM_INDEX = 0;

    private static final Long MOCKED_USER_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_CONTEST_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_SOLUTION_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_PROBLEM_ID = MockedValueMaker.mLong();

    private static final String MOCKED_CONTEST_MONGO_ID = MockedValueMaker.mString();
    private static final String MOCKED_CONTEST_TITLE = MockedValueMaker.mString();
    private static final String MOCKED_USER_HANDLE = MockedValueMaker.mString();
    private static final String MOCKED_PROBLEM_TITLE = MockedValueMaker.mString();
    private static final String MOCKED_PROBLEM_MONGO_ID = MockedValueMaker.mString();

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
        problemData = ProblemData.builder().build();
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
                .password(MockedValueMaker.mString())
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
        passwordContestData.setPassword(MockedValueMaker.mString());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPasswordContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(passwordContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(MockedValueMaker.mString())
                .build();

        ContestVisitType retVal = contestService.authorizeContest(contestAuth);

        Assertions.assertEquals(ContestVisitType.ADMIN, retVal);
    }

    @Test
    void testAuthorizeContestWithPasswordFail() throws PortableException {
        contest.setAccessType(ContestAccessType.PASSWORD);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        passwordContestData.setPassword(MockedValueMaker.mString());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPasswordContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(passwordContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        ContestAuth contestAuth = ContestAuth.builder()
                .contestId(MOCKED_CONTEST_ID)
                .password(MockedValueMaker.mString())
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
        passwordContestData.setPassword(MockedValueMaker.mString());
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
        passwordContestData.setPassword(MockedValueMaker.mString());
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
        Long loginUserId = MockedValueMaker.mLong();
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
        Assertions.assertEquals(0, retVal.getProblemList().get(0).getId());
        Assertions.assertEquals(1, retVal.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getProblemList().get(0).getTitle());
        Assertions.assertEquals(5, retVal.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(10, retVal.getProblemList().get(0).getSubmissionCount());

        /// endregion
    }

    @Test
    void getContestAdminDataWithParticipant() throws PortableException {
        Long loginUserId = MockedValueMaker.mLong();
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

        try {
            contestService.getContestAdminData(MOCKED_CONTEST_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-004", e.getCode());
        }
    }

    @Test
    void getContestAdminDataWith() throws PortableException {
        Long loginUserId = MockedValueMaker.mLong();
        Long otherUserId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(loginUserId);
        contest.setAccessType(ContestAccessType.PRIVATE);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setTitle(MOCKED_CONTEST_TITLE);
        contest.setOwner(MOCKED_USER_ID);
        contest.setStartTime(new Date(0));
        problemData.setContestId(MOCKED_CONTEST_ID);
        privateContestData.setCoAuthor(new HashSet<>());
        privateContestData.setInviteUserSet(new HashSet<Long>() {{
            add(otherUserId);
        }});

        Problem problem2 = Problem.builder()
                .id(MockedValueMaker.mLong())
                .dataId(MockedValueMaker.mString())
                .build();

        ProblemData problemData2 = ProblemData.builder()
                .contestId(MockedValueMaker.mLong())
                .build();

        User inUser = User.builder()
                .handle(MockedValueMaker.mString())
                .build();

        Problem problem3 = Problem.builder()
                .id(MockedValueMaker.mLong())
                .dataId(MockedValueMaker.mString())
                .build();

        privateContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(problem2.getId())
                    .acceptCount(6)
                    .submissionCount(11)
                    .build());
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(problem3.getId())
                    .acceptCount(7)
                    .submissionCount(12)
                    .build());
        }});
        user.setHandle(MOCKED_USER_HANDLE);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        solution.setStatus(SolutionStatusType.WRONG_ANSWER);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPrivateContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(privateContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemManager.getProblemById(problem2.getId())).thenReturn(Optional.of(problem2));
        Mockito.when(problemManager.getProblemById(problem3.getId())).thenReturn(Optional.of(problem3));
        Mockito.when(solutionManager.selectContestLastSolution(loginUserId, MOCKED_PROBLEM_ID, MOCKED_CONTEST_ID)).thenReturn(Optional.of(solution));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(problemDataManager.getProblemData(problem2.getDataId())).thenReturn(problemData2);
        Mockito.when(problemDataManager.getProblemData(problem3.getDataId())).thenThrow(PortableException.of("S-03-001"));
        Mockito.when(userManager.getAccountById(otherUserId)).thenReturn(Optional.of(inUser));

        ContestAdminDetailResponse retVal = contestService.getContestAdminData(MOCKED_CONTEST_ID);

        /// region 校验返回值

        Assertions.assertEquals(ContestAccessType.PRIVATE, retVal.getAccessType());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getId());
        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getOwnerHandle());
        Assertions.assertEquals(new HashSet<>(), retVal.getCoAuthor());
        Assertions.assertEquals(3, retVal.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getProblemList().get(0).getTitle());
        Assertions.assertEquals(0, retVal.getProblemList().get(0).getId());
        Assertions.assertEquals(1, retVal.getProblemList().get(1).getId());
        Assertions.assertEquals(2, retVal.getProblemList().get(2).getId());
        Assertions.assertEquals(5, retVal.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(10, retVal.getProblemList().get(0).getSubmissionCount());
        Assertions.assertEquals(6, retVal.getProblemList().get(1).getAcceptCount());
        Assertions.assertEquals(11, retVal.getProblemList().get(1).getSubmissionCount());
        Assertions.assertEquals(7, retVal.getProblemList().get(2).getAcceptCount());
        Assertions.assertEquals(12, retVal.getProblemList().get(2).getSubmissionCount());
        Assertions.assertEquals(1, retVal.getInviteUserSet().size());
        Assertions.assertTrue(retVal.getInviteUserSet().contains(inUser.getHandle()));

        /// endregion
    }

    @Test
    void testGetContestProblemWithNoAccess() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.NO_ACCESS);

        try {
            contestService.getContestProblem(MOCKED_CONTEST_ID, MOCKED_PROBLEM_INDEX);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-004", e.getCode());
        }
    }

    @Test
    void testGetContestProblemWithNotStart() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(new Date().getTime() + 10000));
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        try {
            contestService.getContestProblem(MOCKED_CONTEST_ID, MOCKED_PROBLEM_INDEX);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-019", e.getCode());
        }
    }

    @Test
    void testGetContestProblemWithIndexOutOfBound() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(0));
        publicContestData.setProblemList(new ArrayList<>());
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        try {
            contestService.getContestProblem(MOCKED_CONTEST_ID, MOCKED_PROBLEM_INDEX);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-018", e.getCode());
        }
    }

    @Test
    void testGetContestProblemWithProblemMiss() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(0));
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .build());
        }});
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.empty());

        try {
            contestService.getContestProblem(MOCKED_CONTEST_ID, MOCKED_PROBLEM_INDEX);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-07-001", e.getCode());
        }
    }

    @Test
    void testGetContestProblemWithSuccess() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(0));
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        problem.setAcceptCount(6);
        problem.setSubmissionCount(11);
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));

        ProblemDetailResponse retVal = contestService.getContestProblem(MOCKED_CONTEST_ID, MOCKED_PROBLEM_INDEX);

        /// region 校验返回值

        Assertions.assertEquals(Long.valueOf(MOCKED_PROBLEM_INDEX), retVal.getId());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getTitle());
        Assertions.assertEquals(5, retVal.getAcceptCount());
        Assertions.assertEquals(10, retVal.getSubmissionCount());

        /// endregion
    }

    @Test
    void testGetContestStatusListWithNoAccess() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.NO_ACCESS);

        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageSize(10)
                .pageNum(1)
                .queryData(SolutionListQueryRequest.builder()
                        .problemId(MOCKED_PROBLEM_INDEX.longValue())
                        .statusType(SolutionStatusType.ACCEPT)
                        .userHandle(MOCKED_USER_HANDLE)
                        .build())
                .build();

        try {
            contestService.getContestStatusList(MOCKED_CONTEST_ID, pageRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-004", e.getCode());
        }
    }

    @Test
    void testGetContestStatusListWithSuccess() throws PortableException {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setStatus(SolutionStatusType.ACCEPT);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        user.setHandle(MOCKED_USER_HANDLE);
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(userManager.getAccountByHandle(MOCKED_USER_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(solutionManager.countSolution(SolutionType.CONTEST, MOCKED_USER_ID, MOCKED_CONTEST_ID, MOCKED_PROBLEM_ID, SolutionStatusType.ACCEPT)).thenReturn(100);
        Mockito.when(solutionManager.selectSolutionByPage(10, 0, SolutionType.CONTEST, MOCKED_USER_ID, MOCKED_CONTEST_ID, MOCKED_PROBLEM_ID, SolutionStatusType.ACCEPT))
                .thenReturn(Collections.singletonList(solution));

        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageSize(10)
                .pageNum(1)
                .queryData(SolutionListQueryRequest.builder()
                        .problemId(MOCKED_PROBLEM_INDEX.longValue())
                        .statusType(SolutionStatusType.ACCEPT)
                        .userHandle(MOCKED_USER_HANDLE)
                        .build())
                .build();

        PageResponse<SolutionListResponse, Void> retVal = contestService.getContestStatusList(MOCKED_CONTEST_ID, pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(100, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getData().size());
        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getData().get(0).getUserHandle());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getData().get(0).getProblemTitle());
        Assertions.assertEquals(SolutionStatusType.ACCEPT, retVal.getData().get(0).getStatus());

        /// endregion
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