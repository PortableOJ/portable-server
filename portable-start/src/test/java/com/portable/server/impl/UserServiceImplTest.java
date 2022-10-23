package com.portable.server.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.BatchManagerImpl;
import com.portable.server.manager.impl.GridFsManagerImpl;
import com.portable.server.manager.impl.UserDataManagerImpl;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.request.user.UpdatePasswordRequest;
import com.portable.server.model.response.user.BaseUserInfoResponse;
import com.portable.server.model.response.user.BatchAdminUserInfoResponse;
import com.portable.server.model.response.user.BatchUserInfoResponse;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.service.impl.UserServiceImpl;
import com.portable.server.test.MockedValueMaker;
import com.portable.server.type.AccountType;
import com.portable.server.type.BatchStatusType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import com.portable.server.util.ImageUtils;
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

    private static final String MOCKED_ROOT_NAME = MockedValueMaker.mString();
    private static final String MOCKED_ROOT_PASSWORD = MockedValueMaker.mString();
    private static final String MOCKED_ROOT_PASSWORD_ENCODED = MockedValueMaker.mString();
    private static final String MOCKED_MONGO_ID = MockedValueMaker.mString();
    private static final String MOCKED_HANDLE = MockedValueMaker.mString();
    private static final String MOCKED_INPUT_PASSWORD = MockedValueMaker.mString();
    private static final String MOCKED_IP = MockedValueMaker.mString();
    private static final String MOCKED_OTHER_IP = MockedValueMaker.mString();
    private static final Long MOCKED_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_BATCH_ID = MockedValueMaker.mLong();
    private static final Long MOCKED_CONTEST_ID = MockedValueMaker.mLong();

    private User user;
    private Batch batch;
    private UserContext userContext;
    private NormalUserData normalUserData;
    private BatchUserData batchUserData;

    private MockedStatic<BCryptEncoder> bCryptEncoderMockedStatic;
    private MockedStatic<UserContext> userContextMockedStatic;
    private MockedStatic<ImageUtils> imageUtilsMockedStatic;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(MOCKED_ID)
                .dataId(MOCKED_MONGO_ID)
                .handle(MOCKED_HANDLE)
                .password(MOCKED_ROOT_PASSWORD_ENCODED)
                .type(AccountType.NORMAL)
                .build();
        normalUserData = NormalUserData.builder()
                .id(MOCKED_MONGO_ID)
                .build();
        batchUserData = BatchUserData.builder()
                .id(MOCKED_MONGO_ID)
                .batchId(MOCKED_BATCH_ID)
                .build();
        userContext = UserContext.getNullUser();
        batch = Batch.builder()
                .status(BatchStatusType.NORMAL)
                .ipLock(true)
                .contestId(MOCKED_CONTEST_ID)
                .build();

        bCryptEncoderMockedStatic = Mockito.mockStatic(BCryptEncoder.class);
        userContextMockedStatic = Mockito.mockStatic(UserContext.class);
        imageUtilsMockedStatic = Mockito.mockStatic(ImageUtils.class);
    }

    @AfterEach
    void tearDown() {
        bCryptEncoderMockedStatic.close();
        userContextMockedStatic.close();
        imageUtilsMockedStatic.close();
    }

    @Test
    void testInitWithNoRoot() {
        ReflectionTestUtils.setField(userService, "rootName", MOCKED_ROOT_NAME);
        ReflectionTestUtils.setField(userService, "rootPassword", MOCKED_ROOT_PASSWORD);

        Mockito.when(userManager.getAccountByHandle(MOCKED_ROOT_NAME)).thenReturn(Optional.empty());
        Mockito.when(userDataManager.newNormalUserData()).thenAnswer(invocationOnMock -> normalUserData);
        Mockito.when(userManager.newNormalAccount()).thenAnswer(invocationOnMock -> user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.encoder(MOCKED_ROOT_PASSWORD)).thenReturn(MOCKED_ROOT_PASSWORD_ENCODED);

        Mockito.doAnswer(invocationOnMock -> {
            NormalUserData argument = invocationOnMock.getArgument(0);
            argument.setId(MOCKED_MONGO_ID);
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
        Assertions.assertEquals(normalUserData.getId(), userCP.getDataId());
        /// endregion
    }

    @Test
    void testInitWithRoot() {
        user.setDataId(MOCKED_MONGO_ID);

        ReflectionTestUtils.setField(userService, "rootName", MOCKED_ROOT_NAME);

        Mockito.when(userManager.getAccountByHandle(MOCKED_ROOT_NAME)).thenReturn(Optional.of(user));
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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());

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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

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
    void testLoginWithNormalUserAndSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle(MOCKED_HANDLE);
        loginRequest.setPassword(MOCKED_INPUT_PASSWORD);

        user.setType(AccountType.NORMAL);
        normalUserData.setPermissionTypeSet(new HashSet<PermissionType>() {{
            add(PermissionType.MANAGER_JUDGE);
        }});

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);

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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

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
    void testLoginWithBatchUserNoIPLockAndSuccess() {
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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

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
    void testLoginWithBatchUserIpLockNoIPAndSuccess() {
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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

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
    void testLoginWithBatchUserIpLockOneIPAndSuccess() {
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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));

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

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));

        try {
            userService.register(registerRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-003", e.getCode());
        }
    }

    @Test
    void testRegisterWithSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHandle(MOCKED_HANDLE);
        registerRequest.setPassword(MOCKED_INPUT_PASSWORD);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());
        Mockito.when(userDataManager.newNormalUserData()).thenAnswer(invocationOnMock -> normalUserData);
        Mockito.when(userManager.newNormalAccount()).thenAnswer(invocationOnMock -> user);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.encoder(MOCKED_INPUT_PASSWORD)).thenReturn(MOCKED_ROOT_PASSWORD_ENCODED);

        Mockito.doAnswer(invocationOnMock -> {
            NormalUserData userData = invocationOnMock.getArgument(0);
            userData.setId(MOCKED_MONGO_ID);
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
        Assertions.assertEquals(userCP.getDataId(), normalUserDataCP.getId());

        /// endregion
    }

    @Test
    void testCheckWithNotLogin() {
        userContext.setId(null);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        BaseUserInfoResponse retVal = userService.check();

        Assertions.assertNull(retVal);
    }

    @Test
    void testCheckWithLogin() {
        user.setType(AccountType.NORMAL);
        userContext.setId(MOCKED_ID);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(userManager.getAccountById(MOCKED_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);

        BaseUserInfoResponse retVal = userService.check();

        Assertions.assertEquals(MOCKED_HANDLE, retVal.getHandle());
    }

    @Test
    void testGetUserInfoWithNoUser() {
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());

        try {
            userService.getUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testGetUserInfoWithLockNormalNoUserData() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.LOCKED_NORMAL);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.getUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testGetUserInfoWithLockNormalSuccess() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.LOCKED_NORMAL);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);

        BaseUserInfoResponse baseUserInfoResponse = userService.getUserInfo(MOCKED_HANDLE);

        /// region 校验用户的数据信息

        NormalUserInfoResponse normalUserInfoResponse = (NormalUserInfoResponse) baseUserInfoResponse;
        Assertions.assertEquals(user.getHandle(), normalUserInfoResponse.getHandle());
        Assertions.assertEquals(AccountType.LOCKED_NORMAL, user.getType());
        Assertions.assertEquals(normalUserData.getAvatar(), normalUserInfoResponse.getAvatar());
        Assertions.assertEquals(normalUserData.getOrganization(), normalUserInfoResponse.getOrganizationType());
        Assertions.assertEquals(normalUserData.getPermissionTypeSet(), normalUserInfoResponse.getPermissionTypeSet());

        /// endregion
    }

    @Test
    void testGetUserInfoWithNormalNoUserData() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.NORMAL);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.getUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testGetUserInfoWithNormalSuccess() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.NORMAL);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);

        BaseUserInfoResponse baseUserInfoResponse = userService.getUserInfo(MOCKED_HANDLE);

        /// region 校验用户的数据信息

        NormalUserInfoResponse normalUserInfoResponse = (NormalUserInfoResponse) baseUserInfoResponse;
        Assertions.assertEquals(user.getHandle(), normalUserInfoResponse.getHandle());
        Assertions.assertEquals(AccountType.NORMAL, user.getType());
        Assertions.assertEquals(normalUserData.getAvatar(), normalUserInfoResponse.getAvatar());
        Assertions.assertEquals(normalUserData.getOrganization(), normalUserInfoResponse.getOrganizationType());
        Assertions.assertEquals(normalUserData.getPermissionTypeSet(), normalUserInfoResponse.getPermissionTypeSet());

        /// endregion
    }

    @Test
    void testGetUserInfoWithBatchNoUserData() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.getUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testGetUserInfoWithBatchNoBatch() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.empty());

        try {
            userService.getUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-006", e.getCode());
        }
    }

    @Test
    void testGetUserInfoWithBatch() {
        Batch batch = Batch.builder()
                .contestId(MOCKED_CONTEST_ID)
                .build();
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        BaseUserInfoResponse baseUserInfoResponse = userService.getUserInfo(MOCKED_HANDLE);

        BatchUserInfoResponse batchUserInfoResponse = (BatchUserInfoResponse) baseUserInfoResponse;

        Assertions.assertEquals(user.getHandle(), batchUserInfoResponse.getHandle());
        Assertions.assertEquals(AccountType.BATCH, batchUserInfoResponse.getType());
        Assertions.assertEquals(MOCKED_CONTEST_ID, batchUserInfoResponse.getContestId());
    }

    @Test
    void testGetBatchUserInfoWithNoUser() {
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());

        try {
            userService.getBatchUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testGetBatchUserInfoWithNotBatch() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.NORMAL);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));

        try {
            userService.getBatchUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-014", e.getCode());
        }
    }

    @Test
    void testGetBatchUserInfoWithNoUserData() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.getBatchUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testGetBatchUserInfoWithNoBatch() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);
        batchUserData.setBatchId(MOCKED_BATCH_ID);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.empty());

        try {
            userService.getBatchUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-006", e.getCode());
        }
    }

    @Test
    void testGetBatchUserInfoWithNotMine() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);
        batchUserData.setBatchId(MOCKED_BATCH_ID);
        batch.setOwner(MockedValueMaker.mLong());
        userContext.setId(MOCKED_ID);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        try {
            userService.getBatchUserInfo(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-002", e.getCode());
        }
    }

    @Test
    void testGetBatchUserInfoWithSuccess() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);
        batchUserData.setBatchId(MOCKED_BATCH_ID);
        batch.setOwner(MOCKED_ID);
        batch.setContestId(MOCKED_CONTEST_ID);
        userContext.setId(MOCKED_ID);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        BatchAdminUserInfoResponse batchAdminUserInfoResponse = userService.getBatchUserInfo(MOCKED_HANDLE);

        /// region 校验返回值是否正确

        Assertions.assertEquals(batch.getContestId(), batchAdminUserInfoResponse.getContestId());
        Assertions.assertEquals(user.getHandle(), batchAdminUserInfoResponse.getHandle());
        Assertions.assertEquals(AccountType.BATCH, batchAdminUserInfoResponse.getType());

        /// endregion

    }

    @Test
    void testChangeOrganizationWithNoUser() {
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());

        try {
            userService.changeOrganization(MOCKED_HANDLE, OrganizationType.STUDENT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testChangeOrganizationWithNotNormal() {
        user.setType(AccountType.BATCH);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));

        try {
            userService.changeOrganization(MOCKED_HANDLE, OrganizationType.STUDENT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-003", e.getCode());
        }
    }

    @Test
    void testChangeOrganizationWithNoUserData() {
        user.setType(AccountType.LOCKED_NORMAL);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.changeOrganization(MOCKED_HANDLE, OrganizationType.STUDENT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testChangeOrganizationWithNotDominate() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.TEACHER);
        normalUserData.setOrganization(OrganizationType.ACMER);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        try {
            userService.changeOrganization(MOCKED_HANDLE, OrganizationType.STUDENT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-03-001", e.getCode());
        }
    }

    @Test
    void testChangeOrganizationWithSuccess() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.ACMER);
        normalUserData.setOrganization(OrganizationType.STUDENT);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        userService.changeOrganization(MOCKED_HANDLE, OrganizationType.SPECIAL_STUDENT);

        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).updateUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_MONGO_ID, normalUserData.getId());
        Assertions.assertEquals(OrganizationType.SPECIAL_STUDENT, normalUserDataCP.getOrganization());
    }

    @Test
    void testAddPermissionWithNoUser() {
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());

        try {
            userService.addPermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testAddPermissionWithNotNormal() {
        user.setType(AccountType.BATCH);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));

        try {
            userService.addPermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-003", e.getCode());
        }
    }

    @Test
    void testAddPermissionWithNoUserData() {
        user.setType(AccountType.LOCKED_NORMAL);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.addPermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testAddPermissionWithNotDominate() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.TEACHER);
        normalUserData.setOrganization(OrganizationType.ACMER);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        try {
            userService.addPermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-03-001", e.getCode());
        }
    }

    @Test
    void testAddPermissionWithNoPermission() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.ACMER);
        userContext.getPermissionTypeSet().add(PermissionType.VIEW_SOLUTION_MESSAGE);
        normalUserData.setOrganization(OrganizationType.STUDENT);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        try {
            userService.addPermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-007", e.getCode());
        }
    }

    @Test
    void testAddPermissionWithSuccess() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.ACMER);
        userContext.getPermissionTypeSet().add(PermissionType.GRANT);
        normalUserData.setOrganization(OrganizationType.STUDENT);
        normalUserData.setPermissionTypeSet(new HashSet<>());
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        userService.addPermission(MOCKED_HANDLE, PermissionType.GRANT);

        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).updateUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_MONGO_ID, normalUserData.getId());
        Assertions.assertTrue(normalUserDataCP.getPermissionTypeSet().contains(PermissionType.GRANT));
    }

    @Test
    void testRemovePermissionWithNoUser() {
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());

        try {
            userService.removePermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testRemovePermissionWithNotNormal() {
        user.setType(AccountType.BATCH);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));

        try {
            userService.removePermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-003", e.getCode());
        }
    }

    @Test
    void testRemovePermissionWithNoUserData() {
        user.setType(AccountType.LOCKED_NORMAL);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.removePermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testRemovePermissionWithNotDominate() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.TEACHER);
        normalUserData.setOrganization(OrganizationType.ACMER);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        try {
            userService.removePermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-03-001", e.getCode());
        }
    }

    @Test
    void testRemovePermissionWithNoPermission() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.ACMER);
        userContext.getPermissionTypeSet().add(PermissionType.VIEW_SOLUTION_MESSAGE);
        normalUserData.setOrganization(OrganizationType.STUDENT);
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        try {
            userService.removePermission(MOCKED_HANDLE, PermissionType.GRANT);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-007", e.getCode());
        }
    }

    @Test
    void testRemovePermissionWithSuccess() {
        user.setType(AccountType.LOCKED_NORMAL);
        userContext.setOrganization(OrganizationType.ACMER);
        userContext.getPermissionTypeSet().add(PermissionType.GRANT);
        normalUserData.setOrganization(OrganizationType.STUDENT);
        normalUserData.setPermissionTypeSet(new HashSet<PermissionType>() {{
            add(PermissionType.GRANT);
        }});
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        userService.removePermission(MOCKED_HANDLE, PermissionType.GRANT);

        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).updateUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_MONGO_ID, normalUserData.getId());
        Assertions.assertFalse(normalUserDataCP.getPermissionTypeSet().contains(PermissionType.GRANT));
    }

    @Test
    void testUploadAvatarWithNotNormal() {
        userContext.setType(AccountType.BATCH);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        InputStream inputStream = Mockito.mock(InputStream.class);

        try {
            userService.uploadAvatar(inputStream, MOCKED_HANDLE, MOCKED_MONGO_ID, 1, 2, 3, 4);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-02-008", e.getCode());
        }
    }

    @Test
    void testUploadAvatarWithNoUserData() {
        userContext.setType(AccountType.NORMAL);
        userContext.setDataId(MOCKED_MONGO_ID);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        InputStream inputStream = Mockito.mock(InputStream.class);

        try {
            userService.uploadAvatar(inputStream, MOCKED_HANDLE, MOCKED_MONGO_ID, 1, 2, 3, 4);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testUploadAvatarWithSuccess() {
        userContext.setType(AccountType.NORMAL);
        userContext.setDataId(MOCKED_MONGO_ID);
        normalUserData.setAvatar(MOCKED_IP);

        InputStream inputStream = Mockito.mock(InputStream.class);
        InputStream cutInputStream = Mockito.mock(InputStream.class);

        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(userDataManager.getNormalUserDataById(MOCKED_MONGO_ID)).thenReturn(normalUserData);
        imageUtilsMockedStatic.when(() -> ImageUtils.cut(inputStream, 1, 2, 3, 4)).thenReturn(cutInputStream);
        Mockito.when(gridFsManager.uploadAvatar(MOCKED_IP, cutInputStream, MOCKED_HANDLE, MOCKED_MONGO_ID)).thenReturn(MOCKED_OTHER_IP);

        String fileId = userService.uploadAvatar(inputStream, MOCKED_HANDLE, MOCKED_MONGO_ID, 1, 2, 3, 4);

        Assertions.assertEquals(MOCKED_OTHER_IP, fileId);

        /// region 校验头像是否写入了图库

        ArgumentCaptor<NormalUserData> normalUserDataArgumentCaptor = ArgumentCaptor.forClass(NormalUserData.class);
        Mockito.verify(userDataManager).updateUserData(normalUserDataArgumentCaptor.capture());
        NormalUserData normalUserDataCP = normalUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(MOCKED_OTHER_IP, normalUserDataCP.getAvatar());

        /// endregion
    }

    @Test
    void testUpdatePasswordWithNotNormal() {
        userContext.setType(AccountType.BATCH);

        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .oldPassword(MOCKED_INPUT_PASSWORD)
                .newPassword(MOCKED_ROOT_PASSWORD)
                .build();

        try {
            userService.updatePassword(updatePasswordRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-011", e.getCode());
        }
    }

    @Test
    void testUpdatePasswordWithNoUser() {
        userContext.setType(AccountType.NORMAL);
        userContext.setId(MOCKED_ID);

        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(userManager.getAccountById(MOCKED_ID)).thenReturn(Optional.empty());

        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .oldPassword(MOCKED_INPUT_PASSWORD)
                .newPassword(MOCKED_ROOT_PASSWORD)
                .build();

        try {
            userService.updatePassword(updatePasswordRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testUpdatePasswordWithNotMatch() {
        userContext.setType(AccountType.NORMAL);
        userContext.setId(MOCKED_ID);
        user.setPassword(MOCKED_ROOT_PASSWORD_ENCODED);

        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(userManager.getAccountById(MOCKED_ID)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(false);

        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .oldPassword(MOCKED_INPUT_PASSWORD)
                .newPassword(MOCKED_ROOT_PASSWORD)
                .build();

        try {
            userService.updatePassword(updatePasswordRequest);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-002", e.getCode());
        }
    }

    @Test
    void testUpdatePasswordWithNotSuccess() {
        userContext.setType(AccountType.NORMAL);
        userContext.setId(MOCKED_ID);
        user.setPassword(MOCKED_ROOT_PASSWORD_ENCODED);

        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        Mockito.when(userManager.getAccountById(MOCKED_ID)).thenReturn(Optional.of(user));
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.match(MOCKED_INPUT_PASSWORD, MOCKED_ROOT_PASSWORD_ENCODED)).thenReturn(true);
        bCryptEncoderMockedStatic.when(() -> BCryptEncoder.encoder(MOCKED_ROOT_PASSWORD)).thenReturn(MOCKED_ROOT_PASSWORD_ENCODED);

        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .oldPassword(MOCKED_INPUT_PASSWORD)
                .newPassword(MOCKED_ROOT_PASSWORD)
                .build();

        userService.updatePassword(updatePasswordRequest);

        /// region 校验写入数据库的密码是否正确

        Mockito.verify(userManager).updatePassword(MOCKED_ID, MOCKED_ROOT_PASSWORD_ENCODED);

        /// endregion
    }

    @Test
    void testclearBatchUserIpListWithNoUser() {
        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.empty());

        try {
            userService.clearBatchUserIpList(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-001", e.getCode());
        }
    }

    @Test
    void testclearBatchUserIpListWithNotBatch() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.NORMAL);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));

        try {
            userService.clearBatchUserIpList(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-01-014", e.getCode());
        }
    }

    @Test
    void testclearBatchUserIpListWithNoUserData() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenThrow(PortableException.of("S-02-001"));

        try {
            userService.clearBatchUserIpList(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("S-02-001", e.getCode());
        }
    }

    @Test
    void testclearBatchUserIpListWithNoBatch() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);
        batchUserData.setBatchId(MOCKED_BATCH_ID);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.empty());

        try {
            userService.clearBatchUserIpList(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-006", e.getCode());
        }
    }

    @Test
    void testclearBatchUserIpListWithNotMine() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);
        batchUserData.setBatchId(MOCKED_BATCH_ID);
        batch.setOwner(MockedValueMaker.mLong());
        userContext.setId(MOCKED_ID);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        try {
            userService.clearBatchUserIpList(MOCKED_HANDLE);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-10-002", e.getCode());
        }
    }

    @Test
    void testclearBatchUserIpListWithSuccess() {
        user.setDataId(MOCKED_MONGO_ID);
        user.setType(AccountType.BATCH);
        batchUserData.setBatchId(MOCKED_BATCH_ID);
        batchUserData.setIpList(new ArrayList<BatchUserData.IpRecord>() {{
            add(BatchUserData.IpRecord.builder()
                    .ip(MOCKED_IP)
                    .date(new Date())
                    .build());
        }});
        batch.setOwner(MOCKED_ID);
        batch.setContestId(MOCKED_CONTEST_ID);
        userContext.setId(MOCKED_ID);

        Mockito.when(userManager.getAccountByHandle(MOCKED_HANDLE)).thenReturn(Optional.of(user));
        Mockito.when(userDataManager.getBatchUserDataById(MOCKED_MONGO_ID)).thenReturn(batchUserData);
        Mockito.when(batchManager.selectBatchById(MOCKED_BATCH_ID)).thenReturn(Optional.of(batch));
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);

        userService.clearBatchUserIpList(MOCKED_HANDLE);

        /// region 校验写入数据库的信息是否存在 IP 列表

        ArgumentCaptor<BatchUserData> batchUserDataArgumentCaptor = ArgumentCaptor.forClass(BatchUserData.class);
        Mockito.verify(userDataManager).updateUserData(batchUserDataArgumentCaptor.capture());
        BatchUserData batchUserDataCP = batchUserDataArgumentCaptor.getValue();
        Assertions.assertEquals(new ArrayList<>(), batchUserDataCP.getIpList());

        /// endregion

    }
}