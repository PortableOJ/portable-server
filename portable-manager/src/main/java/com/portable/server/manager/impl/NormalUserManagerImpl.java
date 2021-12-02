package com.portable.server.manager.impl;

import com.portable.server.manager.NormalUserManager;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.repo.NormalUserDataRepo;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NormalUserManagerImpl implements NormalUserManager {

    @Resource
    private NormalUserDataRepo normalUserDataRepo;

    @Override
    public NormalUserData newUserData() {
        return NormalUserData.builder()
                ._id(null)
                .organization(OrganizationType.STUDENT)
                .submission(0)
                .accept(0)
                .permissionTypeSet(PermissionType.defaultPermission())
                .email(null)
                .build();
    }

    @Override
    public NormalUserData getUserDataById(String dataId) {
        return normalUserDataRepo.getUserDataById(dataId);
    }

    @Override
    public void insertNormalUserData(NormalUserData normalUserData) {
        normalUserDataRepo.insertUserData(normalUserData);
    }

    @Override
    public void updateNormalUserData(NormalUserData normalUserData) {
        normalUserDataRepo.saveUserData(normalUserData);
    }
}
