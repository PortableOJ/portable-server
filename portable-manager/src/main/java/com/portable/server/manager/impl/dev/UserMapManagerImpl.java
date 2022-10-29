package com.portable.server.manager.impl.dev;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.helper.MemProtractedHelper;
import com.portable.server.manager.UserManager;
import com.portable.server.model.BaseEntity;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class UserMapManagerImpl implements UserManager {

    @Resource
    private MemProtractedHelper<User, Long> userMemProtractedHelper;

    @Resource
    private MemProtractedHelper<BaseUserData, String> userDataMemProtractedHelper;

    @Override
    public Optional<User> getAccountByHandle(String handle) {
        return userMemProtractedHelper.searchFirst(user -> Objects.equals(user.getHandle(), handle));
    }

    @Override
    public Optional<Long> changeHandleToUserId(String handle) {
        Optional<User> user = this.getAccountByHandle(handle);
        return user.map(BaseEntity::getId);
    }

    @Override
    public Set<Long> changeHandleToUserId(Collection<String> handleList) {
        List<User> userList = userMemProtractedHelper.searchList(user -> handleList.contains(user.getHandle()));
        return userList.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<User> getAccountById(Long id) {
        return userMemProtractedHelper.getDataById(id);
    }

    @Override
    public void insertAccount(User user) {
        userMemProtractedHelper.insert(user, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateHandle(Long id, String handle) {
        userMemProtractedHelper.updateByFunction(id, user -> user.setHandle(handle));
    }

    @Override
    public void updatePassword(Long id, String password) {
        userMemProtractedHelper.updateByFunction(id, user -> user.setPassword(password));
    }

    @Override
    public void updateUserType(Long id, AccountType accountType) {
        userMemProtractedHelper.updateByFunction(id, user -> user.setType(accountType));
    }

    @NotNull
    @Override
    public NormalUserData getNormalUserDataById(String dataId) {
        return (NormalUserData) userDataMemProtractedHelper.getDataById(dataId).orElseThrow(PortableException.from("S-02-001"));
    }

    @Override
    public @NotNull BatchUserData getBatchUserDataById(String dataId) {
        return (BatchUserData) userDataMemProtractedHelper.getDataById(dataId).orElseThrow(PortableException.from("S-02-001"));
    }

    @Override
    public void insertUserData(BaseUserData baseUserData) {
        userDataMemProtractedHelper.insert(baseUserData, BasicTranslateUtils::reString);
    }

    @Override
    public void updateUserData(BaseUserData baseUserData) {
        userDataMemProtractedHelper.updateById(baseUserData);
    }
}
