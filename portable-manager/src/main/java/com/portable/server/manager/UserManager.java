package com.portable.server.manager;

import com.portable.server.model.user.User;

public interface UserManager {

    User newNormalAccount();

    User getAccountByHandle(String handle);

    User getAccountById(Long id);

    Integer insertAccount(User user);

    Integer updateHandle(Long id, String handle);

    Integer updatePassword(Long id, String password);
}
