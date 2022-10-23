package com.portable.server.manager.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.helper.RedisValueHelper;
import com.portable.server.manager.UserManager;
import com.portable.server.mapper.UserMapper;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class UserManagerImpl implements UserManager {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisValueHelper redisValueHelper;

    /**
     * redis 缓存的 Key 前缀
     */
    private static final String REDIS_USER_ID_TO_DATA_PREFIX = "USER_CACHE";
    private static final Long REDIS_USER_ID_TO_DATA_TIME = 30L;
    private static final String REDIS_USER_HANDLE_TO_ID_PREFIX = "USER_HANDLE";
    private static final Long REDIS_USER_HANDLE_TO_ID_TIME = 30L;

    @Override
    public User newNormalAccount() {
        return User.builder()
                .id(null)
                .dataId(null)
                .handle(null)
                .password(null)
                .type(AccountType.NORMAL)
                .build();
    }

    @Override
    public User newBatchAccount() {
        return User.builder()
                .id(null)
                .dataId(null)
                .handle(null)
                .password(null)
                .type(AccountType.BATCH)
                .build();
    }

    @Override
    public Optional<User> getAccountByHandle(String handle) {
        if (handle == null) {
            return Optional.empty();
        }
        Optional<Long> userId = redisValueHelper.get(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, Long.class);
        if (userId.isPresent()) {
            return getAccountById(userId.get());
        }
        User user = userMapper.selectAccountByHandle(handle);
        if (user != null) {
            redisValueHelper.set(REDIS_USER_ID_TO_DATA_PREFIX, user.getId(), user, REDIS_USER_ID_TO_DATA_TIME);
            redisValueHelper.set(REDIS_USER_HANDLE_TO_ID_PREFIX, user.getHandle(), user.getId(), REDIS_USER_HANDLE_TO_ID_TIME);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<Long> changeHandleToUserId(String handle) {
        if (handle == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(redisValueHelper.get(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, Long.class)
                .orElseGet(() -> {
                    User user = userMapper.selectAccountByHandle(handle);
                    if (user == null) {
                        return null;
                    }
                    redisValueHelper.set(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, user.getId(), REDIS_USER_HANDLE_TO_ID_TIME);
                    return user.getId();
                }));
    }

    @Override
    public Set<Long> changeHandleToUserId(Collection<String> handleList) {
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
        User user = redisValueHelper.get(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class)
                .orElseGet(() -> userMapper.selectAccountById(id));
        if (Objects.nonNull(user)) {
            redisValueHelper.set(REDIS_USER_ID_TO_DATA_PREFIX, id, user, REDIS_USER_ID_TO_DATA_TIME);
            redisValueHelper.set(REDIS_USER_HANDLE_TO_ID_PREFIX, user.getHandle(), id, REDIS_USER_HANDLE_TO_ID_TIME);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public void insertAccount(User user) {
        userMapper.insertAccount(user);
    }

    @Override
    public void updateHandle(Long id, String handle) {
        userMapper.updateHandle(id, handle);
        redisValueHelper.getPeek(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class, REDIS_USER_ID_TO_DATA_TIME, user -> {
            user.setHandle(handle);
            redisValueHelper.set(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, id, REDIS_USER_HANDLE_TO_ID_TIME);
        });
    }

    @Override
    public void updatePassword(Long id, String password) {
        userMapper.updatePassword(id, password);
        redisValueHelper.getPeek(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class, REDIS_USER_ID_TO_DATA_TIME, user -> user.setPassword(password));
    }

    @Override
    public void updateUserType(Long id, AccountType accountType) {
        userMapper.updateUserType(id, accountType);
        redisValueHelper.getPeek(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class, REDIS_USER_ID_TO_DATA_TIME, user -> user.setType(accountType));
    }
}
