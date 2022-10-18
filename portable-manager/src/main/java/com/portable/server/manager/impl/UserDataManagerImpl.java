package com.portable.server.manager.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.UserDataManager;
import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.repo.UserDataRepo;
import com.portable.server.type.OrganizationType;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class UserDataManagerImpl implements UserDataManager {

    @Resource
    private UserDataRepo userDataRepo;

    @Override
    public @NotNull NormalUserData newNormalUserData() {
        return NormalUserData.builder()
                ._id(null)
                .organization(OrganizationType.STUDENT)
                .submission(0)
                .accept(0)
                .permissionTypeSet(new HashSet<>())
                .email(null)
                .avatar(null)
                .build();
    }

    @Override
    public @NotNull BatchUserData newBatchUserData() {
        return BatchUserData.builder()
                ._id(null)
                .ipList(new ArrayList<>())
                .build();
    }

    @NotNull
    @Override
    public NormalUserData getNormalUserDataById(String dataId) {
        return Optional.ofNullable(userDataRepo.getNormalUserDataById(dataId)).orElseThrow(PortableException.from("S-02-001"));
    }

    @NotNull
    @Override
    public BatchUserData getBatchUserDataById(String dataId) {
        return Optional.ofNullable(userDataRepo.getBatchUserDataById(dataId)).orElseThrow(PortableException.from("S-02-001"));
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
