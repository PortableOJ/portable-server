package com.portable.server.manager;

import com.portable.server.model.user.User;

public interface UserManager {

    User newNormalAccount();

    User getAccountByHandle(String handle);

    User getAccountById(Long id);

    void insertAccount(User user);

    void updateHandle(Long id, String handle);

    void updatePassword(Long id, String password);
}
