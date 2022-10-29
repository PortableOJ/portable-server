package com.portable.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.prod.BatchManagerImpl;
import com.portable.server.manager.impl.prod.ContestManagerImpl;
import com.portable.server.manager.impl.prod.UserManagerImpl;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.batch.BatchRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.batch.BatchListResponse;
import com.portable.server.model.response.batch.CreateBatchResponse;
import com.portable.server.model.user.User;
import com.portable.server.service.impl.BatchServiceImpl;
import com.portable.server.test.MockedValueMaker;
import com.portable.server.test.UserContextBuilder;
import com.portable.server.type.BatchStatusType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchServiceImplTest {

    @InjectMocks
    private BatchServiceImpl batchService;

    @Mock
    private UserManagerImpl userManager;

    @Mock
    private ContestManagerImpl contestManager;

    @Mock
    private BatchManagerImpl batchManager;

    private static final Long MOCKED_USER_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_BATCH_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_CONTEST_ID = MockedValueMaker.mLong();

    private static final String MOCKED_CONTEST_TITLE = MockedValueMaker.mString();
    private static final String MOCKED_BATCh_PREFIX = MockedValueMaker.mString();

    private Batch batch;
    private Contest contest;
    private List<Batch> mockedBatchList;

    private UserContextBuilder userContextBuilder;

    @BeforeEach
    void setUp() {
        batch = Batch.builder()
                .id(MOCKED_BATCH_ID)
                .prefix(MOCKED_BATCh_PREFIX)
                .contestId(MOCKED_CONTEST_ID)
                .build();
        contest = Contest.builder()
                .id(MOCKED_CONTEST_ID)
                .title(MOCKED_CONTEST_TITLE)
                .build();
        mockedBatchList = new ArrayList<Batch>() {{
            add(Batch.builder().id(1L).prefix("a").contestId(MOCKED_CONTEST_ID).build());
            add(Batch.builder().id(2L).prefix("b").contestId(null).build());
            add(Batch.builder().id(3L).prefix("c").contestId(null).build());
        }};

        userContextBuilder = new UserContextBuilder();
        userContextBuilder.setup();
    }

    @AfterEach
    void tearDown() {
        userContextBuilder.tearDown();
    }

    @Test
    void testGetList() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        Mockito.when(batchManager.countBatchListByOwnerId(MOCKED_USER_ID)).thenReturn(300);
        Mockito.when(batchManager.selectBatchByPage(MOCKED_USER_ID, 10, 0)).thenReturn(mockedBatchList);
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));

        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(1)
                .pageSize(10)
                .build();

        PageResponse<BatchListResponse, Void> retVal = batchService.getList(pageRequest);

        Assertions.assertEquals(3, retVal.getData().size());
        Assertions.assertEquals("a", retVal.getData().get(0).getPrefix());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getData().get(0).getContestId());
        Assertions.assertEquals(MOCKED_CONTEST_TITLE, retVal.getData().get(0).getContestTitle());
        Assertions.assertEquals("b", retVal.getData().get(1).getPrefix());
        Assertions.assertEquals("c", retVal.getData().get(2).getPrefix());
    }

    @Test
    void testCreateWithExist() {
        Mockito.when(batchManager.selectBatchByPrefix(MOCKED_BATCh_PREFIX)).thenReturn(Optional.of(batch));

        BatchRequest batchRequest = BatchRequest.builder()
                .prefix(MOCKED_BATCh_PREFIX)
                .count(10)
                .ipLock(false)
                .build();

        try {
            batchService.create(batchRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-001", e.getCode());
        }
    }

    @Test
    void testCreateWithSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        Mockito.when(batchManager.selectBatchByPrefix(MOCKED_BATCh_PREFIX)).thenReturn(Optional.empty());
        Mockito.when(batchManager.newBatch()).thenCallRealMethod();
        Mockito.when(userManager.newBatchUserData()).thenCallRealMethod();
        Mockito.when(userManager.newBatchAccount()).thenCallRealMethod();

        Mockito.doAnswer(invocationOnMock -> {
            Batch batch = invocationOnMock.getArgument(0);
            batch.setId(MOCKED_BATCH_ID);
            Assertions.assertEquals(MOCKED_USER_ID, batch.getOwner());
            return null;
        }).when(batchManager).insertBatch(Mockito.any());

        BatchRequest batchRequest = BatchRequest.builder()
                .prefix(MOCKED_BATCh_PREFIX)
                .count(10)
                .ipLock(false)
                .build();

        CreateBatchResponse retVal = batchService.create(batchRequest);

        Assertions.assertEquals(MOCKED_BATCH_ID, retVal.getId());
        Assertions.assertEquals(10, retVal.getBatchUserList().size());


        /// region 校验写入的用户账号密码是否正确

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userManager, Mockito.times(10)).insertAccount(userArgumentCaptor.capture());
        List<User> userCP = userArgumentCaptor.getAllValues();
        Assertions.assertEquals(10, userCP.size());
        IntStream.range(0, 10)
                .boxed()
                .forEach(integer -> {
                    Assertions.assertEquals(retVal.getBatchUserList().get(integer).getHandle(), userCP.get(integer).getHandle());
                    Assertions.assertTrue(BCryptEncoder.match(retVal.getBatchUserList().get(integer).getPassword(), userCP.get(integer).getPassword()));
                });

        /// endregion
    }

    @Test
    void testChangeStatusWithNoBatch() {
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.empty());

        try {
            batchService.changeStatus(MOCKED_BATCH_ID, BatchStatusType.NORMAL);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-006", e.getCode());
        }
    }

    @Test
    void testChangeStatusWithNotOwner() {
        userContextBuilder.withNormalLoginIn(MockedValueMaker.mLong());

        batch.setOwner(MOCKED_USER_ID);
        batch.setStatus(BatchStatusType.DISABLE);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

        try {
            batchService.changeStatus(MOCKED_BATCH_ID, BatchStatusType.NORMAL);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-002", e.getCode());
        }
    }

    @Test
    void testChangeStatusWithSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        batch.setOwner(MOCKED_USER_ID);
        batch.setStatus(BatchStatusType.DISABLE);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

        batchService.changeStatus(MOCKED_BATCH_ID, BatchStatusType.NORMAL);

        Mockito.verify(batchManager).updateBatchStatus(MOCKED_BATCH_ID, BatchStatusType.NORMAL);
    }

    @Test
    void getBatchWithContetSuccess() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        batch.setOwner(MOCKED_USER_ID);
        batch.setStatus(BatchStatusType.DISABLE);
        batch.setContestId(MOCKED_CONTEST_ID);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        Mockito.when(contestManager.getContestById(MOCKED_CONTEST_ID)).thenReturn(Optional.of(contest));

        BatchListResponse retVal = batchService.getBatch(MOCKED_BATCH_ID);

        Assertions.assertEquals(MOCKED_BATCH_ID, retVal.getId());
        Assertions.assertEquals(MOCKED_CONTEST_ID, retVal.getContestId());
        Assertions.assertEquals(MOCKED_CONTEST_TITLE, retVal.getContestTitle());
    }

    @Test
    void changeBatchIpLock() {
        userContextBuilder.withNormalLoginIn(MOCKED_USER_ID);

        batch.setOwner(MOCKED_USER_ID);
        batch.setIpLock(false);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

        batchService.changeBatchIpLock(MOCKED_BATCH_ID, true);

        Mockito.verify(batchManager).updateBatchIpLock(MOCKED_BATCH_ID, Boolean.TRUE);
    }
}