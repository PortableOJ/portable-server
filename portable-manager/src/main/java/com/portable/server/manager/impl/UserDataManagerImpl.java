package com.portable.server.manager.impl;

import com.portable.server.manager.UserDataManager;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.repo.UserDataRepo;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Optional;

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
    public BatchUserData newBatchUserData() {
        return BatchUserData.builder()
                ._id(null)
                .ipList(new ArrayList<>())
                .build();
    }

    @Override
    public Optional<NormalUserData> getNormalUserDataById(String dataId) {
        return Optional.ofNullable(userDataRepo.getNormalUserDataById(dataId));
    }

    @Override
    public Optional<BatchUserData> getBatchUserDataById(String dataId) {
        return Optional.ofNullable(userDataRepo.getBatchUserDataById(dataId));
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
