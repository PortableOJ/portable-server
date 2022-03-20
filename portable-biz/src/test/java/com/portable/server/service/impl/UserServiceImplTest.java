package com.portable.server.service.impl;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.BatchManagerImpl;
import com.portable.server.manager.impl.GridFsManagerImpl;
import com.portable.server.manager.impl.UserDataManagerImpl;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.response.user.BatchUserInfoResponse;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import com.portable.server.type.BatchStatusType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import com.portable.server.util.UserContext;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserManagerImpl userManager;

    @Mock
    private UserDataManagerImpl userDataManager;

    @Mock
    private BatchManagerImpl batchManager;

    @Mock
    private GridFsManagerImpl gridFsManager;

    private static MockedStatic<BCryptEncoder> bCryptEncoderMockedStatic;
    private static MockedStatic<UserContext> userContextMockedStatic;
    private static MockedStatic<NormalUserInfoResponse> normalUserInfoResponseMockedStatic;
    private static MockedStatic<BatchUserInfoResponse> batchUserInfoResponseMockedStatic;

    private static final String MOCKED_ROOT_NAME = "MOCKED_ROOT_NAME";
    private static final String MOCKED_ROOT_PASSWORD = "MOCKED_ROOT_PASSWORD";
    private static final String MOCKED_ROOT_PASSWORD_ENCODED = "MOCKED_ROOT_PASSWORD_ENCODED";
    private static final String MOCKED_MONGO_ID = "MOCKED_MONGO_ID";
    private static final String MOCKED_HANDLE = "MOCKED_HANDLE";
    private static final String MOCKED_INPUT_PASSWORD = "MOCKED_INPUT_PASSWORD";
    private static final String MOCKED_IP = "MOCKED_IP";
    private static final String MOCKED_OTHER_IP = "MOCKED_OTHER_IP";
    private static final Long MOCKED_ID = 0L;
    private static final Long MOCKED_BATCH_ID = 1L;
    private static final Long MOCKED_CONTEST_ID = 2L;

    private static User user;
    private static NormalUserData normalUserData;
    private static BatchUserData batchUserData;

    @BeforeEach
    void setUp() {
        bCryptEncoderMockedStatic = Mockito.mockStatic(BCryptEncoder.class);
        userContextMockedStatic = Mockito.mockStatic(UserContext.class);
        normalUserInfoResponseMockedStatic = Mockito.mockStatic(NormalUserInfoResponse.class);
        batchUserInfoResponseMockedStatic = Mockito.mockStatic(BatchUserInfoResponse.class);

        user = User.builder()
                .id(MOCKED_ID)
                .dataId(MOCKED_MONGO_ID)
                .handle(MOCKED_HANDLE)
                .password(MOCKED_ROOT_PASSWORD_ENCODED)
                .type(AccountType.NORMAL)
                .build();
        normalUserData = NormalUserData.builder()
                ._id(MOCKED_MONGO_ID)
                .build();
        batchUserData = BatchUserData.builder()
                ._id(MOCKED_MONGO_ID)
                .batchId(MOCKED_BATCH_ID)
                .build();
    }

    @AfterEach
    void tearDown() {
        bCryptEncoderMockedStatic.close();
        userContextMockedStatic.close();
        normalUserInfoResponseMockedStatic.close();
        batchUserInfoResponseMockedStatic.close();
    }

    @Test
    void testInitWithNoRoot() {
        ReflectionTestUtils.setField(userService, "rootName", MOCKED_ROOT_NAME);
        ReflectionTestUtils.setField(userService, "rootPassword", MOCKED_ROOT_PASSWORD);

        Mockito.when(userManager.getAccountByHandle(MOCKED_ROOT_NAME)).thenReturn(null);
        Mockito.when(userDataManager.newNormalUserData()).thenAnswer(invocationOnMock -> normalUserData);
        Mockito.when(userManager.newNormalAccount()).thenAnswer(invocationOnMock -> user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.encoder(MOCKED_ROOT_PASSWORD)).thenReturn(MOCKED_ROOT_PASSWORD_ENCODED);

        Mockito.doAnswer(invocationOnMock -> {
            NormalUserData argument = invocationOnMock.getArgument(0);
            argument.set_id(MOCKED_MONGO_ID);
            return null;
        }).when(userDataManager).insertUserData(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> {
            User argument = invocationOnMock.getArgument(0);
            argument.setId(MOCKED_ID);
            return null;
        }).when(userManager).insertAccount(Mockito.any());

        // DO
        userService.init();
        // END CALL


        /// region 校验写入数据库的 root 权限信息
        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).insertUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(OrganizationType.ADMIN, normalUserDataCP.getOrganization());
        // 校验拥有了所有权限
        Assertions.assertEquals(Arrays.stream(PermissionType.values()).collect(Collectors.toSet()),
                normalUserDataCP.getPermissionTypeSet());

        /// endregion

        /// region 校验写入数据库的 root 账号密码
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userManager).insertAccount(userArgumentCaptor.capture());
        User userCP = userArgumentCaptor.getValue();

        Assertions.assertEquals(MOCKED_ROOT_NAME, userCP.getHandle());
        Assertions.assertEquals(MOCKED_ROOT_PASSWORD_ENCODED, userCP.getPassword());
        Assertions.assertEquals(normalUserData.get_id(), userCP.getDataId());
        /// endregion
    }

    @Test
    void testInitWithRoot() {
        user.setDataId(MOCKED_MONGO_ID);

        ReflectionTestUtils.setField(userService, "rootName", MOCKED_ROOT_NAME);

        Mockito.when(userManager.getAccountByHandle(MOCKED_ROOT_NAME)).thenReturn(user);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);

        Mockito.doNothing().when(userDataManager).updateUserData(Mockito.any());

        // DO
        userService.init();
        // END CALL

        /// region 校验写入数据库的 root 权限信息
        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).updateUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        /// 暂时不校验
        // Assertions.assertEquals(normalUserDataCP.getOrganization(), OrganizationType.ADMIN);
        // 校验拥有了所有权限
        Assertions.assertEquals(Arrays.stream(PermissionType.values()).collect(Collectors.toSet()),
                normalUserDataCP.getPermissionTypeSet());
        /// endregion
    }

    @Test
    void testLoginWithNoUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(null);

        // DO
        try {
            userService.login(loginRequest, MOCKED_IP);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
        // END CALL


        /// region 校验不能有写入 UserContext 的 User 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(User.class)), Mockito.never());
        /// endregion

        /// region 校验不能有写入 UserContest 的 UserData 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(NormalUserData.class)), Mockito.never());
        /// endregion
    }

    @Test
    void testLoginWithErrorPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(false);

        // DO
        try {
            userService.login(loginRequest, MOCKED_IP);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-002", e.getCode());
        }
        // END CALL


        /// region 校验不能有写入 UserContext 的 User 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(User.class)), Mockito.never());
        /// endregion

        /// region 校验不能有写入 UserContest 的 UserData 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(NormalUserData.class)), Mockito.never());
        /// endregion
    }

    @Test
    void testLoginWithNormalUserAndNoData() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.NORMAL);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(null);

        // DO
        try {
            userService.login(loginRequest, MOCKED_IP);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
        // END CALL

        /// region 校验不能有写入 UserContext 的 User 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(User.class)), Mockito.never());
        /// endregion

        /// region 校验不能有写入 UserContest 的 UserData 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(NormalUserData.class)), Mockito.never());
        /// endregion
    }

    @Test
    void testLoginWithNormalUserAndSuccess() throws PortableException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.NORMAL);
        normalUserData.setPermissionTypeSet(new HashSet<PermissionType>() {{
            add(PermissionType.MANAGER_JUDGE);
        }});

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        normalUserInfoResponseMockedStatic.when(() -> NormalUserInfoResponse.of(Mockito.any(), Mockito.any())).thenCallRealMethod();

        // DO
        NormalUserInfoResponse retVal = (NormalUserInfoResponse) userService.login(loginRequest, MOCKED_IP);
        // END CALL

        /// region 校验返回值

        Assertions.assertEquals(user.getHandle(), retVal.getHandle());
        Assertions.assertEquals(AccountType.NORMAL, retVal.getType());
        Assertions.assertEquals(normalUserData.getOrganization(), retVal.getOrganizationType());
        Assertions.assertEquals(normalUserData.getPermissionTypeSet(), retVal.getPermissionTypeSet());

        /// endregion

        /// region 校验写入 UserContext 的 User 部分

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        userContextMockedStatic.verify(() -> UserContext.set(userArgumentCaptor.capture()));
        User userCP = userArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_HANDLE, userCP.getHandle());
        Assertions.assertEquals(AccountType.NORMAL, userCP.getType());
        Assertions.assertEquals(MOCKED_MONGO_ID, userCP.getDataId());

        /// endregion

        /// region 校验写入 UserContest 的 UserData 部分

        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        userContextMockedStatic.verify(() -> UserContext.set(normalUserDataArgumentCaptor.capture()));
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertTrue(normalUserDataCP.getPermissionTypeSet().contains(PermissionType.MANAGER_JUDGE));
        Assertions.assertFalse(normalUserDataCP.getPermissionTypeSet().contains(PermissionType.CHANGE_ORGANIZATION));

        /// endregion
    }

    @Test
    void testLoginWithBatchUserAndNoData() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.BATCH);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(null);

        // DO
        try {
            userService.login(loginRequest, MOCKED_IP);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
        // END CALL

        /// region 校验不能有写入 UserContext 的 User 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(User.class)), Mockito.never());
        /// endregion

        /// region 校验不能有写入 UserContest 的 UserData 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(NormalUserData.class)), Mockito.never());
        /// endregion
    }

    @Test
    void testLoginWithBatchUserIPLockOtherIp() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.BATCH);


        batchUserData.setIpList(new ArrayList<BatchUserData.IpRecord>() {{
            add(BatchUserData.IpRecord.builder()
                    .date(new Date())
                    .ip(MOCKED_OTHER_IP)
                    .build());
        }});

        Batch batch = Batch.builder()
                .status(BatchStatusType.NORMAL)
                .ipLock(true)
                .contestId(MOCKED_CONTEST_ID)
                .build();

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        batchUserInfoResponseMockedStatic.when(() -> BatchUserInfoResponse.of(Mockito.any(), Mockito.any())).thenCallRealMethod();

        // DO

        try {
            userService.login(loginRequest, MOCKED_IP);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-012", e.getCode());
        }

        // END CALL

        /// region 校验不能有写入 UserContext 的 User 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(User.class)), Mockito.never());
        /// endregion

        /// region 校验不能有写入 UserContest 的 UserData 部分
        userContextMockedStatic.verify(() -> UserContext.set(Mockito.any(NormalUserData.class)), Mockito.never());
        /// endregion
    }

    @Test
    void testLoginWithBatchUserNoIPLockAndSuccess() throws PortableException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.BATCH);


        batchUserData.setIpList(new ArrayList<BatchUserData.IpRecord>() {{
            add(BatchUserData.IpRecord.builder()
                    .date(new Date())
                    .ip(MOCKED_OTHER_IP)
                    .build());
        }});

        Batch batch = Batch.builder()
                .status(BatchStatusType.NORMAL)
                .ipLock(false)
                .contestId(MOCKED_CONTEST_ID)
                .build();

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        batchUserInfoResponseMockedStatic.when(() -> BatchUserInfoResponse.of(Mockito.any(), Mockito.any())).thenCallRealMethod();

        // DO

        BatchUserInfoResponse batchUserInfoResponse = (BatchUserInfoResponse) userService.login(loginRequest, MOCKED_IP);

        // END CALL

        /// region 校验返回值

        Assertions.assertEquals(user.getHandle(), batchUserInfoResponse.getHandle());
        Assertions.assertEquals(AccountType.BATCH, batchUserInfoResponse.getType());
        Assertions.assertEquals(batch.getContestId(), batchUserInfoResponse.getContestId());

        /// endregion


        /// region 校验写入 UserContext 的 User 部分

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        userContextMockedStatic.verify(() -> UserContext.set(userArgumentCaptor.capture()));
        User userCP = userArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_HANDLE, userCP.getHandle());
        Assertions.assertEquals(AccountType.BATCH, userCP.getType());
        Assertions.assertEquals(MOCKED_MONGO_ID, userCP.getDataId());

        /// endregion

        /// region 校验写入 UserContest 的 UserData 部分

        ArgumentCaptor<Batch> batchArgumentCaptor = ArgumentCaptor.forClass(Batch.class);
        userContextMockedStatic.verify(() -> UserContext.set(batchArgumentCaptor.capture()));
        Batch batchCP = batchArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, batchCP.getContestId());

        /// endregion
    }

    @Test
    void testLoginWithBatchUserIpLockNoIPAndSuccess() throws PortableException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.BATCH);


        batchUserData.setIpList(new ArrayList<>());

        Batch batch = Batch.builder()
                .status(BatchStatusType.NORMAL)
                .ipLock(true)
                .contestId(MOCKED_CONTEST_ID)
                .build();

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        batchUserInfoResponseMockedStatic.when(() -> BatchUserInfoResponse.of(Mockito.any(), Mockito.any())).thenCallRealMethod();

        // DO

        BatchUserInfoResponse batchUserInfoResponse = (BatchUserInfoResponse) userService.login(loginRequest, MOCKED_IP);

        // END CALL

        /// region 校验返回值

        Assertions.assertEquals(user.getHandle(), batchUserInfoResponse.getHandle());
        Assertions.assertEquals(AccountType.BATCH, batchUserInfoResponse.getType());
        Assertions.assertEquals(batch.getContestId(), batchUserInfoResponse.getContestId());

        /// endregion


        /// region 校验写入 UserContext 的 User 部分

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        userContextMockedStatic.verify(() -> UserContext.set(userArgumentCaptor.capture()));
        User userCP = userArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_HANDLE, userCP.getHandle());
        Assertions.assertEquals(AccountType.BATCH, userCP.getType());
        Assertions.assertEquals(MOCKED_MONGO_ID, userCP.getDataId());

        /// endregion

        /// region 校验写入 UserContest 的 UserData 部分

        ArgumentCaptor<Batch> batchArgumentCaptor = ArgumentCaptor.forClass(Batch.class);
        userContextMockedStatic.verify(() -> UserContext.set(batchArgumentCaptor.capture()));
        Batch batchCP = batchArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, batchCP.getContestId());

        /// endregion
    }

    @Test
    void testLoginWithBatchUserIpLockOneIPAndSuccess() throws PortableException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.BATCH);

        batchUserData.setIpList(new ArrayList<BatchUserData.IpRecord>() {{
            add(BatchUserData.IpRecord.builder()
                    .date(new Date())
                    .ip(MOCKED_IP)
                    .build());
        }});

        Batch batch = Batch.builder()
                .status(BatchStatusType.NORMAL)
                .ipLock(true)
                .contestId(MOCKED_CONTEST_ID)
                .build();

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        batchUserInfoResponseMockedStatic.when(() -> BatchUserInfoResponse.of(Mockito.any(), Mockito.any())).thenCallRealMethod();

        // DO

        BatchUserInfoResponse batchUserInfoResponse = (BatchUserInfoResponse) userService.login(loginRequest, MOCKED_IP);

        // END CALL

        /// region 校验返回值

        Assertions.assertEquals(user.getHandle(), batchUserInfoResponse.getHandle());
        Assertions.assertEquals(AccountType.BATCH, batchUserInfoResponse.getType());
        Assertions.assertEquals(batch.getContestId(), batchUserInfoResponse.getContestId());

        /// endregion


        /// region 校验写入 UserContext 的 User 部分

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        userContextMockedStatic.verify(() -> UserContext.set(userArgumentCaptor.capture()));
        User userCP = userArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_HANDLE, userCP.getHandle());
        Assertions.assertEquals(AccountType.BATCH, userCP.getType());
        Assertions.assertEquals(MOCKED_MONGO_ID, userCP.getDataId());

        /// endregion

        /// region 校验写入 UserContest 的 UserData 部分

        ArgumentCaptor<Batch> batchArgumentCaptor = ArgumentCaptor.forClass(Batch.class);
        userContextMockedStatic.verify(() -> UserContext.set(batchArgumentCaptor.capture()));
        Batch batchCP = batchArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_CONTEST_ID, batchCP.getContestId());

        /// endregion
    }

    @Test
    void testRegisterWithHasHandle() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHandle(MOCKED_HANDLE);
        registerRequest.setPassword(MOCKED_INPUT_PASSWORD);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(user);

        try {
            userService.register(registerRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-003", e.getCode());
        }
    }

    @Test
    void testRegisterWithSuccess() throws PortableException {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHandle(MOCKED_HANDLE);
        registerRequest.setPassword(MOCKED_INPUT_PASSWORD);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(null);
        Mockito.when(userDataManager.newNormalUserData()).thenAnswer(invocationOnMock -> normalUserData);
        Mockito.when(userManager.newNormalAccount()).thenAnswer(invocationOnMock -> user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.encoder(MOCKED_INPUT_PASSWORD)).thenReturn(MOCKED_ROOT_PASSWORD_ENCODED);
        normalUserInfoResponseMockedStatic.when(() -> NormalUserInfoResponse.of(Mockito.any(), Mockito.any())).thenCallRealMethod();

        Mockito.doAnswer(invocationOnMock -> {
            NormalUserData userData = invocationOnMock.getArgument(0);
            userData.set_id(MOCKED_MONGO_ID);
            return null;
        }).when(userDataManager).insertUserData(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            user.setId(MOCKED_ID);
            return null;
        }).when(userManager).insertAccount(Mockito.any());

        NormalUserInfoResponse retVal = userService.register(registerRequest);

        /// region 校验返回值是否正确

        Assertions.assertEquals(MOCKED_HANDLE, retVal.getHandle());
        Assertions.assertEquals(AccountType.NORMAL, retVal.getType());

        /// endregion

        /// region 校验写入的用户是否正确

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userManager).insertAccount(userArgumentCaptor.capture());
        User userCP = userArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_HANDLE, userCP.getHandle());
        Assertions.assertEquals(MOCKED_ROOT_PASSWORD_ENCODED, userCP.getPassword());
        Assertions.assertEquals(AccountType.NORMAL, userCP.getType());

        /// endregion
        /// region 校验写入的用户数据是否正确
        
        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).insertUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(userCP.getDataId(), normalUserDataCP.get_id());

        /// endregion
    }

    @Test
    void getUserInfo() {
    }

    @Test
    void testGetUserInfo() {
    }

    @Test
    void changeOrganization() {
    }

    @Test
    void addPermission() {
    }

    @Test
    void removePermission() {
    }

    @Test
    void uploadAvatar() {
    }

    @Test
    void updatePassword() {
    }
}