package com.portable.server.service.impl;

import com.portable.server.manager.ContestManager;
import com.portable.server.manager.ProblemDataManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserDataManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.support.FileSupport;
import com.portable.server.support.JudgeSupport;
import com.portable.server.type.PermissionType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemListStatusType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.util.UserContext;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    private List<Problem> problemList;
    private UserContext userContext;

    private MockedStatic<UserContext> userContextMockedStatic;

    @BeforeEach
    void setUp() {
        problemList = new ArrayList<Problem>() {{
            add(Problem.builder().id(1L).build());
            add(Problem.builder().id(2L).build());
            add(Problem.builder().id(3L).build());
        }};
        userContext = new UserContext();

        userContextMockedStatic = Mockito.mockStatic(UserContext.class);
    }


    @AfterEach
    void tearDown() {
        userContextMockedStatic.close();
    }

    @Test
    void testGetProblemListWithNoLogin() {
        userContext.setId(null);

        List<ProblemAccessType> problemAccessTypeList = Collections.singletonList(ProblemAccessType.PUBLIC);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, null)).thenReturn(problemList.size());
        Mockito.when(problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, null, 30, 0)).thenReturn(problemList);

        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(1)
                .pageSize(30)
                .build();
        PageResponse<ProblemListResponse, Void> retVal = problemService.getProblemList(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(3, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getTotalPage());
        Assertions.assertEquals(1, retVal.getPageNum());
        Assertions.assertTrue(IntStream.range(0, 3)
                .allMatch(i -> Objects.equals(i + 1L, retVal.getData().get(i).getId()))
        );
        Assertions.assertTrue(retVal.getData().stream()
                .allMatch(problemListResponse -> ProblemListStatusType.NEVER_SUBMIT.equals(problemListResponse.getProblemListStatusType())));

        /// endregion
    }

    @Test
    void testGetProblemListWithNoPermission() {
        userContext.setId(MOCKED_USER_ID);
        userContext.setPermissionTypeSet(new HashSet<>());

        Solution solutionAC = Solution.builder()
                .status(SolutionStatusType.ACCEPT)
                .build();
        Solution solutionWA = Solution.builder()
                .status(SolutionStatusType.WRONG_ANSWER)
                .build();

        List<ProblemAccessType> problemAccessTypeList = Collections.singletonList(ProblemAccessType.PUBLIC);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, MOCKED_USER_ID)).thenReturn(problemList.size());
        Mockito.when(problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, MOCKED_USER_ID, 30, 0)).thenReturn(problemList);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 1L)).thenReturn(solutionAC);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 2L)).thenReturn(solutionWA);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 3L)).thenReturn(null);

        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(1)
                .pageSize(30)
                .build();
        PageResponse<ProblemListResponse, Void> retVal = problemService.getProblemList(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(3, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getTotalPage());
        Assertions.assertEquals(1, retVal.getPageNum());
        Assertions.assertTrue(IntStream.range(0, 3)
                .allMatch(i -> {
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
                })
        );

        /// endregion
    }

    @Test
    void testGetProblemListWithPermission() {
        userContext.setId(MOCKED_USER_ID);
        userContext.setPermissionTypeSet(new HashSet<PermissionType>() {{
            add(PermissionType.VIEW_HIDDEN_PROBLEM);
        }});

        Solution solutionAC = Solution.builder()
                .status(SolutionStatusType.ACCEPT)
                .build();
        Solution solutionWA = Solution.builder()
                .status(SolutionStatusType.WRONG_ANSWER)
                .build();

        List<ProblemAccessType> problemAccessTypeList = Arrays.asList(ProblemAccessType.PUBLIC, ProblemAccessType.HIDDEN);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, MOCKED_USER_ID)).thenReturn(problemList.size());
        Mockito.when(problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, MOCKED_USER_ID, 30, 0)).thenReturn(problemList);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 1L)).thenReturn(solutionAC);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 2L)).thenReturn(solutionWA);
        Mockito.when(solutionManager.selectLastSolutionByUserIdAndProblemId(MOCKED_USER_ID, 3L)).thenReturn(null);

        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(1)
                .pageSize(30)
                .build();
        PageResponse<ProblemListResponse, Void> retVal = problemService.getProblemList(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(3, retVal.getTotalNum());
        Assertions.assertEquals(1, retVal.getTotalPage());
        Assertions.assertEquals(1, retVal.getPageNum());
        Assertions.assertTrue(IntStream.range(0, 3)
                .allMatch(i -> {
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
                })
        );

        /// endregion
    }

    @Test
    void searchProblemSetList() {
    }

    @Test
    void searchPrivateProblemList() {
    }

    @Test
    void getProblem() {
    }

    @Test
    void getProblemTestList() {
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
