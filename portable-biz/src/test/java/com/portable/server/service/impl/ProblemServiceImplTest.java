package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestManager;
import com.portable.server.manager.ProblemDataManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserDataManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.user.User;
import com.portable.server.support.FileSupport;
import com.portable.server.support.JudgeSupport;
import com.portable.server.tool.UserContextBuilder;
import com.portable.server.type.PermissionType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemListStatusType;
import com.portable.server.type.SolutionStatusType;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceImplTest {

    @InjectMocks
    private ProblemServiceImpl problemService;

    @Mock
    private ProblemManager problemManager;

    @Mock
    private ProblemDataManager problemDataManager;

    @Mock
    private UserManager userManager;

    @Mock
    private UserDataManager userDataManager;

    @Mock
    private SolutionManager solutionManager;

    @Mock
    private SolutionDataManager solutionDataManager;

    @Mock
    private ContestManager contestManager;

    @Mock
    private FileSupport fileSupport;

    @Mock
    private JudgeSupport judgeSupport;

    private static final Long MOCKED_USER_ID = 1L;
    private static final Long MOCKED_PROBLEM_ID = 2L;
    private static final String MOCKED_PROBLEM_MONGO_ID = "MOCKED_PROBLEM_MONGO_ID";
    private static final String MOCKED_HANDLE = "MOCKED_HANDLE";

    private Problem problem;
    private ProblemData problemData;
    private List<Problem> problemList;
    private User user;

    private UserContextBuilder userContextBuilder;

    private MockedStatic<ProblemServiceImpl.UserToProblemAccessType> userToProblemAccessTypeMockedStatic;

    public static class UserToProblemAccessTypeTest {

        private static final Long MOCKED_USER_ID = 1L;
        private static final Long MOCKED_PROBLEM_ID = 2L;
        private static final Long MOCKED_CONTEST_ID = 3L;
        private static final String MOCKED_PROBLEM_MONGO_ID = "MOCKED_PROBLEM_MONGO_ID";
        private static final String MOCKED_HANDLE = "MOCKED_HANDLE";

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
            userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
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
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 1L)).thenReturn(solutionAC);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 2L)).thenReturn(solutionWA);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 3L)).thenReturn(null);

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
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 1L)).thenReturn(solutionAC);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 2L)).thenReturn(solutionWA);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 3L)).thenReturn(null);

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
    void showTestInput() {
    }

    @Test
    void showTestOutput() {
    }

    @Test
    void downloadTestInput() {
    }

    @Test
    void downloadTestOutput() {
    }

    @Test
    void newProblem() {
    }

    @Test
    void updateProblemContent() {
    }

    @Test
    void updateProblemSetting() {
    }

    @Test
    void updateProblemJudge() {
    }

    @Test
    void addProblemTest() {
    }

    @Test
    void removeProblemTest() {
    }

    @Test
    void getProblemStdTestCode() {
    }

    @Test
    void updateProblemStdCode() {
    }

    @Test
    void addProblemTestCode() {
    }

    @Test
    void removeProblemTestCode() {
    }

    @Test
    void showStdCode() {
    }

    @Test
    void showTestCode() {
    }

    @Test
    void downloadStdCode() {
    }

    @Test
    void downloadTestCode() {
    }

    @Test
    void treatAndCheckProblem() {
    }

    @Test
    void submit() {
    }
}
