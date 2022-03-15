package com.portable.server.manager.impl;

import com.portable.server.manager.UserDataManager;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.repo.UserDataRepo;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@Component
public class UserDataManagerImpl implements UserDataManager {

    @Resource
    private UserDataRepo userDataRepo;

    @Override
    public NormalUserData newNormalUserData() {
        return NormalUserData.builder()
                ._id(null)
                .organization(OrganizationType.STUDENT)
                .submission(0)
                .accept(0)
                .permissionTypeSet(PermissionType.defaultPermission())
                .email(null)
                .avatar(null)
                .build();
    }

    @Override
    public NormalUserData getNormalUserDataById(String dataId) {
        return userDataRepo.getNormalUserDataById(dataId);
    }

    @Override
    public void insertNormalUserData(NormalUserData normalUserData) {
        userDataRepo.insertUserData(normalUserData);
    }

    @Override
    public void updateNormalUserData(NormalUserData normalUserData) {
        userDataRepo.saveUserData(normalUserData);
    }
}
