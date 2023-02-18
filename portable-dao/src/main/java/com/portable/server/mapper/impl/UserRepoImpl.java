package com.portable.server.mapper.impl;

import java.util.Objects;

import com.portable.server.mapper.UserRepo;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserRepoImpl extends BaseMemStructuredRepo<Long, User> implements UserRepo {

    @Override
    @Nullable
    public User selectAccountById(@NotNull Long id) {
        return super.getDataById(id);
    }

    @Override
    @Nullable
    public User selectAccountByHandle(@NotNull String handle) {
        return super.searchFirstAsc(user -> Objects.equals(user.getHandle(), handle));
    }

    @Override
    public void insertAccount(@NotNull User user) {
        super.insert(user, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateHandle(@NotNull Long id, @NotNull String handle) {
        super.updateByFunction(id, user -> user.setHandle(handle));
    }

    @Override
    public void updatePassword(@NotNull Long id, @NotNull String password) {
        super.updateByFunction(id, user -> user.setPassword(password));
    }

    @Override
    public void updateUserType(@NotNull Long id, @NotNull AccountType accountType) {
        super.updateByFunction(id, user -> user.setType(accountType));
    }
}
