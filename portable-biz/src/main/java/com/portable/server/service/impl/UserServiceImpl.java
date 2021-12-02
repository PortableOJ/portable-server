package com.portable.server.service.impl;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.AccountManager;
import com.portable.server.manager.NormalUserManager;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.response.user.UserBasicInfoResponse;
import com.portable.server.model.user.User;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.service.UserService;
import com.portable.server.type.AccountType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import com.portable.server.util.UserContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserServiceImpl implements UserService {

    @Resource
    private AccountManager accountManager;

    @Resource
    private NormalUserManager normalUserManager;

    @Resource
    private BCryptEncoder bCryptEncoder;

    @Override
    public UserBasicInfoResponse login(LoginRequest loginRequest) throws PortableException {
        User user = accountManager.getAccountByHandle(loginRequest.getHandle());
        if (user == null) {
            throw PortableException.of("A-01-001");
        }
        if (!bCryptEncoder.match(loginRequest.getPassword(), user.getPassword())) {
            throw PortableException.of("A-01-002");
        }
        UserContext.set(user);
        switch (user.getType()) {
            case NORMAL:
                NormalUserData normalUserData = normalUserManager.getUserDataById(user.getDataId());
                if (normalUserData == null) {
                    throw PortableException.of("S-02-001");
                }
                UserContext.set(normalUserData);
                return NormalUserInfoResponse.of(user, normalUserData);
            case TEMPORARY:
            default:
                throw PortableException.of("S-02-002", user.getType());
        }
    }

    @Override
    public synchronized NormalUserInfoResponse register(RegisterRequest registerRequest) throws PortableException {
        User user = accountManager.getAccountByHandle(registerRequest.getHandle());
        if (user != null) {
            throw PortableException.of("A-01-003");
        }

        NormalUserData normalUserData = normalUserManager.newUserData();
        normalUserManager.insertNormalUserData(normalUserData);

        user = accountManager.newNormalAccount();
        user.setHandle(registerRequest.getHandle());
        user.setPassword(bCryptEncoder.encoder(registerRequest.getPassword()));
        user.setDataId(normalUserData.get_id());
        accountManager.insertAccount(user);

        UserContext.set(user);
        UserContext.set(normalUserData);

        return NormalUserInfoResponse.of(user, normalUserData);
    }

    @Override
    public void changeOrganization(Long targetId, OrganizationType newOrganization) throws PortableException {
        NormalUserData targetUserData = organizationCheck(targetId);
        if (!UserContext.ctx().getOrganization().isDominate(newOrganization)) {
            throw PortableException.of("A-03-002", newOrganization);
        }
        targetUserData.setOrganization(newOrganization);
        normalUserManager.updateNormalUserData(targetUserData);
    }

    @Override
    public void addPermission(Long targetId, PermissionType newPermission) throws PortableException {
        NormalUserData targetUserData = organizationCheck(targetId);
        if (!UserContext.ctx().getPermissionTypeSet().contains(newPermission)) {
            throw PortableException.of("A-02-007", newPermission);
        }
        targetUserData.getPermissionTypeSet().add(newPermission);
        normalUserManager.updateNormalUserData(targetUserData);
    }

    @Override
    public void removePermission(Long targetId, PermissionType permission) throws PortableException {
        NormalUserData targetUserData = organizationCheck(targetId);
        if (!UserContext.ctx().getPermissionTypeSet().contains(permission)) {
            throw PortableException.of("A-02-007", permission);
        }
        targetUserData.getPermissionTypeSet().remove(permission);
        normalUserManager.updateNormalUserData(targetUserData);
    }

    private NormalUserData organizationCheck(Long target) throws PortableException {
        User user = accountManager.getAccountById(target);
        if (user == null) {
            throw PortableException.of("A-01-001");
        }
        if (!AccountType.NORMAL.equals(user.getType())) {
            throw PortableException.of("A-02-003");
        }

        NormalUserData targetUserData = normalUserManager.getUserDataById(user.getDataId());
        if (targetUserData == null) {
            throw PortableException.of("S-02-001");
        }

        if (!UserContext.ctx().getOrganization().isDominate(targetUserData.getOrganization())) {
            throw PortableException.of("A-03-001", user.getHandle(), targetUserData.getOrganization(), UserContext.ctx().getOrganization());
        }
        return targetUserData;
    }
}
