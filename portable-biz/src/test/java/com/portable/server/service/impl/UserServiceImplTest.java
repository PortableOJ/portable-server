package com.portable.server.service.impl;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.UserManagerImpl;
import com.portable.server.manager.impl.NormalUserManagerImpl;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.response.user.UserBasicInfoResponse;
import com.portable.server.model.user.User;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.type.AccountType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import com.portable.server.util.UserContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Objects;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserManagerImpl accountManager;

    @Mock
    private NormalUserManagerImpl normalUserManager;

    @Mock
    private BCryptEncoder bCryptEncoder;

    private static User user;
    private static NormalUserData normalUserData;

    @BeforeClass
    public static void init() {
        user = User.builder()
                .id(1L)
                .dataId("123")
                .handle("name")
                .password("abc")
                .type(AccountType.NORMAL)
                .build();
        normalUserData = NormalUserData.builder()
                ._id("dataId")
                .organization(OrganizationType.STUDENT)
                .permissionTypeSet(new HashSet<>())
                .build();
    }

    @Test
    public void loginTestNoAccount() {
        Mockito.when(accountManager.getAccountByHandle(Mockito.any())).thenReturn(null);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle("name");

        try {
            userService.login(loginRequest);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("A-01-001", e.getCode());
        }
    }

    @Test
    public void loginTestPasswordFail() {
        Mockito.when(accountManager.getAccountByHandle(Mockito.any())).thenReturn(user);
        Mockito.when(bCryptEncoder.match(Mockito.any(), Mockito.any())).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle("name");
        loginRequest.setPassword("pwd");

        try {
            userService.login(loginRequest);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("A-01-002", e.getCode());
        }
    }

    @Test
    public void loginTestNoData() {
        Mockito.when(accountManager.getAccountByHandle(Mockito.any())).thenReturn(user);
        Mockito.when(bCryptEncoder.match(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(null);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle("name");
        loginRequest.setPassword("pwd");

        try {
            userService.login(loginRequest);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("S-02-001", e.getCode());
        }
    }

    @Test
    public void loginTestNormalUserDataSuccess() {
        Mockito.when(accountManager.getAccountByHandle(Mockito.any())).thenReturn(user);
        Mockito.when(bCryptEncoder.match(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setHandle("name");
        loginRequest.setPassword("pwd");

        UserBasicInfoResponse userBasicInfoResponse = null;
        try {
            userBasicInfoResponse = userService.login(loginRequest);
        } catch (PortableException e) {
            assert false;
        }
        assert userBasicInfoResponse != null;
        assert userBasicInfoResponse instanceof NormalUserInfoResponse;
        assert normalUserData.getPermissionTypeSet().equals(((NormalUserInfoResponse) userBasicInfoResponse).getPermissionTypeSet());
    }

    @Test
    public void registerTestSameHandle() {
        Mockito.when(accountManager.getAccountByHandle(Mockito.any())).thenReturn(user);
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHandle("name");
        registerRequest.setPassword("pwd");

        try {
            userService.register(registerRequest);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("A-01-003", e.getCode());
        }
    }

    @Test
    public void registerTestSuccess() {
        Mockito.when(accountManager.getAccountByHandle(Mockito.any())).thenReturn(null);
        Mockito.when(normalUserManager.newUserData()).thenCallRealMethod();
        Mockito.doAnswer(invocationOnMock -> {
            NormalUserData normalUserData = invocationOnMock.getArgument(0, NormalUserData.class);
            normalUserData.set_id("dataId");
            return normalUserData;
        }).when(normalUserManager).insertNormalUserData(Mockito.any());
        Mockito.when(accountManager.newNormalAccount()).thenCallRealMethod();
        Mockito.when(bCryptEncoder.encoder(Mockito.any())).thenReturn("pwd");
        Mockito.doAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0, User.class);
            user.setId(1L);
            return 1;
        }).when(accountManager).insertAccount(Mockito.any());

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHandle("name");
        registerRequest.setPassword("pwd");

        NormalUserInfoResponse normalUserInfoResponse = null;
        try {
            normalUserInfoResponse = userService.register(registerRequest);
        } catch (PortableException e) {
            assert false;
        }

        assert normalUserInfoResponse != null;
        assert registerRequest.getHandle().equals(normalUserInfoResponse.getHandle());
    }

    @Test
    public void changeOrganizationTestNotDominateUser() {
        UserContext.ctx().setOrganization(OrganizationType.ACMER);
        Mockito.when(accountManager.getAccountById(Mockito.any())).thenReturn(user);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        try {
            userService.changeOrganization(1L, OrganizationType.TEACHER);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("A-03-001", e.getCode());
        }
    }

    @Test
    public void changeOrganizationTestNotDominateOrganization() {
        UserContext.ctx().setOrganization(OrganizationType.HONOR);
        Mockito.when(accountManager.getAccountById(Mockito.any())).thenReturn(user);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        try {
            userService.changeOrganization(1L, OrganizationType.BOSS);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("A-03-002", e.getCode());
        }
    }

    @Test
    public void changeOrganizationTestSuccess() {
        UserContext.ctx().setOrganization(OrganizationType.BOSS);
        Mockito.when(accountManager.getAccountById(Mockito.any())).thenReturn(user);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        try {
            userService.changeOrganization(1L, OrganizationType.HONOR);
        } catch (PortableException e) {
            assert false;
        }
    }

    @Test
    public void addPermissionTestNotDominateUser() {
        UserContext.ctx().setOrganization(OrganizationType.ACMER);
        UserContext.ctx().setPermissionTypeSet(new HashSet<>());
        Mockito.when(accountManager.getAccountById(Mockito.any())).thenReturn(user);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        try {
            userService.addPermission(1L, PermissionType.CREATE_AND_EDIT_PROBLEM);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("A-02-007", e.getCode());
        }
    }

    @Test
    public void addPermissionTestSuccess() {
        UserContext.ctx().setOrganization(OrganizationType.ACMER);
        UserContext.ctx().setPermissionTypeSet(new HashSet<PermissionType>() {{
            add(PermissionType.GRANT);
            add(PermissionType.CREATE_AND_EDIT_PROBLEM);
        }});
        Mockito.when(accountManager.getAccountById(Mockito.any())).thenReturn(user);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        try {
            userService.addPermission(1L, PermissionType.CREATE_AND_EDIT_PROBLEM);
        } catch (PortableException e) {
            assert false;
        }
    }

    @Test
    public void removePermissionTestNotDominateUser() {
        UserContext.ctx().setOrganization(OrganizationType.ACMER);
        UserContext.ctx().setPermissionTypeSet(new HashSet<>());
        Mockito.when(accountManager.getAccountById(Mockito.any())).thenReturn(user);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        try {
            userService.removePermission(1L, PermissionType.CREATE_AND_EDIT_PROBLEM);
            assert false;
        } catch (PortableException e) {
            assert Objects.equals("A-02-007", e.getCode());
        }
    }

    @Test
    public void removePermissionTestSuccess() {
        UserContext.ctx().setOrganization(OrganizationType.ACMER);
        UserContext.ctx().setPermissionTypeSet(new HashSet<PermissionType>() {{
            add(PermissionType.GRANT);
            add(PermissionType.CREATE_AND_EDIT_PROBLEM);
        }});
        Mockito.when(accountManager.getAccountById(Mockito.any())).thenReturn(user);
        Mockito.when(normalUserManager.getUserDataById(Mockito.any())).thenReturn(normalUserData);

        try {
            userService.removePermission(1L, PermissionType.CREATE_AND_EDIT_PROBLEM);
        } catch (PortableException e) {
            assert false;
        }
    }
}
