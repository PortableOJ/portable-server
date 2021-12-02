package com.portable.server.manager;

import com.portable.server.model.user.NormalUserData;

public interface NormalUserManager {

    NormalUserData newUserData();

    NormalUserData getUserDataById(String dataId);

    void insertNormalUserData(NormalUserData normalUserData);

    void updateNormalUserData(NormalUserData normalUserData);
}
