package com.portable.server.service.impl;

import com.Ostermiller.util.CircularByteBuffer;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.ContestManagerImpl;
import com.portable.server.manager.impl.ProblemDataManagerImpl;
import com.portable.server.manager.impl.ProblemManagerImpl;
import com.portable.server.manager.impl.SolutionDataManagerImpl;
import com.portable.server.manager.impl.SolutionManagerImpl;
import com.portable.server.manager.impl.UserDataManagerImpl;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.problem.ProblemCodeRequest;
import com.portable.server.model.request.problem.ProblemContentRequest;
import com.portable.server.model.request.problem.ProblemJudgeRequest;
import com.portable.server.model.request.problem.ProblemNameRequest;
import com.portable.server.model.request.problem.ProblemSettingRequest;
import com.portable.server.model.request.problem.ProblemTestRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.response.problem.ProblemStdTestCodeResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.support.impl.FileSupportImpl;
import com.portable.server.support.impl.JudgeSupportImpl;
import com.portable.server.tool.UserContextBuilder;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.LanguageType;
import com.portable.server.type.PermissionType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemListStatusType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.StreamUtils;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceImplTest {

    @InjectMocks
    private ProblemServiceImpl problemService;

    @Mock
    private ProblemManagerImpl problemManager;

    @Mock
    private ProblemDataManagerImpl problemDataManager;

    @Mock
    private UserManagerImpl userManager;

    @Mock
    private UserDataManagerImpl userDataManager;

    @Mock
    private SolutionManagerImpl solutionManager;

    @Mock
    private SolutionDataManagerImpl solutionDataManager;

    @Mock
    private ContestManagerImpl contestManager;

    @Mock
    private FileSupportImpl fileSupport;

    @Mock
    private JudgeSupportImpl judgeSupport;

    private static final Long MOCKED_USER_ID = 1L;
    private static final Long MOCKED_PROBLEM_ID = 2L;
    private static final Long MOCKED_CONTEST_ID = 3L;
    private static final Long MOCKED_SOLUTION_ID = 4L;
    private static final Integer MOCKED_TEST_LEN = 100;
    private static final String MOCKED_PROBLEM_MONGO_ID = "MOCKED_PROBLEM_MONGO_ID";
    private static final String MOCKED_HANDLE = "MOCKED_HANDLE";
    private static final String MOCKED_NAME = "MOCKED_NAME";
    private static final String MOCKED_CODE_TEST = "MOCKED_CODE_TEST";
    private static final String MOCKED_PROBLEM_TITLE = "MOCKED_PROBLEM_TITLE";
    private static final String MOCKED_PROBLEM_DESC = "MOCKED_PROBLEM_DESC";
    private static final String MOCKED_PROBLEM_INPUT = "MOCKED_PROBLEM_INPUT";
    private static final String MOCKED_PROBLEM_OUTPUT = "MOCKED_PROBLEM_OUTPUT";
    private static final String MOCKED_USER_DATA_ID = "MOCKED_USER_DATA_ID";

    private Problem problem;
    private ProblemData problemData;
    private List<Problem> problemList;
    private User user;
    private NormalUserData normalUserData;
    private Contest contest;
    private Solution solution;

    private UserContextBuilder userContextBuilder;

    private MockedStatic<ProblemServiceImpl.UserToProblemAccessType> userToProblemAccessTypeMockedStatic;

    public static class UserToProblemAccessTypeTest {

        private static final Long MOCKED_USER_ID = 1L;
        private static final Long MOCKED_PROBLEM_ID = 2L;

        private static final String MOCKED_PROBLEM_MONGO_ID = "MOCKED_PROBLEM_MONGO_ID";

        private Problem problem;
        private Contest contest;

        private UserContextBuilder userContextBuilder;

        @BeforeEach
        void setUp() {
            problem = Problem.builder().id(MOCKED_PROBLEM_ID).dataId(MOCKED_PROBLEM_MONGO_ID).build();
            contest = Contest.builder().build();

            userContextBuilder = new UserContextBuilder();
            userContextBuilder.setup();
        }

        @AfterEach
        void tearDown() {
            userContextBuilder.tearDown();
        }

        @Test
        void testOfWithNoContestPrivateNotOwner() {
            userContextBuilder.withNotLogin();
            problem.setOwner(MOCKED_USER_ID + 1);
            problem.setAccessType(ProblemAccessType.PRIVATE);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestPrivateOwnerNoPermission() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PRIVATE);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestPrivateOwner() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PRIVATE);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestPrivateEditOther() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PRIVATE);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS, retVal);
        }

        @Test
        void testOfWithContestOwnerPrivateEnd() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
            contest.setOwner(MOCKED_USER_ID + 1);
            contest.setStartTime(new Date(0));
            contest.setDuration(10000);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PRIVATE);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS, retVal);
        }

        @Test
        void testOfWithContestOwnerPrivateNotEnd() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
            contest.setOwner(MOCKED_USER_ID + 1);
            contest.setStartTime(new Date());
            contest.setDuration(10000);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PRIVATE);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestHiddenNotOwner() {
            userContextBuilder.withNotLogin();
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.HIDDEN);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestHiddenOwnerNoPermission() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.HIDDEN);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestHiddenOwnerWithView() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.VIEW_HIDDEN_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.HIDDEN);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.VIEW, retVal);
        }

        @Test
        void testOfWithNoContestHiddenOwnerWithEdit() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.HIDDEN);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestHiddenNotOwnerView() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.VIEW_HIDDEN_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.HIDDEN);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.VIEW, retVal);
        }

        @Test
        void testOfWithNoContestHiddenNotOwnerNotViewEditOther() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.HIDDEN);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestHiddenNotOwnerViewEditOther() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.VIEW_HIDDEN_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.HIDDEN);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS, retVal);
        }

        @Test
        void testOfWithNoContestPublicNotOwner() {
            userContextBuilder.withNotLogin();
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PUBLIC);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.VIEW, retVal);
        }

        @Test
        void testOfWithNoContestPublicOwner() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PUBLIC);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.VIEW, retVal);
        }

        @Test
        void testOfWithNoContestPublicOwnerPermission() {
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
            problem.setOwner(MOCKED_USER_ID);
            problem.setAccessType(ProblemAccessType.PUBLIC);

            ProblemServiceImpl.UserToProblemAccessType retVal = ProblemServiceImpl.UserToProblemAccessType.of(problem, contest);

            Assertions.assertEquals(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS, retVal);
        }

    }

    @BeforeEach
    void setUp() {
        problem = Problem.builder().id(MOCKED_PROBLEM_ID).dataId(MOCKED_PROBLEM_MONGO_ID).build();
        problemData = ProblemData.builder()
                ._id(MOCKED_PROBLEM_MONGO_ID)
                .testName(new ArrayList<String>() {{
                    add("TestName1");
                    add("TestName2");
                    add("TestName3");
                }})
                .build();
        problemList = new ArrayList<Problem>() {{
            add(Problem.builder().id(1L).build());
            add(Problem.builder().id(2L).build());
            add(Problem.builder().id(3L).build());
        }};
        user = User.builder().build();
        normalUserData = NormalUserData.builder().build();
        contest = Contest.builder().build();
        solution = Solution.builder().build();

        userContextBuilder = new UserContextBuilder();
        userContextBuilder.setup();

        userToProblemAccessTypeMockedStatic = Mockito.mockStatic(ProblemServiceImpl.UserToProblemAccessType.class);
    }

    @AfterEach
    void tearDown() {
        userContextBuilder.tearDown();
        userToProblemAccessTypeMockedStatic.close();
    }

    @Test
    void testGetProblemListWithNoLogin() {
        userContextBuilder.withNotLogin();

        List<ProblemAccessType> problemAccessTypeList = Collections.singletonList(ProblemAccessType.PUBLIC);
        Mockito.when(problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, null)).thenReturn(problemList.size());
        Mockito.when(problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, null, 30, 0)).thenReturn(problemList);

        PageRequest<Void> pageRequest = PageRequest.<Void>builder().pageNum(1).pageSize(30).build();
        PageResponse<ProblemListResponse, Void> retVal = problemService.getProblemList(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(3, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getTotalPage());
        Assertions.assertEquals(1, retVal.getPageNum());
        Assertions.assertTrue(IntStream.range(0, 3).allMatch(i -> Objects.equals(i + 1L, retVal.getData().get(i).getId())));
        Assertions.assertTrue(retVal.getData().stream().allMatch(problemListResponse -> ProblemListStatusType.NEVER_SUBMIT.equals(problemListResponse.getProblemListStatusType())));

        /// endregion
    }

    @Test
    void testGetProblemListWithNoPermission() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        Solution solutionAC = Solution.builder().status(SolutionStatusType.ACCEPT).build();
        Solution solutionWA = Solution.builder().status(SolutionStatusType.WRONG_ANSWER).build();

        List<ProblemAccessType> problemAccessTypeList = Collections.singletonList(ProblemAccessType.PUBLIC);
        Mockito.when(problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, MOCKED_USER_ID)).thenReturn(problemList.size());
        Mockito.when(problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, MOCKED_USER_ID, 30, 0)).thenReturn(problemList);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 1L)).thenReturn(Optional.of(solutionAC));
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 2L)).thenReturn(Optional.of(solutionWA));
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 3L)).thenReturn(Optional.empty());

        PageRequest<Void> pageRequest = PageRequest.<Void>builder().pageNum(1).pageSize(30).build();
        PageResponse<ProblemListResponse, Void> retVal = problemService.getProblemList(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(3, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getTotalPage());
        Assertions.assertEquals(1, retVal.getPageNum());
        Assertions.assertTrue(IntStream.range(0, 3).allMatch(i -> {
            if (!Objects.equals(i + 1L, retVal.getData().get(i).getId())) {
                return false;
            }
            if (i == 0) {
                return ProblemListStatusType.PASS.equals(retVal.getData().get(i).getProblemListStatusType());
            } else if (i == 1) {
                return ProblemListStatusType.NOT_PASS.equals(retVal.getData().get(i).getProblemListStatusType());
            } else if (i == 2) {
                return ProblemListStatusType.NEVER_SUBMIT.equals(retVal.getData().get(i).getProblemListStatusType());
            } else {
                return ProblemListStatusType.ON_JUDGE.equals(retVal.getData().get(i).getProblemListStatusType());
            }
        }));

        /// endregion
    }

    @Test
    void testGetProblemListWithPermission() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.VIEW_HIDDEN_PROBLEM);

        Solution solutionAC = Solution.builder().status(SolutionStatusType.ACCEPT).build();
        Solution solutionWA = Solution.builder().status(SolutionStatusType.WRONG_ANSWER).build();

        List<ProblemAccessType> problemAccessTypeList = Arrays.asList(ProblemAccessType.PUBLIC, ProblemAccessType.HIDDEN);
        Mockito.when(problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, MOCKED_USER_ID)).thenReturn(problemList.size());
        Mockito.when(problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, MOCKED_USER_ID, 30, 0)).thenReturn(problemList);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 1L)).thenReturn(Optional.of(solutionAC));
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 2L)).thenReturn(Optional.of(solutionWA));
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 3L)).thenReturn(Optional.empty());

        PageRequest<Void> pageRequest = PageRequest.<Void>builder().pageNum(1).pageSize(30).build();
        PageResponse<ProblemListResponse, Void> retVal = problemService.getProblemList(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(3, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getTotalPage());
        Assertions.assertEquals(1, retVal.getPageNum());
        Assertions.assertTrue(IntStream.range(0, 3).allMatch(i -> {
            if (!Objects.equals(i + 1L, retVal.getData().get(i).getId())) {
                return false;
            }
            if (i == 0) {
                return ProblemListStatusType.PASS.equals(retVal.getData().get(i).getProblemListStatusType());
            } else if (i == 1) {
                return ProblemListStatusType.NOT_PASS.equals(retVal.getData().get(i).getProblemListStatusType());
            } else if (i == 2) {
                return ProblemListStatusType.NEVER_SUBMIT.equals(retVal.getData().get(i).getProblemListStatusType());
            } else {
                return ProblemListStatusType.ON_JUDGE.equals(retVal.getData().get(i).getProblemListStatusType());
            }
        }));

        /// endregion
    }

    @Test
    void testSearchProblemSetListWithNoPermission() {
        userContextBuilder.withNormalLoginIn();

        Integer MOCKED_SEARCH_PAGE_SIZE = 10;
        String MOCKED_SEARCH_KEYWORD = "MOCKED_SEARCH_KEYWORD";

        List<ProblemAccessType> problemAccessTypeList = Collections.singletonList(ProblemAccessType.PUBLIC);
        ReflectionTestUtils.setField(problemService, "searchPageSize", MOCKED_SEARCH_PAGE_SIZE);

        Mockito.when(problemManager.searchRecentProblemByTypedAndKeyword(problemAccessTypeList, MOCKED_SEARCH_KEYWORD, MOCKED_SEARCH_PAGE_SIZE)).thenReturn(problemList);

        List<ProblemListResponse> retVal = problemService.searchProblemSetList(MOCKED_SEARCH_KEYWORD);


        /// region 校验返回值

        Assertions.assertTrue(IntStream.range(0, 3).allMatch(i -> Objects.equals(i + 1L, retVal.get(i).getId())));

        /// endregion
    }

    @Test
    void testSearchProblemSetListWithPermission() {
        userContextBuilder.withNormalLoginIn().withPermission(PermissionType.VIEW_HIDDEN_PROBLEM);

        Integer MOCKED_SEARCH_PAGE_SIZE = 10;
        String MOCKED_SEARCH_KEYWORD = "MOCKED_SEARCH_KEYWORD";

        List<ProblemAccessType> problemAccessTypeList = Arrays.asList(ProblemAccessType.PUBLIC, ProblemAccessType.HIDDEN);
        ReflectionTestUtils.setField(problemService, "searchPageSize", MOCKED_SEARCH_PAGE_SIZE);
        Mockito.when(problemManager.searchRecentProblemByTypedAndKeyword(problemAccessTypeList, MOCKED_SEARCH_KEYWORD, MOCKED_SEARCH_PAGE_SIZE)).thenReturn(problemList);

        List<ProblemListResponse> retVal = problemService.searchProblemSetList(MOCKED_SEARCH_KEYWORD);


        /// region 校验返回值

        Assertions.assertTrue(IntStream.range(0, 3).allMatch(i -> Objects.equals(i + 1L, retVal.get(i).getId())));

        /// endregion
    }

    @Test
    void testSearchPrivateProblemList() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
        Integer MOCKED_SEARCH_PAGE_SIZE = 10;
        String MOCKED_SEARCH_KEYWORD = "MOCKED_SEARCH_KEYWORD";

        ReflectionTestUtils.setField(problemService, "searchPageSize", MOCKED_SEARCH_PAGE_SIZE);

        Mockito.when(problemManager.searchRecentProblemByOwnerIdAndKeyword(MOCKED_USER_ID, MOCKED_SEARCH_KEYWORD, MOCKED_SEARCH_PAGE_SIZE)).thenReturn(problemList);

        List<ProblemListResponse> retVal = problemService.searchPrivateProblemList(MOCKED_SEARCH_KEYWORD);


        /// region 校验返回值

        Assertions.assertTrue(IntStream.range(0, 3).allMatch(i -> Objects.equals(i + 1L, retVal.get(i).getId())));

        /// endregion
    }

    @Test
    void testGetProblemWithNoProblem() {
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.empty());

        try {
            problemService.getProblem(MOCKED_PROBLEM_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-001", e.getCode());
        }
    }

    @Test
    void testGetProblemWithNoProblemData() throws PortableException {
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenThrow(PortableException.of("S-03-001"));

        try {
            problemService.getProblem(MOCKED_PROBLEM_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-03-001", e.getCode());
        }
    }

    @Test
    void testGetProblemWithNoAccess() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS);

        problemData.setContestId(null);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        try {
            problemService.getProblem(MOCKED_PROBLEM_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-004", e.getCode());
        }
    }

    @Test
    void testGetProblemWithView() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.VIEW);

        problem.setOwner(MOCKED_USER_ID);
        problemData.setContestId(null);
        user.setId(MOCKED_USER_ID);
        user.setHandle(MOCKED_HANDLE);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));

        ProblemDetailResponse retVal = problemService.getProblem(MOCKED_PROBLEM_ID);

        /// region 校验返回值

        Assertions.assertEquals(problem.getId(), retVal.getId());
        Assertions.assertEquals(user.getHandle(), retVal.getOwnerHandle());

        /// endregion
    }

    @Test
    void testGetProblemWithFullAccess() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problem.setOwner(MOCKED_USER_ID);
        problemData.setContestId(null);
        user.setId(MOCKED_USER_ID);
        user.setHandle(MOCKED_HANDLE);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));

        ProblemDetailResponse retVal = problemService.getProblem(MOCKED_PROBLEM_ID);

        /// region 校验返回值

        Assertions.assertEquals(problem.getId(), retVal.getId());
        Assertions.assertEquals(user.getHandle(), retVal.getOwnerHandle());

        /// endregion
    }

    @Test
    void testGetProblemTestListNoAccess() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS);

        problemData.setContestId(null);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        try {
            problemService.getProblemTestList(MOCKED_PROBLEM_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-006", e.getCode());
        }
    }

    @Test
    void testGetProblemTestListViewNoShare() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.VIEW);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        try {
            problemService.getProblemTestList(MOCKED_PROBLEM_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-006", e.getCode());
        }
    }

    @Test
    void testGetProblemTestListViewShare() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.VIEW);

        problemData.setContestId(null);
        problemData.setShareTest(true);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        List<String> testList = problemService.getProblemTestList(MOCKED_PROBLEM_ID);

        Assertions.assertEquals(problemData.getTestName(), testList);
    }

    @Test
    void testGetProblemTestListFullAccessNoShare() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        List<String> testList = problemService.getProblemTestList(MOCKED_PROBLEM_ID);

        Assertions.assertEquals(problemData.getTestName(), testList);
    }

    @Test
    void testShowTestInputWithNoTest() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();
        try {
            problemService.showTestInput(problemNameRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-006", e.getCode());
        }
    }

    @Test
    void testShowTestInputWithTest() throws PortableException, IOException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        circularByteBuffer.getOutputStream().write(MOCKED_CODE_TEST.getBytes());
        circularByteBuffer.getOutputStream().close();

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(fileSupport.getTestInput(MOCKED_PROBLEM_ID, problemData.getTestName().get(0))).thenReturn(circularByteBuffer.getInputStream());

        ReflectionTestUtils.setField(problemService, "maxTestShowLen", MOCKED_TEST_LEN);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(problemData.getTestName().get(0))
                .build();
        String retVal = problemService.showTestInput(problemNameRequest);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_CODE_TEST, retVal);

        /// endregion

    }

    @Test
    void testShowTestOutputWithNoTest() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();
        try {
            problemService.showTestOutput(problemNameRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-006", e.getCode());
        }

    }

    @Test
    void testShowTestOutputWithTest() throws PortableException, IOException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        circularByteBuffer.getOutputStream().write(MOCKED_CODE_TEST.getBytes());
        circularByteBuffer.getOutputStream().close();

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(fileSupport.getTestOutput(MOCKED_PROBLEM_ID, problemData.getTestName().get(0))).thenReturn(circularByteBuffer.getInputStream());

        ReflectionTestUtils.setField(problemService, "maxTestShowLen", MOCKED_TEST_LEN);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(problemData.getTestName().get(0))
                .build();
        String retVal = problemService.showTestOutput(problemNameRequest);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_CODE_TEST, retVal);

        /// endregion

    }

    @Test
    void testDownloadTestInputWithNoTest() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();
        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();

        try {
            problemService.downloadTestInput(problemNameRequest, circularByteBuffer.getOutputStream());
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-006", e.getCode());
        }
    }

    @Test
    void testDownloadTestInputWithTest() throws PortableException, IOException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        circularByteBuffer.getOutputStream().write(MOCKED_CODE_TEST.getBytes());
        circularByteBuffer.getOutputStream().close();

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(fileSupport.getTestInput(MOCKED_PROBLEM_ID, problemData.getTestName().get(0))).thenReturn(circularByteBuffer.getInputStream());

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(problemData.getTestName().get(0))
                .build();
        CircularByteBuffer circularByteBufferOutput = new CircularByteBuffer();
        problemService.downloadTestInput(problemNameRequest, circularByteBufferOutput.getOutputStream());

        /// region 校验返回值

        String retVal = StreamUtils.read(circularByteBufferOutput.getInputStream());
        Assertions.assertEquals(MOCKED_CODE_TEST, retVal);

        /// endregion

    }

    @Test
    void testDownloadTestOutputWithNoTest() throws PortableException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();
        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();

        try {
            problemService.downloadTestOutput(problemNameRequest, circularByteBuffer.getOutputStream());
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-006", e.getCode());
        }
    }

    @Test
    void testDownloadTestOutputWithTest() throws PortableException, IOException {
        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        circularByteBuffer.getOutputStream().write(MOCKED_CODE_TEST.getBytes());
        circularByteBuffer.getOutputStream().close();

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(fileSupport.getTestOutput(MOCKED_PROBLEM_ID, problemData.getTestName().get(0))).thenReturn(circularByteBuffer.getInputStream());

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(problemData.getTestName().get(0))
                .build();
        CircularByteBuffer circularByteBufferOutput = new CircularByteBuffer();
        problemService.downloadTestOutput(problemNameRequest, circularByteBufferOutput.getOutputStream());

        /// region 校验返回值

        String retVal = StreamUtils.read(circularByteBufferOutput.getInputStream());
        Assertions.assertEquals(MOCKED_CODE_TEST, retVal);

        /// endregion

    }

    @Test
    void testNewProblemWithSuccess() throws PortableException {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);

        Mockito.when(problemManager.newProblem()).thenCallRealMethod();
        Mockito.when(problemDataManager.newProblemData()).thenCallRealMethod();

        Mockito.doAnswer(invocationOnMock -> {
            ProblemData problemData = invocationOnMock.getArgument(0);
            problemData.set_id(MOCKED_PROBLEM_MONGO_ID);
            return null;
        }).when(problemDataManager).insertProblemData(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> {
            Problem problem = invocationOnMock.getArgument(0);
            problem.setId(MOCKED_PROBLEM_ID);
            return null;
        }).when(problemManager).insertProblem(Mockito.any());

        ProblemContentRequest problemContentRequest = ProblemContentRequest.builder()
                .id(null)
                .title(MOCKED_PROBLEM_TITLE)
                .description(MOCKED_PROBLEM_DESC)
                .input(MOCKED_PROBLEM_INPUT)
                .output(MOCKED_PROBLEM_OUTPUT)
                .example(new ArrayList<>())
                .build();

        Problem problem = problemService.newProblem(problemContentRequest);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_PROBLEM_ID, problem.getId());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, problem.getTitle());

        /// endregion

        /// region 校验写入的问题数据信息

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).insertProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_PROBLEM_DESC, problemDataCP.getDescription());
        Assertions.assertEquals(MOCKED_PROBLEM_INPUT, problemDataCP.getInput());
        Assertions.assertEquals(MOCKED_PROBLEM_OUTPUT, problemDataCP.getOutput());

        /// endregion

        /// region 校验写入的问题信息

        ArgumentCaptor<Problem> problemArgumentCaptor = ArgumentCaptor.forClass(Problem.class);
        Mockito.verify(problemManager).insertProblem(problemArgumentCaptor.capture());
        Problem problemCP = problemArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_PROBLEM_MONGO_ID, problemCP.getDataId());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, problemCP.getTitle());

        /// endregion
    }

    @Test
    void testUpdateProblemContentWithNoAccess() throws PortableException {
        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.NO_ACCESS);

        ProblemContentRequest problemContentRequest = ProblemContentRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .title(MOCKED_PROBLEM_TITLE)
                .description(MOCKED_PROBLEM_DESC)
                .input(MOCKED_PROBLEM_INPUT)
                .output(MOCKED_PROBLEM_OUTPUT)
                .example(new ArrayList<>())
                .build();

        try {
            problemService.updateProblemContent(problemContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-005", e.getCode());
        }
    }

    @Test
    void testUpdateProblemContentWithView() throws PortableException {
        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.VIEW);

        ProblemContentRequest problemContentRequest = ProblemContentRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .title(MOCKED_PROBLEM_TITLE)
                .description(MOCKED_PROBLEM_DESC)
                .input(MOCKED_PROBLEM_INPUT)
                .output(MOCKED_PROBLEM_OUTPUT)
                .example(new ArrayList<>())
                .build();

        try {
            problemService.updateProblemContent(problemContentRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-005", e.getCode());
        }
    }

    @Test
    void testUpdateProblemContentWithFullAccess() throws PortableException {
        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemContentRequest problemContentRequest = ProblemContentRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .title(MOCKED_PROBLEM_TITLE)
                .description(MOCKED_PROBLEM_DESC)
                .input(MOCKED_PROBLEM_INPUT)
                .output(MOCKED_PROBLEM_OUTPUT)
                .example(new ArrayList<>())
                .build();

        problemService.updateProblemContent(problemContentRequest);

        /// region 校验写入 MySQL 的数据

        Mockito.verify(problemManager).updateProblemTitle(MOCKED_PROBLEM_ID, MOCKED_PROBLEM_TITLE);

        /// endregion

        /// region 校验写入 Mongo 的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_PROBLEM_DESC, problemDataCP.getDescription());
        Assertions.assertEquals(MOCKED_PROBLEM_INPUT, problemDataCP.getInput());
        Assertions.assertEquals(MOCKED_PROBLEM_OUTPUT, problemDataCP.getOutput());

        /// endregion

    }

    @Test
    void testUpdateProblemSettingWithTreated() throws PortableException {
        problem.setStatusType(ProblemStatusType.TREATING);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .build();

        try {
            problemService.updateProblemSetting(problemSettingRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-007", e.getCode());
        }
    }

    @Test
    void testUpdateProblemSettingWithPublicToPrivate() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);

        problemData.setContestId(null);
        problemData.setShareTest(false);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .accessType(ProblemAccessType.PRIVATE)
                .build();

        try {
            problemService.updateProblemSetting(problemSettingRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-015", e.getCode());
        }
    }

    @Test
    void testUpdateProblemSettingWithContestTime() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(MOCKED_CONTEST_ID);
        problemData.setShareTest(false);
        contest.setStartTime(new Date());
        contest.setDuration(100);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .accessType(ProblemAccessType.HIDDEN)
                .build();

        try {
            problemService.updateProblemSetting(problemSettingRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-014", e.getCode());
        }
    }

    @Test
    void testUpdateProblemSettingWithNoChecked() throws PortableException {
        problem.setStatusType(ProblemStatusType.UNCHECK);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setStdCode(ProblemData.StdCode.builder().build());
        problemData.setTestName(new ArrayList<>());

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        List<LanguageType> supportLanguage = new ArrayList<LanguageType>() {{
            add(LanguageType.CPP17);
        }};
        Map<LanguageType, Integer> speTimeLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP17, 10);
        }};
        Map<LanguageType, Integer> speMemoryLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP11, 5);
        }};
        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .accessType(ProblemAccessType.HIDDEN)
                .supportLanguage(supportLanguage)
                .defaultTimeLimit(10)
                .defaultMemoryLimit(5)
                .specialTimeLimit(speTimeLimit)
                .specialMemoryLimit(speMemoryLimit)
                .build();

        problemService.updateProblemSetting(problemSettingRequest);

        Mockito.verify(solutionManager, Mockito.never()).selectSolutionById(Mockito.any());
        Mockito.verify(problemManager).updateProblemAccessStatus(MOCKED_PROBLEM_ID, ProblemAccessType.HIDDEN);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(supportLanguage, problemDataCP.getSupportLanguage());
        Assertions.assertEquals(10, problemDataCP.getDefaultTimeLimit());
        Assertions.assertEquals(speTimeLimit, problemDataCP.getSpecialTimeLimit());
        Assertions.assertEquals(speMemoryLimit, problemDataCP.getSpecialMemoryLimit());

        /// endregion
    }

    @Test
    void testUpdateProblemSettingWithCheckedTimeoutCheck() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setTestCodeList(new ArrayList<>());
        problemData.setStdCode(ProblemData.StdCode.builder()
                .languageType(LanguageType.CPP17)
                .solutionId(MOCKED_SOLUTION_ID)
                .expectResultType(SolutionStatusType.ACCEPT)
                .build());
        problemData.setTestName(new ArrayList<>());
        solution.setTimeCost(15);
        solution.setStatus(SolutionStatusType.ACCEPT);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        List<LanguageType> supportLanguage = new ArrayList<LanguageType>() {{
            add(LanguageType.CPP17);
        }};
        Map<LanguageType, Integer> speTimeLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP17, 10);
        }};
        Map<LanguageType, Integer> speMemoryLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP11, 5);
        }};
        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .accessType(ProblemAccessType.HIDDEN)
                .supportLanguage(supportLanguage)
                .defaultTimeLimit(10)
                .defaultMemoryLimit(5)
                .specialTimeLimit(speTimeLimit)
                .specialMemoryLimit(speMemoryLimit)
                .build();

        problemService.updateProblemSetting(problemSettingRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNCHECK);
        Mockito.verify(problemManager).updateProblemAccessStatus(MOCKED_PROBLEM_ID, ProblemAccessType.HIDDEN);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(supportLanguage, problemDataCP.getSupportLanguage());
        Assertions.assertEquals(10, problemDataCP.getDefaultTimeLimit());
        Assertions.assertEquals(speTimeLimit, problemDataCP.getSpecialTimeLimit());
        Assertions.assertEquals(speMemoryLimit, problemDataCP.getSpecialMemoryLimit());

        /// endregion
    }

    @Test
    void testUpdateProblemSettingWithCheckedMemoryOutCheck() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setTestCodeList(new ArrayList<>());
        problemData.setStdCode(ProblemData.StdCode.builder()
                .languageType(LanguageType.CPP17)
                .solutionId(MOCKED_SOLUTION_ID)
                .expectResultType(SolutionStatusType.TIME_LIMIT_EXCEEDED)
                .build());
        problemData.setTestName(new ArrayList<>());
        solution.setTimeCost(15);
        solution.setMemoryCost(10);
        solution.setStatus(SolutionStatusType.TIME_LIMIT_EXCEEDED);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        List<LanguageType> supportLanguage = new ArrayList<LanguageType>() {{
            add(LanguageType.CPP17);
        }};
        Map<LanguageType, Integer> speTimeLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP17, 10);
        }};
        Map<LanguageType, Integer> speMemoryLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP11, 5);
        }};
        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .accessType(ProblemAccessType.HIDDEN)
                .supportLanguage(supportLanguage)
                .defaultTimeLimit(10)
                .defaultMemoryLimit(5)
                .specialTimeLimit(speTimeLimit)
                .specialMemoryLimit(speMemoryLimit)
                .build();

        problemService.updateProblemSetting(problemSettingRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNCHECK);
        Mockito.verify(problemManager).updateProblemAccessStatus(MOCKED_PROBLEM_ID, ProblemAccessType.HIDDEN);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(supportLanguage, problemDataCP.getSupportLanguage());
        Assertions.assertEquals(10, problemDataCP.getDefaultTimeLimit());
        Assertions.assertEquals(speTimeLimit, problemDataCP.getSpecialTimeLimit());
        Assertions.assertEquals(speMemoryLimit, problemDataCP.getSpecialMemoryLimit());

        /// endregion
    }

    @Test
    void testUpdateProblemSettingWithTestCheckedMemoryOutCheck() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setTestCodeList(new ArrayList<ProblemData.StdCode>() {{
            add(ProblemData.StdCode.builder()
                    .languageType(LanguageType.CPP17)
                    .solutionId(MOCKED_SOLUTION_ID)
                    .expectResultType(SolutionStatusType.TIME_LIMIT_EXCEEDED)
                    .build());
        }});
        problemData.setStdCode(ProblemData.StdCode.builder()
                .languageType(LanguageType.CPP17)
                .solutionId(null)
                .build());
        problemData.setTestName(new ArrayList<>());
        solution.setTimeCost(15);
        solution.setMemoryCost(10);
        solution.setStatus(SolutionStatusType.TIME_LIMIT_EXCEEDED);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        List<LanguageType> supportLanguage = new ArrayList<LanguageType>() {{
            add(LanguageType.CPP17);
        }};
        Map<LanguageType, Integer> speTimeLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP17, 10);
        }};
        Map<LanguageType, Integer> speMemoryLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP11, 5);
        }};
        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .accessType(ProblemAccessType.HIDDEN)
                .supportLanguage(supportLanguage)
                .defaultTimeLimit(10)
                .defaultMemoryLimit(5)
                .specialTimeLimit(speTimeLimit)
                .specialMemoryLimit(speMemoryLimit)
                .build();

        problemService.updateProblemSetting(problemSettingRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNCHECK);
        Mockito.verify(problemManager).updateProblemAccessStatus(MOCKED_PROBLEM_ID, ProblemAccessType.HIDDEN);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(supportLanguage, problemDataCP.getSupportLanguage());
        Assertions.assertEquals(10, problemDataCP.getDefaultTimeLimit());
        Assertions.assertEquals(speTimeLimit, problemDataCP.getSpecialTimeLimit());
        Assertions.assertEquals(speMemoryLimit, problemDataCP.getSpecialMemoryLimit());

        /// endregion
    }

    @Test
    void testUpdateProblemSettingWithNoCheck() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setTestCodeList(new ArrayList<ProblemData.StdCode>() {{
            add(ProblemData.StdCode.builder()
                    .languageType(LanguageType.CPP17)
                    .solutionId(MOCKED_SOLUTION_ID)
                    .expectResultType(SolutionStatusType.TIME_LIMIT_EXCEEDED)
                    .build());
        }});
        problemData.setStdCode(ProblemData.StdCode.builder()
                .languageType(LanguageType.CPP17)
                .solutionId(null)
                .build());
        problemData.setTestName(new ArrayList<>());
        solution.setTimeCost(15);
        solution.setMemoryCost(1);
        solution.setStatus(SolutionStatusType.TIME_LIMIT_EXCEEDED);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        List<LanguageType> supportLanguage = new ArrayList<LanguageType>() {{
            add(LanguageType.CPP17);
        }};
        Map<LanguageType, Integer> speTimeLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP17, 10);
        }};
        Map<LanguageType, Integer> speMemoryLimit = new HashMap<LanguageType, Integer>() {{
            put(LanguageType.CPP11, 5);
        }};
        ProblemSettingRequest problemSettingRequest = ProblemSettingRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .accessType(ProblemAccessType.HIDDEN)
                .supportLanguage(supportLanguage)
                .defaultTimeLimit(10)
                .defaultMemoryLimit(5)
                .specialTimeLimit(speTimeLimit)
                .specialMemoryLimit(speMemoryLimit)
                .build();

        problemService.updateProblemSetting(problemSettingRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.NORMAL);
        Mockito.verify(problemManager).updateProblemAccessStatus(MOCKED_PROBLEM_ID, ProblemAccessType.HIDDEN);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(supportLanguage, problemDataCP.getSupportLanguage());
        Assertions.assertEquals(10, problemDataCP.getDefaultTimeLimit());
        Assertions.assertEquals(speTimeLimit, problemDataCP.getSpecialTimeLimit());
        Assertions.assertEquals(speMemoryLimit, problemDataCP.getSpecialMemoryLimit());

        /// endregion
    }

    @Test
    void testUpdateProblemJudgeWithOnTreate() throws PortableException {
        problem.setStatusType(ProblemStatusType.TREATING);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemJudgeRequest problemJudgeRequest = ProblemJudgeRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .judgeCodeType(JudgeCodeType.ALL_SAME)
                .judgeCode("abc")
                .build();

        try {
            problemService.updateProblemJudge(problemJudgeRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-007", e.getCode());
        }
    }

    @Test
    void testUpdateProblemJudge() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setVersion(0);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemJudgeRequest problemJudgeRequest = ProblemJudgeRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .judgeCodeType(JudgeCodeType.ALL_SAME)
                .judgeCode("abc")
                .build();

        problemService.updateProblemJudge(problemJudgeRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNCHECK);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(JudgeCodeType.ALL_SAME, problemDataCP.getJudgeCodeType());
        Assertions.assertEquals("", problemDataCP.getJudgeCode());
        Assertions.assertEquals(1, problemDataCP.getVersion());

        /// endregion
    }

    @Test
    void testAddProblemTest() throws PortableException, IOException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setVersion(0);
        problemData.setTestName(new ArrayList<>());
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        circularByteBuffer.getOutputStream().write(MOCKED_PROBLEM_DESC.getBytes());
        circularByteBuffer.getOutputStream().close();

        ProblemTestRequest problemTestRequest = ProblemTestRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .inputStream(circularByteBuffer.getInputStream())
                .name(MOCKED_NAME)
                .build();

        problemService.addProblemTest(problemTestRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNTREATED);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(Collections.singletonList(MOCKED_NAME), problemDataCP.getTestName());

        /// endregion

        /// region 校验写入文件的数据

        ArgumentCaptor<InputStream> inputStreamArgumentCaptor = ArgumentCaptor.forClass(InputStream.class);
        Mockito.verify(fileSupport).saveTestInput(Mockito.eq(MOCKED_PROBLEM_ID), Mockito.eq(MOCKED_NAME), inputStreamArgumentCaptor.capture());
        InputStream inputStreamCP = inputStreamArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_PROBLEM_DESC, StreamUtils.read(inputStreamCP));

        /// endregion
    }

    @Test
    void testRemoveProblemTest() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setShareTest(false);
        problemData.setVersion(0);
        problemData.setTestName(new ArrayList<String>() {{
            add(MOCKED_NAME);
        }});
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();

        problemService.removeProblemTest(problemNameRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNCHECK);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(new ArrayList<>(), problemDataCP.getTestName());

        /// endregion

        /// region 校验写入文件的数据

        Mockito.verify(fileSupport).removeTest(MOCKED_PROBLEM_ID, MOCKED_NAME);

        /// endregion
    }

    @Test
    void testGetProblemStdTestCode() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code(MOCKED_CODE_TEST)
                .name(MOCKED_NAME)
                .expectResultType(SolutionStatusType.ACCEPT)
                .languageType(LanguageType.CPP17)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestCodeList(new ArrayList<ProblemData.StdCode>() {{
            add(ProblemData.StdCode.builder()
                    .code(MOCKED_CODE_TEST)
                    .name(MOCKED_NAME)
                    .expectResultType(SolutionStatusType.ACCEPT)
                    .languageType(LanguageType.CPP17)
                    .solutionId(MOCKED_SOLUTION_ID)
                    .build());
        }});
        problemData.setVersion(0);
        solution.setStatus(SolutionStatusType.WRONG_ANSWER);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemStdTestCodeResponse retVal = problemService.getProblemStdTestCode(MOCKED_PROBLEM_ID);

        /// region 校验返回值

        Assertions.assertEquals(SolutionStatusType.ACCEPT, retVal.getStdCode().getExpectResultType());
        Assertions.assertEquals(SolutionStatusType.ACCEPT, retVal.getTestCodeList().get(0).getExpectResultType());
        Assertions.assertEquals(SolutionStatusType.WRONG_ANSWER, retVal.getTestCodeList().get(0).getSolutionStatusType());

        /// endregion
    }

    @Test
    void testUpdateProblemStdCode() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code("")
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setVersion(0);
        solution.setStatus(SolutionStatusType.WRONG_ANSWER);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemCodeRequest problemCodeRequest = ProblemCodeRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .code(MOCKED_CODE_TEST)
                .languageType(LanguageType.CPP17)
                .codeName(MOCKED_NAME)
                .resultType(SolutionStatusType.WRONG_ANSWER)
                .build();

        problemService.updateProblemStdCode(problemCodeRequest);

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNTREATED);

        /// region 校验写入的数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CODE_TEST, problemDataCP.getStdCode().getCode());
        Assertions.assertNull(problemDataCP.getStdCode().getSolutionId());
        Assertions.assertEquals(SolutionStatusType.ACCEPT, problemDataCP.getStdCode().getExpectResultType());
        Assertions.assertEquals(LanguageType.CPP17, problemDataCP.getStdCode().getLanguageType());

        /// endregion
    }

    @Test
    void testAddProblemTestCodeWithExist() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code("")
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestCodeList(new ArrayList<ProblemData.StdCode>() {{
            add(ProblemData.StdCode.builder()
                    .code("")
                    .name(MOCKED_NAME)
                    .expectResultType(null)
                    .languageType(LanguageType.CPP11)
                    .solutionId(MOCKED_SOLUTION_ID)
                    .build());
        }});
        problemData.setVersion(0);
        solution.setStatus(SolutionStatusType.WRONG_ANSWER);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemCodeRequest problemCodeRequest = ProblemCodeRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .code(MOCKED_CODE_TEST)
                .languageType(LanguageType.CPP17)
                .codeName(MOCKED_NAME)
                .resultType(SolutionStatusType.WRONG_ANSWER)
                .build();

        problemService.addProblemTestCode(problemCodeRequest);

        /// region 校验写入的状态

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNCHECK);

        /// endregion

        /// region 校验写入的题目数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, problemDataCP.getTestCodeList().size());
        Assertions.assertEquals(MOCKED_NAME, problemDataCP.getTestCodeList().get(0).getName());
        Assertions.assertEquals(MOCKED_CODE_TEST, problemDataCP.getTestCodeList().get(0).getCode());
        Assertions.assertNull(problemDataCP.getTestCodeList().get(0).getSolutionId());

        /// endregion
    }

    @Test
    void testAddProblemTestCodeWithNotExist() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code("")
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestCodeList(new ArrayList<>());
        problemData.setVersion(0);
        solution.setStatus(SolutionStatusType.WRONG_ANSWER);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemCodeRequest problemCodeRequest = ProblemCodeRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .code(MOCKED_CODE_TEST)
                .languageType(LanguageType.CPP17)
                .codeName(MOCKED_NAME)
                .resultType(SolutionStatusType.WRONG_ANSWER)
                .build();

        problemService.addProblemTestCode(problemCodeRequest);

        /// region 校验写入的状态

        Mockito.verify(problemManager).updateProblemStatus(MOCKED_PROBLEM_ID, ProblemStatusType.UNCHECK);

        /// endregion

        /// region 校验写入的题目数据

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, problemDataCP.getTestCodeList().size());
        Assertions.assertEquals(MOCKED_NAME, problemDataCP.getTestCodeList().get(0).getName());
        Assertions.assertEquals(MOCKED_CODE_TEST, problemDataCP.getTestCodeList().get(0).getCode());
        Assertions.assertNull(problemDataCP.getTestCodeList().get(0).getSolutionId());

        /// endregion
    }

    @Test
    void testRemoveProblemTestCode() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code("")
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestCodeList(new ArrayList<ProblemData.StdCode>() {{
            add(ProblemData.StdCode.builder()
                    .code("")
                    .name(MOCKED_NAME)
                    .expectResultType(null)
                    .languageType(LanguageType.CPP11)
                    .solutionId(MOCKED_SOLUTION_ID)
                    .build());
        }});
        problemData.setVersion(0);
        solution.setStatus(SolutionStatusType.WRONG_ANSWER);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();

        problemService.removeProblemTestCode(problemNameRequest);

        /// region 郊野写入的值

        ArgumentCaptor<ProblemData> problemDataArgumentCaptor = ArgumentCaptor.forClass(ProblemData.class);
        Mockito.verify(problemDataManager).updateProblemData(problemDataArgumentCaptor.capture());
        ProblemData problemDataCP = problemDataArgumentCaptor.getValue();
        Assertions.assertEquals(0, problemDataCP.getTestCodeList().size());

        /// endregion
    }

    @Test
    void testShowStdCode() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code(MOCKED_CODE_TEST)
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        String retVal = problemService.showStdCode(MOCKED_PROBLEM_ID);

        Assertions.assertEquals(MOCKED_CODE_TEST, retVal);
    }

    @Test
    void testShowTestCode() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code("")
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestCodeList(new ArrayList<ProblemData.StdCode>() {{
            add(ProblemData.StdCode.builder()
                    .code(MOCKED_CODE_TEST)
                    .name(MOCKED_NAME)
                    .expectResultType(null)
                    .languageType(LanguageType.CPP11)
                    .solutionId(MOCKED_SOLUTION_ID)
                    .build());
        }});

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();

        String retVal = problemService.showTestCode(problemNameRequest);

        Assertions.assertEquals(MOCKED_CODE_TEST, retVal);
    }

    @Test
    void testDownloadStdCode() throws PortableException, IOException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code(MOCKED_CODE_TEST)
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();


        problemService.downloadStdCode(MOCKED_PROBLEM_ID, circularByteBuffer.getOutputStream());
        circularByteBuffer.getOutputStream().close();

        Assertions.assertEquals(MOCKED_CODE_TEST, StreamUtils.read(circularByteBuffer.getInputStream()));
    }

    @Test
    void testDownloadTestCode() throws PortableException, IOException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setContestId(null);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code("")
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestCodeList(new ArrayList<ProblemData.StdCode>() {{
            add(ProblemData.StdCode.builder()
                    .code(MOCKED_CODE_TEST)
                    .name(MOCKED_NAME)
                    .expectResultType(null)
                    .languageType(LanguageType.CPP11)
                    .solutionId(MOCKED_SOLUTION_ID)
                    .build());
        }});

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        ProblemNameRequest problemNameRequest = ProblemNameRequest.builder()
                .id(MOCKED_PROBLEM_ID)
                .name(MOCKED_NAME)
                .build();

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();

        problemService.downloadTestCode(problemNameRequest, circularByteBuffer.getOutputStream());
        circularByteBuffer.getOutputStream().close();

        Assertions.assertEquals(MOCKED_CODE_TEST, StreamUtils.read(circularByteBuffer.getInputStream()));
    }

    @Test
    void testTreatAndCheckProblemWithNormal() throws PortableException {
        problem.setStatusType(ProblemStatusType.NORMAL);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemService.treatAndCheckProblem(MOCKED_PROBLEM_ID);

        Mockito.verify(judgeSupport, Mockito.never()).removeProblemJudge(Mockito.any());
    }

    @Test
    void testTreatAndCheckProblemWithTreaded() throws PortableException {
        problem.setStatusType(ProblemStatusType.UNCHECK);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemService.treatAndCheckProblem(MOCKED_PROBLEM_ID);

        Mockito.verify(judgeSupport).removeProblemJudge(MOCKED_PROBLEM_ID);
        Mockito.verify(judgeSupport).reportTestOver(MOCKED_PROBLEM_ID);
    }

    @Test
    void testTreatAndCheckProblemWithNoStdCode() throws PortableException {
        problem.setStatusType(ProblemStatusType.UNTREATED);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code(null)
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        try {
            problemService.treatAndCheckProblem(MOCKED_PROBLEM_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-009", e.getCode());
        }
    }

    @Test
    void testTreatAndCheckProblemWithNoTest() throws PortableException {
        problem.setStatusType(ProblemStatusType.UNTREATED);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code(MOCKED_CODE_TEST)
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestName(new ArrayList<>());
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        try {
            problemService.treatAndCheckProblem(MOCKED_PROBLEM_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-04-010", e.getCode());
        }
    }

    @Test
    void testTreatAndCheckProblemWithNoTreated() throws PortableException {
        problem.setStatusType(ProblemStatusType.UNTREATED);
        problem.setAccessType(ProblemAccessType.PUBLIC);
        problemData.setStdCode(ProblemData.StdCode.builder()
                .code(MOCKED_CODE_TEST)
                .name("")
                .expectResultType(null)
                .languageType(LanguageType.CPP11)
                .solutionId(MOCKED_SOLUTION_ID)
                .build());
        problemData.setTestName(Collections.singletonList(MOCKED_NAME));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.FULL_ACCESS);

        problemService.treatAndCheckProblem(MOCKED_PROBLEM_ID);

        Mockito.verify(judgeSupport).removeProblemJudge(MOCKED_PROBLEM_ID);
        Mockito.verify(judgeSupport).removeProblemCache(MOCKED_PROBLEM_ID);
        Mockito.verify(judgeSupport).addTestTask(MOCKED_PROBLEM_ID);
    }

    @Test
    void testSubmitWithNotNormal() throws PortableException {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
        problem.setStatusType(ProblemStatusType.UNTREATED);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.VIEW);

        problemData.setContestId(null);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);

        SubmitSolutionRequest submitSolutionRequest = SubmitSolutionRequest.builder()
                .problemId(MOCKED_PROBLEM_ID)
                .contestId(null)
                .languageType(LanguageType.CPP11)
                .code(MOCKED_CODE_TEST)
                .build();

        try {
            problemService.submit(submitSolutionRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-05-004", e.getCode());
        }
    }

    @Test
    void testSubmitWithSuccess() throws PortableException {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withDataId(MOCKED_USER_DATA_ID);
        problem.setStatusType(ProblemStatusType.NORMAL);

        userToProblemAccessTypeMockedStatic
                .when(() -> ProblemServiceImpl.UserToProblemAccessType.of(Mockito.any(), Mockito.any()))
                .thenReturn(ProblemServiceImpl.UserToProblemAccessType.VIEW);

        problemData.setContestId(null);
        normalUserData.setSubmission(0);

        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));
        Mockito.when(problemDataManager.getProblemData(MOCKED_PROBLEM_MONGO_ID)).thenReturn(problemData);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_USER_DATA_ID)).thenReturn(normalUserData);
        Mockito.when(solutionManager.newSolution()).thenCallRealMethod();
        Mockito.when(solutionDataManager.newSolutionData(Mockito.any())).thenCallRealMethod();

        SubmitSolutionRequest submitSolutionRequest = SubmitSolutionRequest.builder()
                .problemId(MOCKED_PROBLEM_ID)
                .contestId(null)
                .languageType(LanguageType.CPP11)
                .code(MOCKED_CODE_TEST)
                .build();

        problemService.submit(submitSolutionRequest);

        Mockito.verify(problemManager).updateProblemCount(MOCKED_PROBLEM_ID, 1, 0);

        /// region 校验写入的用户信息

        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).updateUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(1, normalUserDataCP.getSubmission());

        /// endregion

        /// region 校验写入的提交数据

        ArgumentCaptor<SolutionData> solutionDataArgumentCaptor = ArgumentCaptor.forClass(SolutionData.class);
        Mockito.verify(solutionDataManager).insertSolutionData(solutionDataArgumentCaptor.capture());
        SolutionData solutionDataCP = solutionDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CODE_TEST, solutionDataCP.getCode());

        /// endregion

        /// region 校验写入的提交

        ArgumentCaptor<Solution> solutionArgumentCaptor = ArgumentCaptor.forClass(Solution.class);
        Mockito.verify(solutionManager).insertSolution(solutionArgumentCaptor.capture());
        Solution solutionCP = solutionArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_USER_ID, solutionCP.getUserId());
        Assertions.assertEquals(MOCKED_PROBLEM_ID, solutionCP.getProblemId());
        Assertions.assertNull(solutionCP.getContestId());
        Assertions.assertEquals(SolutionType.PUBLIC, solutionCP.getSolutionType());

        /// endregion
    }
}
