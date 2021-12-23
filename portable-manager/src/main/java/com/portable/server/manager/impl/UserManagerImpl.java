package com.portable.server.manager.impl;

import com.portable.server.manager.UserManager;
import com.portable.server.mapper.UserMapper;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@Component
public class UserManagerImpl implements UserManager {

    @Resource
    private UserMapper accountMapper;

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
        return accountMapper.selectAccountByHandle(handle);
    }

    @Override
    public User getAccountById(Long id) {
        return accountMapper.selectAccountById(id);
    }

    @Override
    public Integer insertAccount(User user) {
        return accountMapper.insertAccount(user);
    }

    @Override
    public Integer updateHandle(Long id, String handle) {
        return accountMapper.updateHandle(id, handle);
    }

    @Override
    public Integer updatePassword(Long id, String password) {
        return accountMapper.updatePassword(id, password);
    }
}
