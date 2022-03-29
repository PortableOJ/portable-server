package com.portable.server.tool;

import com.portable.server.type.AccountType;
import com.portable.server.type.PermissionType;
import com.portable.server.util.UserContext;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 用于快速创建测试使用的用户上下文信息
 */
public class UserContextBuilder {

    private Long MOCKED_USER_ID;
    private UserContext userContext;
    private MockedStatic<UserContext> userContextMockedStatic;

    public void setup() {
        MOCKED_USER_ID = 1L;
        userContext = UserContext.getNullUser();
        userContextMockedStatic = Mockito.mockStatic(UserContext.class);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
    }

    public void tearDown() {
        userContextMockedStatic.close();
    }

    public void withNotLogin() {
    }

    public UserContextBuilder withNormalLoginIn() {
        userContext.setId(MOCKED_USER_ID);
        userContext.setType(AccountType.NORMAL);
        return this;
    }

    public UserContextBuilder withNormalLoginIn(Long id) {
        userContext.setId(id);
        userContext.setType(AccountType.NORMAL);
        return this;
    }

    public UserContextBuilder withBatchLoginIn() {
        userContext.setId(MOCKED_USER_ID);
        userContext.setType(AccountType.NORMAL);
        return this;
    }

    public UserContextBuilder withBatchLoginIn(Long id) {
        userContext.setId(id);
        userContext.setType(AccountType.NORMAL);
        return this;
    }

    public void withPermission(PermissionType... permission) {
        userContext.setPermissionTypeSet(Arrays.stream(permission).collect(Collectors.toSet()));
    }
}
