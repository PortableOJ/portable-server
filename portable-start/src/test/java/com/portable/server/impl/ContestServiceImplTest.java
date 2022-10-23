package com.portable.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.BatchManagerImpl;
import com.portable.server.manager.impl.ContestDataManagerImpl;
import com.portable.server.manager.impl.ContestManagerImpl;
import com.portable.server.manager.impl.ProblemDataManagerImpl;
import com.portable.server.manager.impl.ProblemManagerImpl;
import com.portable.server.manager.impl.SolutionManagerImpl;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.ContestRankItem;
import com.portable.server.model.contest.ContestRankProblemStatus;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAddProblem;
import com.portable.server.model.request.contest.ContestAuth;
import com.portable.server.model.request.contest.ContestContentRequest;
import com.portable.server.model.request.contest.ContestRankPageRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestInfoResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.contest.ContestRankListResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.User;
import com.portable.server.service.impl.ContestServiceImpl;
import com.portable.server.support.impl.ContestSupportImpl;
import com.portable.server.support.impl.JudgeSupportImpl;
import com.portable.server.test.MockedValueMaker;
import com.portable.server.test.UserContextBuilder;
import com.portable.server.type.ContestAccessType;
import com.portable.server.type.ContestVisitType;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
    private BatchManagerImpl batchManager;

    @Mock
    private JudgeSupportImpl judgeSupport;

    @Mock
    private ContestSupportImpl contestSupport;

    private User user;
    private Batch batch;
    private Contest contest;
    private Problem problem;
    private ProblemData problemData;
    private Solution solution;
    private SolutionData solutionData;
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
    private static final Long MOCKED_BATCH_ID = MockedValueMaker.mLong();

    private static final String MOCKED_CONTEST_MONGO_ID = MockedValueMaker.mString();
    private static final String MOCKED_CONTEST_TITLE = MockedValueMaker.mString();
    private static final String MOCKED_USER_HANDLE = MockedValueMaker.mString();
    private static final String MOCKED_PROBLEM_TITLE = MockedValueMaker.mString();
    private static final String MOCKED_PROBLEM_MONGO_ID = MockedValueMaker.mString();
    private static final String MOCKED_SOLUTION_MONGO_ID = MockedValueMaker.mString();
    private static final String MOCKED_CODE = MockedValueMaker.mString();

    private UserContextBuilder userContextBuilder;

    private MockedStatic<ContestVisitType> contestVisitTypeMockedStatic;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(MOCKED_USER_ID)
                .build();
        batch = Batch.builder()
                .id(MOCKED_BATCH_ID)
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
        solutionData = SolutionData.builder()
                .id(MOCKED_SOLUTION_MONGO_ID)
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
    void testAuthorizeContestWithNoContestData() {
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
    void testAuthorizeContestWithNoPasswordVisit() {
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
    void testAuthorizeContestWithPasswordPublicVisit() {
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
    void testAuthorizeContestWithNoPasswordAdmin() {
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
    void testAuthorizeContestWithPasswordFailAdmin() {
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
    void testAuthorizeContestWithPasswordFail() {
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
    void testAuthorizeContestWithPasswordSuccess() {
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
    void testGetContestInfoWithNoAccess() {
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
    void testGetContestInfoWithSuccess() {
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
    void testGetContestDataWithNotStart() {
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
    void testGetContestDataWithErrorProblem() {
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
    void testGetContestDataWithSuccess() {
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
    void getContestAdminDataWithParticipant() {
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
    void getContestAdminDataWith() {
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
    void testGetContestProblemWithNoAccess() {
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
    void testGetContestProblemWithNotStart() {
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
    void testGetContestProblemWithIndexOutOfBound() {
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
    void testGetContestProblemWithProblemMiss() {
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
    void testGetContestProblemWithSuccess() {
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
    void testGetContestStatusListWithNoAccess() {
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
    void testGetContestStatusListWithIndexOutOfBound() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setStatus(SolutionStatusType.ACCEPT);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        user.setHandle(MOCKED_USER_HANDLE);
        publicContestData.setProblemList(new ArrayList<>());

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

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
            Assertions.assertEquals("A-08-018", e.getCode());
        }
    }

    @Test
    void testGetContestStatusListWithNoUser() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
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
        Mockito.when(userManager.getAccountByHandle(MOCKED_USER_HANDLE)).thenReturn(Optional.empty());

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
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testGetContestStatusListWithSuccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setContestId(MOCKED_CONTEST_ID);
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
        Mockito.when(solutionManager.selectSolutionByPage(10, 0, SolutionType.CONTEST, MOCKED_USER_ID, MOCKED_CONTEST_ID, MOCKED_PROBLEM_ID, SolutionStatusType.ACCEPT, null, null))
                .thenReturn(Collections.singletonList(solution));

        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageSize(10)
                .pageNum(1)
                .queryData(SolutionListQueryRequest.builder()
                        .problemId(MOCKED_PROBLEM_INDEX.longValue())
                        .statusType(SolutionStatusType.ACCEPT)
                        .userHandle(MOCKED_USER_HANDLE)
                        .afterId(null)
                        .beforeId(null)
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
    void testGetContestSolutionWithNoSolution() {
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.empty());

        try {
            contestService.getContestSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-05-001", e.getCode());
        }
    }

    @Test
    void testGetContestSolutionWithNoAccess() {
        solution.setContestId(MOCKED_CONTEST_ID);
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setAccessType(ContestAccessType.PUBLIC);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.NO_ACCESS);

        try {
            contestService.getContestSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-004", e.getCode());
        }
    }

    @Test
    void testGetContestSolutionWithNotEndNotSelfNotAdmin() {
        userContextBuilder.withNormalLoginIn();

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(10000);
        contest.setOwner(MockedValueMaker.mLong());
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setProblemList(new ArrayList<>());
        publicContestData.setCoAuthor(new HashSet<>());
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setContestId(MOCKED_CONTEST_ID);
        solution.setStatus(SolutionStatusType.ACCEPT);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        try {
            contestService.getContestSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-005", e.getCode());
        }
    }

    @Test
    void testGetContestSolutionWithEndNotSelfNotAdmin() {
        userContextBuilder.withNormalLoginIn();

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(0));
        contest.setDuration(0);
        contest.setOwner(MockedValueMaker.mLong());
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        solution.setContestId(MOCKED_CONTEST_ID);
        solution.setSubmitTime(MockedValueMaker.mDate());
        solution.setStatus(SolutionStatusType.ACCEPT);
        solutionData.setCode(MOCKED_CODE);

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(solutionManager.getSolutionData(MOCKED_SOLUTION_MONGO_ID)).thenReturn(solutionData);

        SolutionDetailResponse retVal = contestService.getContestSolution(MOCKED_SOLUTION_ID);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_SOLUTION_ID, retVal.getId());
        Assertions.assertEquals(solution.getSubmitTime(), retVal.getSubmitTime());
        Assertions.assertEquals(0, retVal.getProblemId());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getContestId());
        Assertions.assertEquals(MOCKED_CODE, retVal.getCode());

        /// endregion
    }

    @Test
    void testGetContestSolutionWithNotEndSelfNotAdmin() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(100000);
        contest.setOwner(MockedValueMaker.mLong());
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        solution.setContestId(MOCKED_CONTEST_ID);
        solution.setSubmitTime(MockedValueMaker.mDate());
        solution.setStatus(SolutionStatusType.ACCEPT);
        solutionData.setCode(MOCKED_CODE);

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);
        Mockito.when(solutionManager.getSolutionData(MOCKED_SOLUTION_MONGO_ID)).thenReturn(solutionData);

        SolutionDetailResponse retVal = contestService.getContestSolution(MOCKED_SOLUTION_ID);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_SOLUTION_ID, retVal.getId());
        Assertions.assertEquals(solution.getSubmitTime(), retVal.getSubmitTime());
        Assertions.assertEquals(0, retVal.getProblemId());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getContestId());
        Assertions.assertEquals(MOCKED_CODE, retVal.getCode());

        /// endregion
    }

    @Test
    void testGetContestSolutionWithNotEndNotSelfAdmin() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(100000);
        contest.setOwner(MOCKED_USER_ID);
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MockedValueMaker.mLong());
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        solution.setContestId(MOCKED_CONTEST_ID);
        solution.setSubmitTime(MockedValueMaker.mDate());
        solution.setStatus(SolutionStatusType.ACCEPT);
        solutionData.setCode(MOCKED_CODE);

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);
        Mockito.when(solutionManager.getSolutionData(MOCKED_SOLUTION_MONGO_ID)).thenReturn(solutionData);

        SolutionDetailResponse retVal = contestService.getContestSolution(MOCKED_SOLUTION_ID);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_SOLUTION_ID, retVal.getId());
        Assertions.assertEquals(solution.getSubmitTime(), retVal.getSubmitTime());
        Assertions.assertEquals(0, retVal.getProblemId());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getContestId());
        Assertions.assertEquals(MOCKED_CODE, retVal.getCode());

        /// endregion
    }

    @Test
    void testGetContestTestStatusListWithParticipant() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);

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
            contestService.getContestTestStatusList(MOCKED_CONTEST_ID, pageRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-006", e.getCode());
        }
    }

    @Test
    void testGetContestTestStatusListWithIndexOutOfBound() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setStatus(SolutionStatusType.ACCEPT);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        user.setHandle(MOCKED_USER_HANDLE);
        publicContestData.setProblemList(new ArrayList<>());

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);

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
            contestService.getContestTestStatusList(MOCKED_CONTEST_ID, pageRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-018", e.getCode());
        }
    }

    @Test
    void testGetContestTestStatusListWithNoUser() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
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
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(userManager.getAccountByHandle(MOCKED_USER_HANDLE)).thenReturn(Optional.empty());

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
            contestService.getContestTestStatusList(MOCKED_CONTEST_ID, pageRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testGetContestTestStatusListWithSuccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MOCKED_USER_ID);
        solution.setContestId(MOCKED_CONTEST_ID);
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
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(userManager.getAccountByHandle(MOCKED_USER_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(solutionManager.countSolution(SolutionType.TEST_CONTEST, MOCKED_USER_ID, MOCKED_CONTEST_ID, MOCKED_PROBLEM_ID, SolutionStatusType.ACCEPT)).thenReturn(100);
        Mockito.when(solutionManager.selectSolutionByPage(10, 0, SolutionType.TEST_CONTEST, MOCKED_USER_ID, MOCKED_CONTEST_ID, MOCKED_PROBLEM_ID, SolutionStatusType.ACCEPT, null, null))
                .thenReturn(Collections.singletonList(solution));

        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageSize(10)
                .pageNum(1)
                .queryData(SolutionListQueryRequest.builder()
                        .problemId(MOCKED_PROBLEM_INDEX.longValue())
                        .statusType(SolutionStatusType.ACCEPT)
                        .userHandle(MOCKED_USER_HANDLE)
                        .beforeId(null)
                        .afterId(null)
                        .build())
                .build();

        PageResponse<SolutionListResponse, Void> retVal = contestService.getContestTestStatusList(MOCKED_CONTEST_ID, pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(100, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getData().size());
        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getData().get(0).getUserHandle());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getData().get(0).getProblemTitle());
        Assertions.assertEquals(SolutionStatusType.ACCEPT, retVal.getData().get(0).getStatus());

        /// endregion
    }

    @Test
    void testGetContestTestSolutionWithNoSolution() {
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.empty());

        try {
            contestService.getContestTestSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-05-001", e.getCode());
        }
    }

    @Test
    void testGetContestTestSolutionWithNoAccess() {
        solution.setContestId(MOCKED_CONTEST_ID);
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setAccessType(ContestAccessType.PUBLIC);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);

        try {
            contestService.getContestTestSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-006", e.getCode());
        }
    }

    @Test
    void testGetContestTestSolutionWithNotEndNotSelfAdmin() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(100000);
        contest.setOwner(MOCKED_USER_ID);
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setUserId(MockedValueMaker.mLong());
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        solution.setContestId(MOCKED_CONTEST_ID);
        solution.setSubmitTime(MockedValueMaker.mDate());
        solution.setStatus(SolutionStatusType.ACCEPT);
        solutionData.setCode(MOCKED_CODE);

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(solutionManager.getSolutionData(MOCKED_SOLUTION_MONGO_ID)).thenReturn(solutionData);

        SolutionDetailResponse retVal = contestService.getContestTestSolution(MOCKED_SOLUTION_ID);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_SOLUTION_ID, retVal.getId());
        Assertions.assertEquals(solution.getSubmitTime(), retVal.getSubmitTime());
        Assertions.assertEquals(0, retVal.getProblemId());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getContestId());
        Assertions.assertEquals(MOCKED_CODE, retVal.getCode());

        /// endregion
    }

    @Test
    void testGetContestRankWithNoAccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(100000);
        contest.setOwner(MOCKED_USER_ID);
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.NO_ACCESS);

        PageRequest<ContestRankPageRequest> pageRequest = PageRequest.<ContestRankPageRequest>builder()
                .queryData(ContestRankPageRequest.builder()
                        .freeze(true)
                        .build())
                .pageNum(1)
                .pageSize(10)
                .build();

        try {
            contestService.getContestRank(MOCKED_CONTEST_ID, pageRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-004", e.getCode());
        }
    }

    @Test
    void testGetContestRankWithNoFreezeNoAccess() {

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(100000);
        contest.setOwner(MOCKED_USER_ID);
        contest.setAccessType(ContestAccessType.PUBLIC);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);

        PageRequest<ContestRankPageRequest> pageRequest = PageRequest.<ContestRankPageRequest>builder()
                .queryData(ContestRankPageRequest.builder()
                        .freeze(false)
                        .build())
                .pageNum(1)
                .pageSize(10)
                .build();

        try {
            contestService.getContestRank(MOCKED_CONTEST_ID, pageRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-034", e.getCode());
        }
    }

    @Test
    void testGetContestRankWithSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withHandle(MOCKED_USER_HANDLE);

        ContestRankItem contestRankItem = ContestRankItem.builder()
                .rank(0)
                .userId(MOCKED_USER_ID)
                .totalCost(MockedValueMaker.mLong())
                .totalSolve(MockedValueMaker.mInt())
                .submitStatus(new HashMap<Integer, ContestRankProblemStatus>() {{
                    put(0, ContestRankProblemStatus.builder()
                            .firstSolveId(null)
                            .runningSubmit(5)
                            .penaltyTimes(10)
                            .solveTime(20L)
                            .build());
                }})
                .noFreezeSubmitStatus(new HashMap<>(16))
                .build();
        List<ContestRankItem> contestRankItemList = Collections.singletonList(contestRankItem);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(100000);
        contest.setOwner(MOCKED_USER_ID);
        contest.setAccessType(ContestAccessType.PUBLIC);
        user.setHandle(MOCKED_USER_HANDLE);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        publicContestData.setFreezeTime(0);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);
        Mockito.when(contestSupport.getContestRankLen(MOCKED_CONTEST_ID, true)).thenReturn(100);
        Mockito.when(contestSupport.getContestRank(MOCKED_CONTEST_ID, 10, 0, true)).thenReturn(contestRankItemList);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(contestSupport.getContestByUserId(MOCKED_CONTEST_ID, MOCKED_USER_ID, true)).thenReturn(contestRankItem);

        PageRequest<ContestRankPageRequest> pageRequest = PageRequest.<ContestRankPageRequest>builder()
                .queryData(ContestRankPageRequest.builder().freeze(true).build()).pageNum(1).pageSize(10).build();

        PageResponse<ContestRankListResponse, ContestRankListResponse> retVal = contestService.getContestRank(MOCKED_CONTEST_ID, pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(100, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getData().size());
        Assertions.assertEquals(0, retVal.getData().get(0).getRank());
        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getData().get(0).getUserHandle());
        Assertions.assertEquals(5, retVal.getData().get(0).getSubmitStatus().get(0).getRunningSubmit());
        Assertions.assertEquals(10, retVal.getData().get(0).getSubmitStatus().get(0).getPenaltyTimes());
        Assertions.assertEquals(20L, retVal.getData().get(0).getSubmitStatus().get(0).getSolveTime());

        Assertions.assertEquals(0, retVal.getMetaData().getRank());
        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getMetaData().getUserHandle());
        Assertions.assertEquals(5, retVal.getMetaData().getSubmitStatus().get(0).getRunningSubmit());
        Assertions.assertEquals(10, retVal.getMetaData().getSubmitStatus().get(0).getPenaltyTimes());
        Assertions.assertEquals(20L, retVal.getMetaData().getSubmitStatus().get(0).getSolveTime());

        /// endregion
    }

    @Test
    void testSubmitWithVisit() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.VISIT);

        SubmitSolutionRequest submitSolutionRequest = SubmitSolutionRequest.builder()
                .problemId(0L)
                .contestId(MOCKED_CONTEST_ID)
                .languageType(LanguageType.CPP11)
                .code(MOCKED_CODE)
                .build();

        try {
            contestService.submit(submitSolutionRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-007", e.getCode());
        }
    }

    @Test
    void testSubmitWithNotStart() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(new Date().getTime() + 10000));
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);

        SubmitSolutionRequest submitSolutionRequest = SubmitSolutionRequest.builder()
                .problemId(0L)
                .contestId(MOCKED_CONTEST_ID)
                .languageType(LanguageType.CPP11)
                .code(MOCKED_CODE)
                .build();

        try {
            contestService.submit(submitSolutionRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-008", e.getCode());
        }
    }

    @Test
    void testSubmitWithIsEnd() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(0));
        contest.setDuration(10);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);

        SubmitSolutionRequest submitSolutionRequest = SubmitSolutionRequest.builder()
                .problemId(0L)
                .contestId(MOCKED_CONTEST_ID)
                .languageType(LanguageType.CPP11)
                .code(MOCKED_CODE)
                .build();

        try {
            contestService.submit(submitSolutionRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-008", e.getCode());
        }
    }

    @Test
    void testSubmitWithParticipant() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date());
        contest.setDuration(1000);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setTestName(new ArrayList<>());
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        publicContestData.setFreezeTime(0);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(solutionManager.newSolution()).thenCallRealMethod();
        Mockito.when(solutionManager.newSolutionData(Mockito.any())).thenCallRealMethod();

        Mockito.doAnswer(invocationOnMock -> {
            SolutionData solutionData = invocationOnMock.getArgument(0);
            solutionData.setId(MOCKED_SOLUTION_MONGO_ID);
            return null;
        }).when(solutionManager).insertSolutionData(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> {
            Solution solution = invocationOnMock.getArgument(0);
            solution.setId(MOCKED_SOLUTION_ID);
            return null;
        }).when(solutionManager).insertSolution(Mockito.any());

        SubmitSolutionRequest submitSolutionRequest = SubmitSolutionRequest.builder()
                .problemId(Long.valueOf(MOCKED_PROBLEM_INDEX))
                .contestId(MOCKED_CONTEST_ID)
                .languageType(LanguageType.CPP11)
                .code(MOCKED_CODE)
                .build();

        Long retVal = contestService.submit(submitSolutionRequest);

        Assertions.assertEquals(MOCKED_SOLUTION_ID, retVal);

        /// region 校验写入的提交数据

        ArgumentCaptor<SolutionData> solutionDataArgumentCaptor = ArgumentCaptor.forClass(SolutionData.class);
        Mockito.verify(solutionManager).insertSolutionData(solutionDataArgumentCaptor.capture());
        SolutionData solutionDataCP = solutionDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CODE, solutionDataCP.getCode());

        /// endregion

        /// region 校验写入的提交

        ArgumentCaptor<Solution> solutionArgumentCaptor = ArgumentCaptor.forClass(Solution.class);
        Mockito.verify(solutionManager).insertSolution(solutionArgumentCaptor.capture());
        Solution solutionCP = solutionArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_USER_ID, solutionCP.getUserId());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, solutionCP.getProblemId());
        Assertions.assertEquals(MOCKED_CONTEST_ID, solutionCP.getContestId());
        Assertions.assertEquals(SolutionType.CONTEST, solutionCP.getSolutionType());

        /// endregion
    }

    @Test
    void testSubmitWithAdmin() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(0));
        contest.setDuration(1);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setTestName(new ArrayList<>());
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setCoAuthor(new HashSet<>());
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});
        publicContestData.setFreezeTime(0);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(solutionManager.newSolution()).thenCallRealMethod();
        Mockito.when(solutionManager.newSolutionData(Mockito.any())).thenCallRealMethod();

        Mockito.doAnswer(invocationOnMock -> {
            SolutionData solutionData = invocationOnMock.getArgument(0);
            solutionData.setId(MOCKED_SOLUTION_MONGO_ID);
            return null;
        }).when(solutionManager).insertSolutionData(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> {
            Solution solution = invocationOnMock.getArgument(0);
            solution.setId(MOCKED_SOLUTION_ID);
            return null;
        }).when(solutionManager).insertSolution(Mockito.any());

        SubmitSolutionRequest submitSolutionRequest = SubmitSolutionRequest.builder()
                .problemId(Long.valueOf(MOCKED_PROBLEM_INDEX))
                .contestId(MOCKED_CONTEST_ID)
                .languageType(LanguageType.CPP11)
                .code(MOCKED_CODE)
                .build();

        Long retVal = contestService.submit(submitSolutionRequest);

        Assertions.assertEquals(MOCKED_SOLUTION_ID, retVal);

        /// region 校验写入的提交数据

        ArgumentCaptor<SolutionData> solutionDataArgumentCaptor = ArgumentCaptor.forClass(SolutionData.class);
        Mockito.verify(solutionManager).insertSolutionData(solutionDataArgumentCaptor.capture());
        SolutionData solutionDataCP = solutionDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CODE, solutionDataCP.getCode());

        /// endregion

        /// region 校验写入的提交

        ArgumentCaptor<Solution> solutionArgumentCaptor = ArgumentCaptor.forClass(Solution.class);
        Mockito.verify(solutionManager).insertSolution(solutionArgumentCaptor.capture());
        Solution solutionCP = solutionArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_USER_ID, solutionCP.getUserId());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, solutionCP.getProblemId());
        Assertions.assertEquals(MOCKED_CONTEST_ID, solutionCP.getContestId());
        Assertions.assertEquals(SolutionType.TEST_CONTEST, solutionCP.getSolutionType());

        /// endregion
    }

    @Test
    void testCreateContestWithSameProblem() {
        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(new Date())
                .duration(10)
                .accessType(ContestAccessType.PUBLIC)
                .password(null)
                .inviteUserSet(null)
                .batchId(null)
                .problemList(Arrays.asList(MOCKED_PROBLEM_ID, MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<>())
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.createContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-020", e.getCode());
        }
    }

    @Test
    void testCreateContestWithNotExistProblem() {
        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenAnswer(invocationOnMock -> {
            List<Long> problemList = invocationOnMock.getArgument(0);
            if (problemList.contains(MOCKED_PROBLEM_ID)) {
                return Collections.singletonList(MOCKED_PROBLEM_ID);
            }
            return new ArrayList<>();
        });

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(new Date())
                .duration(10)
                .accessType(ContestAccessType.PRIVATE)
                .password(null)
                .inviteUserSet(null)
                .batchId(null)
                .problemList(Arrays.asList(MOCKED_PROBLEM_ID, MockedValueMaker.mLong()))
                .coAuthor(new HashSet<>())
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.createContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-010", e.getCode());
        }
    }

    @Test
    void testCreateContestWithNotExistBatch() {
        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.empty());

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(new Date())
                .duration(10)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Arrays.asList(MOCKED_PROBLEM_ID, MockedValueMaker.mLong()))
                .coAuthor(new HashSet<>())
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.createContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-006", e.getCode());
        }
    }

    @Test
    void testCreateContestWithNotOwnerBatch() {
        userContextBuilder.withNormalLoginIn();

        batch.setOwner(MOCKED_USER_ID);
        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(new Date())
                .duration(10)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Arrays.asList(MOCKED_PROBLEM_ID, MockedValueMaker.mLong()))
                .coAuthor(new HashSet<>())
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.createContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-008", e.getCode());
        }
    }

    @Test
    void testCreateContestWithPublicSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);

        Long otherUserId = MockedValueMaker.mLong();
        Date startTime = MockedValueMaker.mDate();
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(userManager.changeHandleToUserId(Mockito.anyCollection())).thenReturn(new HashSet<Long>() {{
            add(otherUserId);
        }});
        Mockito.doAnswer(invocationOnMock -> {
            PublicContestData publicContestData = invocationOnMock.getArgument(0);
            publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
            return null;
        }).when(contestDataManager).insertContestData(Mockito.any());

        Mockito.doAnswer(invocationOnMock -> {
            Contest contest = invocationOnMock.getArgument(0);
            contest.setId(MOCKED_CONTEST_ID);
            return null;
        }).when(contestManager).insertContest(Mockito.any());
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.PUBLIC)
                .password(null)
                .inviteUserSet(null)
                .batchId(null)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        Long retVal = contestService.createContest(contestContentRequest);

        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal);

        /// region 校验写入的比赛数据

        ArgumentCaptor<PublicContestData> publicContestDataArgumentCaptor = ArgumentCaptor.forClass(PublicContestData.class);
        Mockito.verify(contestDataManager).insertContestData(publicContestDataArgumentCaptor.capture());
        PublicContestData publicContestDataCP = publicContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, publicContestDataCP.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, publicContestDataCP.getProblemList().get(0).getProblemId());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(0).getSubmissionCount());
        Assertions.assertEquals(contestContentRequest.getAnnouncement(), publicContestDataCP.getAnnouncement());
        Assertions.assertEquals(1, publicContestDataCP.getCoAuthor().size());
        Assertions.assertTrue(publicContestDataCP.getCoAuthor().contains(otherUserId));

        /// endregion

        /// region 校验写入比赛数据

        ArgumentCaptor<Contest> contestArgumentCaptor = ArgumentCaptor.forClass(Contest.class);
        Mockito.verify(contestManager).insertContest(contestArgumentCaptor.capture());
        Contest contestCP = contestArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_MONGO_ID, contestCP.getDataId());
        Assertions.assertEquals(MOCKED_CONTEST_TITLE, contestCP.getTitle());
        Assertions.assertEquals(ContestAccessType.PUBLIC, contestCP.getAccessType());
        Assertions.assertEquals(startTime, contestCP.getStartTime());
        Assertions.assertEquals(duration, contestCP.getDuration());

        /// endregion

        /// region 校验写入的题目数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, problemDataCP.getContestId());

        /// endregion
    }

    @Test
    void testCreateContestWithPasswordSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);

        String password = MockedValueMaker.mString();
        Long otherUserId = MockedValueMaker.mLong();
        Date startTime = MockedValueMaker.mDate();
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(userManager.changeHandleToUserId(Mockito.anyCollection())).thenReturn(new HashSet<Long>() {{
            add(otherUserId);
        }});
        Mockito.doAnswer(invocationOnMock -> {
            PasswordContestData passwordContestData = invocationOnMock.getArgument(0);
            passwordContestData.setId(MOCKED_CONTEST_MONGO_ID);
            return null;
        }).when(contestDataManager).insertContestData(Mockito.any());

        Mockito.doAnswer(invocationOnMock -> {
            Contest contest = invocationOnMock.getArgument(0);
            contest.setId(MOCKED_CONTEST_ID);
            return null;
        }).when(contestManager).insertContest(Mockito.any());
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.PASSWORD)
                .password(password)
                .inviteUserSet(null)
                .batchId(null)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        Long retVal = contestService.createContest(contestContentRequest);

        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal);

        /// region 校验写入的比赛数据

        ArgumentCaptor<PasswordContestData> passwordContestDataArgumentCaptor = ArgumentCaptor.forClass(PasswordContestData.class);
        Mockito.verify(contestDataManager).insertContestData(passwordContestDataArgumentCaptor.capture());
        PasswordContestData passwordContestDataCP = passwordContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, passwordContestDataCP.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, passwordContestDataCP.getProblemList().get(0).getProblemId());
        Assertions.assertEquals(0, passwordContestDataCP.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(0, passwordContestDataCP.getProblemList().get(0).getSubmissionCount());
        Assertions.assertEquals(password, passwordContestDataCP.getPassword());
        Assertions.assertEquals(contestContentRequest.getAnnouncement(), passwordContestDataCP.getAnnouncement());
        Assertions.assertEquals(1, passwordContestDataCP.getCoAuthor().size());
        Assertions.assertTrue(passwordContestDataCP.getCoAuthor().contains(otherUserId));

        /// endregion

        /// region 校验写入比赛数据

        ArgumentCaptor<Contest> contestArgumentCaptor = ArgumentCaptor.forClass(Contest.class);
        Mockito.verify(contestManager).insertContest(contestArgumentCaptor.capture());
        Contest contestCP = contestArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_MONGO_ID, contestCP.getDataId());
        Assertions.assertEquals(MOCKED_CONTEST_TITLE, contestCP.getTitle());
        Assertions.assertEquals(ContestAccessType.PASSWORD, contestCP.getAccessType());
        Assertions.assertEquals(startTime, contestCP.getStartTime());
        Assertions.assertEquals(duration, contestCP.getDuration());

        /// endregion

        /// region 校验写入的题目数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, problemDataCP.getContestId());

        /// endregion
    }

    @Test
    void testCreateContestWithPrivateSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);

        Long otherUserId = MockedValueMaker.mLong();
        Long anotherUserId = MockedValueMaker.mLong();
        String anotherUserHandle = MockedValueMaker.mString();
        Date startTime = MockedValueMaker.mDate();
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(userManager.changeHandleToUserId(new HashSet<String>() {{
            add(MOCKED_USER_HANDLE);
        }})).thenReturn(new HashSet<Long>() {{
            add(otherUserId);
        }});
        Mockito.when(userManager.changeHandleToUserId(new HashSet<String>() {{
            add(anotherUserHandle);
        }})).thenReturn(new HashSet<Long>() {{
            add(anotherUserId);
        }});
        Mockito.doAnswer(invocationOnMock -> {
            PrivateContestData privateContestData = invocationOnMock.getArgument(0);
            privateContestData.setId(MOCKED_CONTEST_MONGO_ID);
            return null;
        }).when(contestDataManager).insertContestData(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> {
            Contest contest = invocationOnMock.getArgument(0);
            contest.setId(MOCKED_CONTEST_ID);
            return null;
        }).when(contestManager).insertContest(Mockito.any());
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.PRIVATE)
                .password(null)
                .inviteUserSet(new HashSet<String>() {{
                    add(anotherUserHandle);
                }})
                .batchId(null)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        Long retVal = contestService.createContest(contestContentRequest);

        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal);

        /// region 校验写入的比赛数据

        ArgumentCaptor<PrivateContestData> privateContestDataArgumentCaptor = ArgumentCaptor.forClass(PrivateContestData.class);
        Mockito.verify(contestDataManager).insertContestData(privateContestDataArgumentCaptor.capture());
        PrivateContestData privateContestDataCP = privateContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, privateContestDataCP.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, privateContestDataCP.getProblemList().get(0).getProblemId());
        Assertions.assertEquals(0, privateContestDataCP.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(0, privateContestDataCP.getProblemList().get(0).getSubmissionCount());
        Assertions.assertEquals(contestContentRequest.getAnnouncement(), privateContestDataCP.getAnnouncement());
        Assertions.assertEquals(1, privateContestDataCP.getCoAuthor().size());
        Assertions.assertTrue(privateContestDataCP.getCoAuthor().contains(otherUserId));
        Assertions.assertTrue(privateContestDataCP.getInviteUserSet().contains(anotherUserId));

        /// endregion

        /// region 校验写入比赛数据

        ArgumentCaptor<Contest> contestArgumentCaptor = ArgumentCaptor.forClass(Contest.class);
        Mockito.verify(contestManager).insertContest(contestArgumentCaptor.capture());
        Contest contestCP = contestArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_MONGO_ID, contestCP.getDataId());
        Assertions.assertEquals(MOCKED_CONTEST_TITLE, contestCP.getTitle());
        Assertions.assertEquals(ContestAccessType.PRIVATE, contestCP.getAccessType());
        Assertions.assertEquals(startTime, contestCP.getStartTime());
        Assertions.assertEquals(duration, contestCP.getDuration());

        /// endregion

        /// region 校验写入的题目数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, problemDataCP.getContestId());

        /// endregion
    }

    @Test
    void testCreateContestWithBatchSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        batch.setOwner(MOCKED_USER_ID);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);

        Long otherUserId = MockedValueMaker.mLong();
        Date startTime = MockedValueMaker.mDate();
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.newContest()).thenCallRealMethod();
        Mockito.when(contestDataManager.newContestData(Mockito.any())).thenCallRealMethod();
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        Mockito.when(userManager.changeHandleToUserId(new HashSet<String>() {{
            add(MOCKED_USER_HANDLE);
        }})).thenReturn(new HashSet<Long>() {{
            add(otherUserId);
        }});
        Mockito.doAnswer(invocationOnMock -> {
            BatchContestData batchContestData = invocationOnMock.getArgument(0);
            batchContestData.setId(MOCKED_CONTEST_MONGO_ID);
            return null;
        }).when(contestDataManager).insertContestData(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> {
            Contest contest = invocationOnMock.getArgument(0);
            contest.setId(MOCKED_CONTEST_ID);
            return null;
        }).when(contestManager).insertContest(Mockito.any());
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(null)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        Long retVal = contestService.createContest(contestContentRequest);

        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal);

        /// region 校验写入的比赛数据

        ArgumentCaptor<BatchContestData> batchContestDataArgumentCaptor = ArgumentCaptor.forClass(BatchContestData.class);
        Mockito.verify(contestDataManager).insertContestData(batchContestDataArgumentCaptor.capture());
        BatchContestData batchContestDataCP = batchContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, batchContestDataCP.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, batchContestDataCP.getProblemList().get(0).getProblemId());
        Assertions.assertEquals(0, batchContestDataCP.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(0, batchContestDataCP.getProblemList().get(0).getSubmissionCount());
        Assertions.assertEquals(contestContentRequest.getAnnouncement(), batchContestDataCP.getAnnouncement());
        Assertions.assertEquals(1, batchContestDataCP.getCoAuthor().size());
        Assertions.assertTrue(batchContestDataCP.getCoAuthor().contains(otherUserId));
        Assertions.assertEquals(MOCKED_BATCH_ID, batchContestDataCP.getBatchId());

        /// endregion

        /// region 校验写入比赛数据

        ArgumentCaptor<Contest> contestArgumentCaptor = ArgumentCaptor.forClass(Contest.class);
        Mockito.verify(contestManager).insertContest(contestArgumentCaptor.capture());
        Contest contestCP = contestArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_MONGO_ID, contestCP.getDataId());
        Assertions.assertEquals(MOCKED_CONTEST_TITLE, contestCP.getTitle());
        Assertions.assertEquals(ContestAccessType.BATCH, contestCP.getAccessType());
        Assertions.assertEquals(startTime, contestCP.getStartTime());
        Assertions.assertEquals(duration, contestCP.getDuration());

        /// endregion

        /// region 校验写入的题目数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, problemDataCP.getContestId());

        /// endregion

        /// region 校验更新了批量用户组

        Mockito.verify(batchManager).updateBatchContest(null, null);
        Mockito.verify(batchManager).updateBatchContest(MOCKED_BATCH_ID, MOCKED_CONTEST_ID);

        /// endregion
    }

    @Test
    void testUpdateContestWithNoAccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);

        Date startTime = MockedValueMaker.mDate();
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.updateContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-011", e.getCode());
        }
    }

    @Test
    void testUpdateContestWithNotStartToStart() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(new Date().getTime() + 10000));
        contest.setDuration(10000);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);

        Date startTime = new Date(0);
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.updateContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-012", e.getCode());
        }
    }

    @Test
    void testUpdateContestWithNotStartSameProblem() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(new Date().getTime() + 10000));
        contest.setDuration(10000);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);

        Date startTime = new Date(new Date().getTime() + 20000);
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Arrays.asList(MOCKED_PROBLEM_ID, MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.updateContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-020", e.getCode());
        }
    }

    @Test
    void testUpdateContestWithNotStartSuccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(new Date().getTime() + 10000));
        contest.setDuration(10000);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setProblemList(new ArrayList<>());

        Date startTime = new Date(new Date().getTime() + 20000);
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(userManager.changeHandleToUserId(Mockito.anyCollection())).thenReturn(new HashSet<Long>() {{
            add(MOCKED_USER_ID);
        }});
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        contestService.updateContest(contestContentRequest);

        /// region 校验题目的绑定情况

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, problemDataCP.getContestId());

        /// endregion

        /// region 校验写入的比赛数据

        ArgumentCaptor<PublicContestData> publicContestDataArgumentCaptor = ArgumentCaptor.forClass(PublicContestData.class);
        Mockito.verify(contestDataManager).saveContestData(publicContestDataArgumentCaptor.capture());
        PublicContestData publicContestDataCP = publicContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, publicContestDataCP.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, publicContestDataCP.getProblemList().get(0).getProblemId());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(0).getSubmissionCount());
        Assertions.assertEquals(contestContentRequest.getAnnouncement(), publicContestDataCP.getAnnouncement());
        Assertions.assertEquals(1, publicContestDataCP.getCoAuthor().size());

        /// endregion

        /// region 校验写入比赛数据

        Mockito.verify(contestManager).updateStartTime(MOCKED_CONTEST_ID, startTime);
        Mockito.verify(contestManager).updateDuration(MOCKED_CONTEST_ID, duration);
        Mockito.verify(contestManager).updateTitle(MOCKED_CONTEST_ID, MOCKED_CONTEST_TITLE);

        /// endregion

    }

    @Test
    void testUpdateContestWithNotEndDeleteProblem() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(new Date().getTime() - 1000));
        contest.setDuration(1000);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MockedValueMaker.mLong())
                    .build());
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .build());
        }});
        publicContestData.setCoAuthor(new HashSet<>());

        Date startTime = new Date(new Date().getTime() + 20000);
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        try {
            contestService.updateContest(contestContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-009", e.getCode());
        }

        /// region 校验题目的绑定情况

        Mockito.verify(problemDataManager, Mockito.never()).updateProblemData(Mockito.any());

        /// endregion

        /// region 校验写入的比赛数据

        Mockito.verify(contestDataManager, Mockito.never()).saveContestData(Mockito.any());

        /// endregion

        /// region 校验写入比赛数据

        Mockito.verify(contestManager, Mockito.never()).updateStartTime(Mockito.any(), Mockito.any());
        Mockito.verify(contestManager, Mockito.never()).updateDuration(Mockito.any(), Mockito.any());
        Mockito.verify(contestManager, Mockito.never()).updateTitle(Mockito.any(), Mockito.any());

        /// endregion

    }

    @Test
    void testUpdateContestWithNotEndSuccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(new Date().getTime() - 1000));
        contest.setDuration(1000);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setProblemList(new ArrayList<>());
        publicContestData.setCoAuthor(new HashSet<>());

        Date startTime = new Date(new Date().getTime() + 20000);
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);
        Mockito.when(problemManager.checkProblemListExist(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        contestService.updateContest(contestContentRequest);

        /// region 校验题目的绑定情况

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, problemDataCP.getContestId());

        /// endregion

        /// region 校验写入的比赛数据

        ArgumentCaptor<PublicContestData> publicContestDataArgumentCaptor = ArgumentCaptor.forClass(PublicContestData.class);
        Mockito.verify(contestDataManager).saveContestData(publicContestDataArgumentCaptor.capture());
        PublicContestData publicContestDataCP = publicContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, publicContestDataCP.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, publicContestDataCP.getProblemList().get(0).getProblemId());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(0).getAcceptCount());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(0).getSubmissionCount());
        Assertions.assertEquals(contestContentRequest.getAnnouncement(), publicContestDataCP.getAnnouncement());
        Assertions.assertEquals(0, publicContestDataCP.getCoAuthor().size());

        /// endregion

        /// region 校验写入比赛数据

        Mockito.verify(contestManager, Mockito.never()).updateStartTime(Mockito.any(), Mockito.any());
        Mockito.verify(contestManager).updateDuration(MOCKED_CONTEST_ID, duration);
        Mockito.verify(contestManager).updateTitle(MOCKED_CONTEST_ID, MOCKED_CONTEST_TITLE);

        /// endregion

    }

    @Test
    void testUpdateContestWithEndSuccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        contest.setStartTime(new Date(0));
        contest.setDuration(1000);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problemData.setContestId(null);
        publicContestData.setId(MOCKED_CONTEST_MONGO_ID);
        publicContestData.setProblemList(new ArrayList<>());
        publicContestData.setCoAuthor(new HashSet<>());

        Date startTime = new Date(new Date().getTime() + 20000);
        Integer duration = MockedValueMaker.mInt();

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.ADMIN);

        ContestContentRequest contestContentRequest = ContestContentRequest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .startTime(startTime)
                .duration(duration)
                .accessType(ContestAccessType.BATCH)
                .password(null)
                .inviteUserSet(null)
                .batchId(MOCKED_BATCH_ID)
                .problemList(Collections.singletonList(MOCKED_PROBLEM_ID))
                .coAuthor(new HashSet<String>() {{
                    add(MOCKED_USER_HANDLE);
                }})
                .freezeTime(0)
                .announcement(MockedValueMaker.mString())
                .penaltyTime(20)
                .build();

        contestService.updateContest(contestContentRequest);

        /// region 校验题目的绑定情况

        Mockito.verify(problemDataManager, Mockito.never()).updateProblemData(Mockito.any());

        /// endregion

        /// region 校验写入的比赛数据

        ArgumentCaptor<PublicContestData> publicContestDataArgumentCaptor = ArgumentCaptor.forClass(PublicContestData.class);
        Mockito.verify(contestDataManager).saveContestData(publicContestDataArgumentCaptor.capture());
        PublicContestData publicContestDataCP = publicContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().size());
        Assertions.assertEquals(contestContentRequest.getAnnouncement(), publicContestDataCP.getAnnouncement());
        Assertions.assertEquals(0, publicContestDataCP.getCoAuthor().size());

        /// endregion

        /// region 校验写入比赛数据

        Mockito.verify(contestManager, Mockito.never()).updateStartTime(Mockito.any(), Mockito.any());
        Mockito.verify(contestManager, Mockito.never()).updateDuration(Mockito.any(), Mockito.any());
        Mockito.verify(contestManager).updateTitle(MOCKED_CONTEST_ID, MOCKED_CONTEST_TITLE);

        /// endregion
    }

    @Test
    void testAddContestProblemWithNoAccess() {
        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.PARTICIPANT);

        ContestAddProblem contestAddProblem = ContestAddProblem.builder()
                .contestId(MOCKED_CONTEST_ID)
                .problemId(MOCKED_PROBLEM_ID)
                .build();
        try {
            contestService.addContestProblem(contestAddProblem);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-014", e.getCode());
        }
    }

    @Test
    void testAddContestProblemWithNoProblem() {
        userContextBuilder.withNormalLoginIn(MockedValueMaker.mLong());

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        problem.setOwner(MOCKED_USER_ID);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.empty());

        ContestAddProblem contestAddProblem = ContestAddProblem.builder()
                .contestId(MOCKED_CONTEST_ID)
                .problemId(MOCKED_PROBLEM_ID)
                .build();
        try {
            contestService.addContestProblem(contestAddProblem);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-017", e.getCode());
        }
    }

    @Test
    void testAddContestProblemWithNotOwnerProblem() {
        userContextBuilder.withNormalLoginIn();

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PRIVATE);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));

        ContestAddProblem contestAddProblem = ContestAddProblem.builder()
                .contestId(MOCKED_CONTEST_ID)
                .problemId(MOCKED_PROBLEM_ID)
                .build();

        try {
            contestService.addContestProblem(contestAddProblem);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-015", e.getCode());
        }
    }

    @Test
    void testAddContestProblemWithNotPrivate() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));

        ContestAddProblem contestAddProblem = ContestAddProblem.builder()
                .contestId(MOCKED_CONTEST_ID)
                .problemId(MOCKED_PROBLEM_ID)
                .build();

        try {
            contestService.addContestProblem(contestAddProblem);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-015", e.getCode());
        }
    }

    @Test
    void testAddContestProblemWithExistProblem() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PRIVATE);
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MOCKED_PROBLEM_ID)
                    .acceptCount(0)
                    .submissionCount(0)
                    .build());
        }});

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));

        ContestAddProblem contestAddProblem = ContestAddProblem.builder()
                .contestId(MOCKED_CONTEST_ID)
                .problemId(MOCKED_PROBLEM_ID)
                .build();

        try {
            contestService.addContestProblem(contestAddProblem);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-08-016", e.getCode());
        }
    }

    @Test
    void testAddContestProblemWithSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        contest.setAccessType(ContestAccessType.PUBLIC);
        contest.setDataId(MOCKED_CONTEST_MONGO_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setDataId(MOCKED_PROBLEM_MONGO_ID);
        problem.setAccessType(ProblemAccessType.PRIVATE);
        problemData.setContestId(null);
        publicContestData.setProblemList(new ArrayList<BaseContestData.ContestProblemData>() {{
            add(BaseContestData.ContestProblemData.builder()
                    .problemId(MockedValueMaker.mLong())
                    .acceptCount(5)
                    .submissionCount(10)
                    .build());
        }});

        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));
        Mockito.when(contestDataManager.getPublicContestDataById(MOCKED_CONTEST_MONGO_ID)).thenReturn(publicContestData);
        contestVisitTypeMockedStatic.when(() -> ContestVisitType.checkPermission(Mockito.any(), Mockito.any())).thenReturn(ContestVisitType.CO_AUTHOR);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ContestAddProblem contestAddProblem = ContestAddProblem.builder()
                .contestId(MOCKED_CONTEST_ID)
                .problemId(MOCKED_PROBLEM_ID)
                .build();

        contestService.addContestProblem(contestAddProblem);

        /// region 校验题目的绑定情况

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, problemDataCP.getContestId());

        /// endregion

        /// region 校验写入的比赛数据

        ArgumentCaptor<PublicContestData> publicContestDataArgumentCaptor = ArgumentCaptor.forClass(PublicContestData.class);
        Mockito.verify(contestDataManager).saveContestData(publicContestDataArgumentCaptor.capture());
        PublicContestData publicContestDataCP = publicContestDataArgumentCaptor.getValue();
        Assertions.assertEquals(2, publicContestDataCP.getProblemList().size());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, publicContestDataCP.getProblemList().get(1).getProblemId());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(1).getAcceptCount());
        Assertions.assertEquals(0, publicContestDataCP.getProblemList().get(1).getSubmissionCount());

        /// endregion
    }
}