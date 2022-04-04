package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.ProblemManagerImpl;
import com.portable.server.manager.impl.SolutionDataManagerImpl;
import com.portable.server.manager.impl.SolutionManagerImpl;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.User;
import com.portable.server.tool.UserContextBuilder;
import com.portable.server.type.PermissionType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SolutionServiceImplTest {

    @InjectMocks
    private SolutionServiceImpl solutionService;

    @Mock
    private UserManagerImpl userManager;

    @Mock
    private ProblemManagerImpl problemManager;

    @Mock
    private SolutionManagerImpl solutionManager;

    @Mock
    private SolutionDataManagerImpl solutionDataManager;

    private static final Long MOCKED_SOLUTION_ID = 1L;
    private static final Long MOCKED_USER_ID = 2L;
    private static final Long MOCKED_PROBLEM_ID = 3L;
    private static final String MOCKED_USER_HANDLE = "MOCKED_USER_HANDLE";
    private static final String MOCKED_PROBLEM_TITLE = "MOCKED_PROBLEM_TITLE";
    private static final String MOCKED_SOLUTION_MONGO_ID = "MOCKED_SOLUTION_MONGO_ID";

    private User user;
    private Problem problem;
    private Solution solution;
    private SolutionData solutionData;

    private UserContextBuilder userContextBuilder;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
        problem = Problem.builder().build();
        solution = Solution.builder().build();
        solutionData = SolutionData.builder().build();
        userContextBuilder = new UserContextBuilder();
        userContextBuilder.setup();
    }

    @AfterEach
    void tearDown() {
        userContextBuilder.tearDown();
    }

    @Test
    void testGetPublicStatus() {
        user.setId(MOCKED_USER_ID);
        user.setHandle(MOCKED_USER_HANDLE);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        solution.setUserId(MOCKED_USER_ID);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setId(MOCKED_SOLUTION_ID);

        Mockito.when(userManager.changeHandleToUserId(MOCKED_USER_HANDLE)).thenReturn(Optional.of(MOCKED_USER_ID));
        Mockito.when(solutionManager.countSolution(SolutionType.PUBLIC, MOCKED_USER_ID, null, MOCKED_PROBLEM_ID, SolutionStatusType.ACCEPT)).thenReturn(100);
        Mockito.when(solutionManager.selectSolutionByPage(10, 10, SolutionType.PUBLIC, MOCKED_USER_ID, null, MOCKED_PROBLEM_ID, SolutionStatusType.ACCEPT)).thenReturn(Collections.singletonList(solution));
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));

        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageNum(2)
                .pageSize(10)
                .queryData(SolutionListQueryRequest.builder()
                        .userHandle(MOCKED_USER_HANDLE)
                        .problemId(MOCKED_PROBLEM_ID)
                        .statusType(SolutionStatusType.ACCEPT)
                        .build())
                .build();

        PageResponse<SolutionListResponse, Void> retVal = solutionService.getPublicStatus(pageRequest);

        /// region 校验返回值

        Assertions.assertEquals(100, retVal.getTotalNum());
        Assertions.assertEquals(2, retVal.getPageNum());
        Assertions.assertEquals(10, retVal.getPageSize());
        Assertions.assertEquals(1, retVal.getData().size());
        Assertions.assertEquals(MOCKED_SOLUTION_ID, retVal.getData().get(0).getId());

        /// endregion
    }

    @Test
    void testGetSolutionWithNoSolution() {
        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.empty());

        try {
            solutionService.getSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-05-001", e.getCode());
        }
    }

    @Test
    void testGetSolutionWithNotPublic() {
        solution.setSolutionType(SolutionType.CONTEST);

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        try {
            solutionService.getSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-05-002", e.getCode());
        }
    }

    @Test
    void testGetSolutionWithNotOwner() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1);

        solution.setUserId(MOCKED_USER_ID);
        solution.setSolutionType(SolutionType.PUBLIC);

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));

        try {
            solutionService.getSolution(MOCKED_SOLUTION_ID);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-05-003", e.getCode());
        }
    }

    @Test
    void testGetSolutionWithNotOwnerPermission() throws PortableException {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.VIEW_PUBLIC_SOLUTION);

        user.setHandle(MOCKED_USER_HANDLE);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        solution.setUserId(MOCKED_USER_ID);
        solution.setSolutionType(SolutionType.PUBLIC);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));
        Mockito.when(solutionDataManager.getSolutionData(MOCKED_SOLUTION_MONGO_ID)).thenReturn(solutionData);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));

        SolutionDetailResponse retVal = solutionService.getSolution(MOCKED_SOLUTION_ID);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getUserHandle());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getProblemTitle());
        Assertions.assertNull(retVal.getJudgeReportMsgMap());

        /// endregion
    }

    @Test
    void testGetSolutionWithOwner() throws PortableException {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        user.setHandle(MOCKED_USER_HANDLE);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        solution.setUserId(MOCKED_USER_ID);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setSolutionType(SolutionType.PUBLIC);
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        solutionData.setRunningMsg(new HashMap<>());

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));
        Mockito.when(solutionDataManager.getSolutionData(MOCKED_SOLUTION_MONGO_ID)).thenReturn(solutionData);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));

        SolutionDetailResponse retVal = solutionService.getSolution(MOCKED_SOLUTION_ID);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getUserHandle());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getProblemTitle());
        Assertions.assertNull(retVal.getJudgeReportMsgMap());

        /// endregion
    }

    @Test
    void testGetSolutionWithNotOwnerPermissionAndMsg() throws PortableException {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID + 1).withPermission(PermissionType.VIEW_PUBLIC_SOLUTION, PermissionType.VIEW_SOLUTION_MESSAGE);

        user.setHandle(MOCKED_USER_HANDLE);
        problem.setTitle(MOCKED_PROBLEM_TITLE);
        solution.setUserId(MOCKED_USER_ID);
        solution.setSolutionType(SolutionType.PUBLIC);
        solution.setProblemId(MOCKED_PROBLEM_ID);
        solution.setDataId(MOCKED_SOLUTION_MONGO_ID);
        solutionData.setRunningMsg(new HashMap<>());

        Mockito.when(solutionManager.selectSolutionById(MOCKED_SOLUTION_ID)).thenReturn(Optional.of(solution));
        Mockito.when(solutionDataManager.getSolutionData(MOCKED_SOLUTION_MONGO_ID)).thenReturn(solutionData);
        Mockito.when(userManager.getAccountById(MOCKED_USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(problemManager.getProblemById(MOCKED_PROBLEM_ID)).thenReturn(Optional.of(problem));

        SolutionDetailResponse retVal = solutionService.getSolution(MOCKED_SOLUTION_ID);

        /// region 校验返回值

        Assertions.assertEquals(MOCKED_USER_HANDLE, retVal.getUserHandle());
        Assertions.assertEquals(MOCKED_PROBLEM_TITLE, retVal.getProblemTitle());
        Assertions.assertNotNull(retVal.getJudgeReportMsgMap());

        /// endregion
    }

}