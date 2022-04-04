package com.portable.server.manager.impl;

import com.portable.server.kit.RedisValueKit;
import com.portable.server.manager.UserManager;
import com.portable.server.mapper.UserMapper;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author shiroha
 */
@Component
public class UserManagerImpl implements UserManager {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisValueKit redisValueKit;

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
        Optional<Long> userId = redisValueKit.get(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, Long.class);
        if (userId.isPresent()) {
            return getAccountById(userId.get());
        }
        User user = userMapper.selectAccountByHandle(handle);
        if (user != null) {
            redisValueKit.set(REDIS_USER_ID_TO_DATA_PREFIX, user.getId(), user, REDIS_USER_ID_TO_DATA_TIME);
            redisValueKit.set(REDIS_USER_HANDLE_TO_ID_PREFIX, user.getHandle(), user.getId(), REDIS_USER_HANDLE_TO_ID_TIME);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<Long> changeHandleToUserId(String handle) {
        return Optional.ofNullable(redisValueKit.get(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, Long.class)
                .orElseGet(() -> {
                    User user = userMapper.selectAccountByHandle(handle);
                    if (user == null) {
                        return null;
                    }
                    redisValueKit.set(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, user.getId(), REDIS_USER_HANDLE_TO_ID_TIME);
                    return user.getId();
                }));
    }

    @Override
    public Stream<Long> changeUserHandleToUserId(Collection<String> handleList) {
        return handleList.stream()
                .parallel()
                .map(s -> changeHandleToUserId(s).orElse(null));
    }

    @Override
    public Optional<User> getAccountById(Long id) {
        return Optional.ofNullable(redisValueKit.get(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class)
                .orElseGet(() -> {
                    User user = userMapper.selectAccountById(id);
                    if (user != null) {
                        redisValueKit.set(REDIS_USER_ID_TO_DATA_PREFIX, id, user, REDIS_USER_ID_TO_DATA_TIME);
                        redisValueKit.set(REDIS_USER_HANDLE_TO_ID_PREFIX, user.getHandle(), id, REDIS_USER_HANDLE_TO_ID_TIME);
                    }
                    return user;
                }));
    }

    @Override
    public void insertAccount(User user) {
        userMapper.insertAccount(user);
    }

    @Override
    public void updateHandle(Long id, String handle) {
        userMapper.updateHandle(id, handle);
        Optional<User> userOptional = redisValueKit.get(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class);
        if (userOptional.isPresent()) {
            userOptional.get().setHandle(handle);
            redisValueKit.set(REDIS_USER_ID_TO_DATA_PREFIX, id, userOptional.get(), REDIS_USER_ID_TO_DATA_TIME);
            redisValueKit.set(REDIS_USER_HANDLE_TO_ID_PREFIX, handle, id, REDIS_USER_HANDLE_TO_ID_TIME);
        }
    }

    @Override
    public void updatePassword(Long id, String password) {
        userMapper.updatePassword(id, password);
        Optional<User> userOptional = redisValueKit.get(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class);
        if (userOptional.isPresent()) {
            userOptional.get().setPassword(password);
            redisValueKit.set(REDIS_USER_ID_TO_DATA_PREFIX, id, userOptional.get(), REDIS_USER_ID_TO_DATA_TIME);
        }
    }

    @Override
    public void updateUserType(Long id, AccountType accountType) {
        userMapper.updateUserType(id, accountType);
        Optional<User> userOptional = redisValueKit.get(REDIS_USER_ID_TO_DATA_PREFIX, id, User.class);
        if (userOptional.isPresent()) {
            userOptional.get().setType(accountType);
            redisValueKit.set(REDIS_USER_ID_TO_DATA_PREFIX, id, userOptional.get(), REDIS_USER_ID_TO_DATA_TIME);
        }
    }
}
