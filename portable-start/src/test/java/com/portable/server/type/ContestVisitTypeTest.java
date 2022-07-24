package com.portable.server.type;

import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.test.MockedValueMaker;
import com.portable.server.test.UserContextBuilder;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
class ContestVisitTypeTest {

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
    void testCheckPermissionWithCache() {
        Long contestId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn().withContestCache(contestId, ContestVisitType.NO_ACCESS);

        Contest contest = Contest.builder()
                .id(contestId)
                .build();
        PublicContestData contestData = PublicContestData.builder().build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testCheckPermissionWithOwner() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId);

        Contest contest = Contest.builder()
                .id(contestId)
                .owner(useId)
                .build();
        PublicContestData contestData = PublicContestData.builder().build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.ADMIN, retVal);
    }

    @Test
    void testCheckPermissionWithCoAuthor() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId);

        Contest contest = Contest.builder()
                .id(contestId)
                .owner(MockedValueMaker.mLong())
                .build();
        PublicContestData contestData = PublicContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(useId))
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.CO_AUTHOR, retVal);
    }

    @Test
    void testCheckPermissionWithPublic() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId);

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.PUBLIC)
                .owner(MockedValueMaker.mLong())
                .build();
        PublicContestData contestData = PublicContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.PARTICIPANT, retVal);
    }

    @Test
    void testCheckPermissionWithPrivateNoAccess() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId);

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.PRIVATE)
                .owner(MockedValueMaker.mLong())
                .build();
        PrivateContestData contestData = PrivateContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .inviteUserSet(new HashSet<>())
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testCheckPermissionWithPrivateInvite() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId);

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.PRIVATE)
                .owner(MockedValueMaker.mLong())
                .build();
        PrivateContestData contestData = PrivateContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .inviteUserSet(Sets.newLinkedHashSet(useId))
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.PARTICIPANT, retVal);
    }

    @Test
    void testCheckPermissionWithPrivateViewPermission() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId).withPermission(PermissionType.VIEW_ALL_CONTEST);

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.PRIVATE)
                .owner(MockedValueMaker.mLong())
                .build();
        PrivateContestData contestData = PrivateContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .inviteUserSet(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.VISIT, retVal);
    }

    @Test
    void testCheckPermissionWithPrivateEditPermission() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId).withPermission(PermissionType.VIEW_ALL_CONTEST, PermissionType.EDIT_NOT_OWNER_CONTEST);

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.PRIVATE)
                .owner(MockedValueMaker.mLong())
                .build();
        PrivateContestData contestData = PrivateContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .inviteUserSet(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.ADMIN, retVal);
    }

    @Test
    void testCheckPermissionWithBatchNoAccess() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        Long batchId = MockedValueMaker.mLong();
        userContextBuilder.withNormalLoginIn(useId).withPermission();

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.BATCH)
                .owner(MockedValueMaker.mLong())
                .build();
        BatchContestData contestData = BatchContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .batchId(batchId)
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.NO_ACCESS, retVal);
    }

    @Test
    void testCheckPermissionWithBatchSame() {
        Long contestId = MockedValueMaker.mLong();
        userContextBuilder.withBatchLoginIn().withContestId(contestId);

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.BATCH)
                .owner(MockedValueMaker.mLong())
                .build();
        BatchContestData contestData = BatchContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.PARTICIPANT, retVal);
    }

    @Test
    void testCheckPermissionWithPassword() {
        Long contestId = MockedValueMaker.mLong();
        Long useId = MockedValueMaker.mLong();
        Long batchId = MockedValueMaker.mLong();
        userContextBuilder.withBatchLoginIn(useId).withContestId(contestId);

        Contest contest = Contest.builder()
                .id(contestId)
                .accessType(ContestAccessType.PASSWORD)
                .owner(MockedValueMaker.mLong())
                .build();
        PasswordContestData contestData = PasswordContestData.builder()
                .coAuthor(Sets.newLinkedHashSet(MockedValueMaker.mLong()))
                .password(MockedValueMaker.mString())
                .build();

        ContestVisitType retVal = ContestVisitType.checkPermission(contest, contestData);

        Assertions.assertEquals(ContestVisitType.NO_ACCESS, retVal);
    }
}