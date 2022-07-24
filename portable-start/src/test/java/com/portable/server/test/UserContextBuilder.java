package com.portable.server.test;

import com.portable.server.type.AccountType;
import com.portable.server.type.ContestVisitType;
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

    private UserContext userContext;
    private MockedStatic<UserContext> userContextMockedStatic;

    private UserContextLogined userContextLogined;

    public void setup() {
        userContext = UserContext.getNullUser();
        userContextMockedStatic = Mockito.mockStatic(UserContext.class);
        userContextMockedStatic.when(UserContext::ctx).thenReturn(userContext);
        userContextLogined = new UserContextLogined(userContext);
    }

    public void tearDown() {
        userContextMockedStatic.close();
    }

    public void withNotLogin() {
    }

    public UserContextLogined withNormalLoginIn() {
        userContext.setId(MockedValueMaker.mLong());
        userContext.setType(AccountType.NORMAL);
        return userContextLogined;
    }

    public UserContextLogined withNormalLoginIn(Long id) {
        userContext.setId(id);
        userContext.setType(AccountType.NORMAL);
        return userContextLogined;
    }

    public UserContextLogined withBatchLoginIn() {
        userContext.setId(MockedValueMaker.mLong());
        userContext.setType(AccountType.BATCH);
        return userContextLogined;
    }

    public UserContextLogined withBatchLoginIn(Long id) {
        userContext.setId(id);
        userContext.setType(AccountType.BATCH);
        return userContextLogined;
    }

    public static class UserContextLogined {

        private final UserContext userContext;

        private UserContextLogined(UserContext userContext) {
            this.userContext = userContext;
        }

        public void withDataId(String dataId) {
            userContext.setDataId(dataId);
        }

        public void withPermission(PermissionType... permission) {
            userContext.setPermissionTypeSet(Arrays.stream(permission).collect(Collectors.toSet()));
        }

        public void withHandle(String handle) {
            userContext.setHandle(handle);
        }

        public void withContestCache(Long contestId, ContestVisitType cache) {
            userContext.getContestVisitPermissionMap().put(contestId, cache);
        }

        public void withContestId(Long contestId) {
            userContext.setContestId(contestId);
        }
    }
}
