package com.portable.server.type;

import com.portable.server.model.contest.Contest;
import com.portable.server.model.problem.Problem;
import com.portable.server.util.test.MockedValueMaker;
import com.portable.server.util.test.UserContextBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ProblemVisitTypeTest {

    private static final Long MOCKED_USER_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_PROBLEM_ID = MockedValueMaker.mLong();

    private static final String MOCKED_PROBLEM_MONGO_ID = MockedValueMaker.mString();

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
        problem.setOwner(MockedValueMaker.mLong());
        problem.setAccessType(ProblemAccessType.PRIVATE);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestPrivateOwnerNoPermission() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PRIVATE);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestPrivateOwner() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PRIVATE);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.FULL_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestPrivateEditOther() {
        userContextBuilder.withNormalLoginIn(MockedValueMaker.mLong()).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PRIVATE);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testOfWithContestOwnerPrivateEnd() {
        userContextBuilder.withNormalLoginIn(MockedValueMaker.mLong()).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
        contest.setOwner(MockedValueMaker.mLong());
        contest.setStartTime(new Date(0));
        contest.setDuration(10000);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PRIVATE);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testOfWithContestOwnerPrivateNotEnd() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
        contest.setOwner(MOCKED_USER_ID);
        contest.setStartTime(new Date());
        contest.setDuration(10000);
        problem.setOwner(MockedValueMaker.mLong());
        problem.setAccessType(ProblemAccessType.PRIVATE);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.FULL_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestHiddenNotOwner() {
        userContextBuilder.withNotLogin();
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestHiddenOwnerNoPermission() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestHiddenOwnerWithView() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.VIEW_HIDDEN_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.VIEW, retVal);
    }

    @Test
    void testOfWithNoContestHiddenOwnerWithEdit() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.FULL_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestHiddenNotOwnerView() {
        userContextBuilder.withNormalLoginIn(MockedValueMaker.mLong()).withPermission(PermissionType.VIEW_HIDDEN_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.VIEW, retVal);
    }

    @Test
    void testOfWithNoContestHiddenNotOwnerNotViewEditOther() {
        userContextBuilder.withNormalLoginIn(MockedValueMaker.mLong()).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestHiddenNotOwnerViewEditOther() {
        userContextBuilder.withNormalLoginIn(MockedValueMaker.mLong()).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM, PermissionType.VIEW_HIDDEN_PROBLEM, PermissionType.EDIT_NOT_OWNER_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.HIDDEN);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.FULL_ACCESS, retVal);
    }

    @Test
    void testOfWithNoContestPublicNotOwner() {
        userContextBuilder.withNotLogin();
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PUBLIC);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.VIEW, retVal);
    }

    @Test
    void testOfWithNoContestPublicOwner() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PUBLIC);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.VIEW, retVal);
    }

    @Test
    void testOfWithNoContestPublicOwnerPermission() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID).withPermission(PermissionType.CREATE_AND_EDIT_PROBLEM);
        problem.setOwner(MOCKED_USER_ID);
        problem.setAccessType(ProblemAccessType.PUBLIC);

        ProblemVisitType retVal = ProblemVisitType.of(problem, contest);

        Assertions.assertEquals(ProblemVisitType.FULL_ACCESS, retVal);
    }

}
