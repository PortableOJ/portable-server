package com.portable.server.manager.impl;

import com.portable.server.manager.UserManager;
import com.portable.server.mapper.UserMapper;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author shiroha
 */
@Component
public class UserManagerImpl implements UserManager {

    @Resource
    private UserMapper userMapper;

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
    public User getAccountByHandle(String handle) {
        return userMapper.selectAccountByHandle(handle);
    }

    @Override
    public Stream<Long> changeUserHandleToUserId(Collection<String> handleList) {
        return handleList.stream()
                .map(s -> {
                    User user = userMapper.selectAccountByHandle(s);
                    return user == null ? null : user.getId();
                });
    }

    @Override
    public User getAccountById(Long id) {
        return userMapper.selectAccountById(id);
    }

    @Override
    public void insertAccount(User user) {
        userMapper.insertAccount(user);
    }

    @Override
    public void updateHandle(Long id, String handle) {
        userMapper.updateHandle(id, handle);
    }

    @Override
    public void updatePassword(Long id, String password) {
        userMapper.updatePassword(id, password);
    }
}
