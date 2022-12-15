package com.portable.server.manager.impl.prod;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.cache.CacheKvHelper;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.UserManager;
import com.portable.server.mapper.UserRepo;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.repo.UserDataRepo;
import com.portable.server.type.AccountType;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class UserManagerImpl implements UserManager {

    @Resource
    private UserRepo userRepo;

    @Resource
    private UserDataRepo userDataRepo;

    @Resource(name = "userCacheKvHelper")
    private CacheKvHelper<Long> userCacheKvHelper;

    @Resource(name = "userHandleCacheKvHelper")
    private CacheKvHelper<String> userHandleCacheKvHelper;

    @Override
    public Optional<User> getAccountByHandle(String handle) {
        if (handle == null) {
            return Optional.empty();
        }
        Optional<Long> userId = userHandleCacheKvHelper.get(handle, Long.class);
        if (userId.isPresent()) {
            return getAccountById(userId.get());
        }
        User user = userRepo.selectAccountByHandle(handle);
        if (user != null) {
            userCacheKvHelper.set(user.getId(), user);
            userHandleCacheKvHelper.set(user.getHandle(), user.getId());
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<Long> changeHandleToUserId(String handle) {
        if (handle == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userHandleCacheKvHelper.get(handle, Long.class)
                .orElseGet(() -> {
                    User user = userRepo.selectAccountByHandle(handle);
                    if (user == null) {
                        return null;
                    }
                    userHandleCacheKvHelper.set(handle, user.getId());
                    return user.getId();
                }));
    }

    @Override
    public Set<Long> changeHandleToUserId(Collection<String> handleList) {
        // TODO: 尝试增加专门的方法，redis 的 mget 效率很高
        if (Objects.isNull(handleList)) {
            return new HashSet<>();
        }
        return handleList.stream()
                .parallel()
                .map(s -> changeHandleToUserId(s).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<User> getAccountById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        User user = userCacheKvHelper.get(id, User.class).orElseGet(() -> userRepo.selectAccountById(id));
        if (Objects.nonNull(user)) {
            userCacheKvHelper.set(id, user);
            userHandleCacheKvHelper.set(user.getHandle(), id);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public void insertAccount(User user) {
        userRepo.insertAccount(user);
    }

    @Override
    public void updateHandle(Long id, String handle) {
        userRepo.updateHandle(id, handle);
        userCacheKvHelper.delete(id);
        userHandleCacheKvHelper.set(handle, id);
    }

    @Override
    public void updatePassword(Long id, String password) {
        userRepo.updatePassword(id, password);
        userCacheKvHelper.delete(id);
    }

    @Override
    public void updateUserType(Long id, AccountType accountType) {
        userRepo.updateUserType(id, accountType);
        userCacheKvHelper.delete(id);
    }

    @NotNull
    @Override
    public NormalUserData getNormalUserDataById(String dataId) {
        return Optional.ofNullable(userDataRepo.getNormalUserDataById(dataId)).orElseThrow(PortableException.from("S-02-001"));
    }

    @NotNull
    @Override
    public BatchUserData getBatchUserDataById(String dataId) {
        return Optional.ofNullable(userDataRepo.getBatchUserDataById(dataId)).orElseThrow(PortableException.from("S-02-001"));
    }

    @Override
    public void insertUserData(BaseUserData baseUserData) {
        userDataRepo.insertUserData(baseUserData);
    }

    @Override
    public void updateUserData(BaseUserData baseUserData) {
        userDataRepo.saveUserData(baseUserData);
    }
}
