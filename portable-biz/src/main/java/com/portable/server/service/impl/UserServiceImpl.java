package com.portable.server.service.impl;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.BatchManager;
import com.portable.server.manager.GridFsManager;
import com.portable.server.manager.UserDataManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.request.user.UpdatePasswordRequest;
import com.portable.server.model.response.user.BatchUserInfoResponse;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.response.user.UserBasicInfoResponse;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.service.UserService;
import com.portable.server.type.AccountType;
import com.portable.server.type.BatchStatusType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import com.portable.server.util.ImageUtils;
import com.portable.server.util.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class UserServiceImpl implements UserService {

    @Resource
    private UserManager userManager;

    @Resource
    private UserDataManager userDataManager;

    @Resource
    private BatchManager batchManager;

    @Resource
    private GridFsManager gridFsManager;

    @Value("${ROOT_NAME}")
    private String rootName;

    @Value("${ROOT_PWD}")
    private String rootPassword;

    @PostConstruct
    public void init() {
        // 创建 root 账户
        User rootUser = userManager.getAccountByHandle(rootName);
        if (rootUser == null) {
            NormalUserData normalUserData = userDataManager.newNormalUserData();
            normalUserData.setOrganization(OrganizationType.ADMIN);
            normalUserData.setPermissionTypeSet(Arrays.stream(PermissionType.values()).collect(Collectors.toSet()));
            userDataManager.insertUserData(normalUserData);

            rootUser = userManager.newNormalAccount();
            rootUser.setHandle(rootName);
            rootUser.setPassword(BCryptEncoder.encoder(rootPassword));
            rootUser.setDataId(normalUserData.get_id());
            userManager.insertAccount(rootUser);
        } else {
            NormalUserData normalUserData = userDataManager.getNormalUserDataById(rootUser.getDataId());
            normalUserData.setPermissionTypeSet(Arrays.stream(PermissionType.values()).collect(Collectors.toSet()));
            userDataManager.updateUserData(normalUserData);
        }
    }

    @Override
    public UserBasicInfoResponse login(LoginRequest loginRequest, String ip) throws PortableException {
        User user = userManager.getAccountByHandle(loginRequest.getHandle());
        if (user == null) {
            throw PortableException.of("A-01-001");
        }
        if (!BCryptEncoder.match(loginRequest.getPassword(), user.getPassword())) {
            throw PortableException.of("A-01-002");
        }
        switch (user.getType()) {
            case LOCKED_NORMAL:
                user.setType(AccountType.NORMAL);
                userManager.updateUserType(user.getId(), AccountType.NORMAL);
                // 锁定的账号只需要修改用户的状态后，剩下的和正常账号完全相同
            case NORMAL:
                UserContext.set(user);
                NormalUserData normalUserData = userDataManager.getNormalUserDataById(user.getDataId());
                if (normalUserData == null) {
                    throw PortableException.of("S-02-001");
                }
                UserContext.set(normalUserData);
                return NormalUserInfoResponse.of(user, normalUserData);
            case BATCH:
                UserContext.set(user);
                BatchUserData batchUserData = userDataManager.getBatchUserDataById(user.getDataId());
                Batch batch = batchManager.selectBatchById(batchUserData.getBatchId());
                if (!BatchStatusType.NORMAL.equals(batch.getStatus())) {
                    throw PortableException.of("A-01-013");
                }
                Boolean isUpdateIp = batchUserData.addIpRecord(ip);
                if (isUpdateIp) {
                    // 首先得是锁定着的，其次必须要不是第一个 IP
                    if (batch.getIpLock() && batchUserData.getIpList().size() > 1) {
                        throw PortableException.of("A-01-012");
                    } else {
                        userDataManager.updateUserData(batchUserData);
                    }
                }
                UserContext.set(batch);
                return BatchUserInfoResponse.of(user, batch);
            default:
                throw PortableException.of("S-02-002", user.getType());
        }
    }

    @Override
    public synchronized NormalUserInfoResponse register(RegisterRequest registerRequest) throws PortableException {
        User user = userManager.getAccountByHandle(registerRequest.getHandle());
        if (user != null) {
            throw PortableException.of("A-01-003");
        }

        NormalUserData normalUserData = userDataManager.newNormalUserData();
        userDataManager.insertUserData(normalUserData);

        user = userManager.newNormalAccount();
        user.setHandle(registerRequest.getHandle());
        user.setPassword(BCryptEncoder.encoder(registerRequest.getPassword()));
        user.setDataId(normalUserData.get_id());
        userManager.insertAccount(user);

        UserContext.set(user);
        UserContext.set(normalUserData);

        return NormalUserInfoResponse.of(user, normalUserData);
    }

    @Override
    public UserBasicInfoResponse getUserInfo(Long userId) throws PortableException {
        User user = userManager.getAccountById(userId);
        if (user == null) {
            throw PortableException.of("A-01-001");
        }
        return getUserBasicInfoResponse(user);
    }

    @Override
    public UserBasicInfoResponse getUserInfo(String handle) throws PortableException {
        User user = userManager.getAccountByHandle(handle);
        if (user == null) {
            throw PortableException.of("A-01-001");
        }
        return getUserBasicInfoResponse(user);
    }

    @Override
    public void changeOrganization(Long targetId, OrganizationType newOrganization) throws PortableException {
        NormalUserData targetUserData = organizationCheck(targetId);
        if (!UserContext.ctx().getOrganization().isDominate(newOrganization)) {
            throw PortableException.of("A-03-002", newOrganization);
        }
        targetUserData.setOrganization(newOrganization);
        userDataManager.updateUserData(targetUserData);
    }

    @Override
    public void addPermission(Long targetId, PermissionType newPermission) throws PortableException {
        NormalUserData targetUserData = organizationCheck(targetId);
        if (!UserContext.ctx().getPermissionTypeSet().contains(newPermission)) {
            throw PortableException.of("A-02-007", newPermission);
        }
        targetUserData.getPermissionTypeSet().add(newPermission);
        userDataManager.updateUserData(targetUserData);
    }

    @Override
    public void removePermission(Long targetId, PermissionType permission) throws PortableException {
        NormalUserData targetUserData = organizationCheck(targetId);
        if (!UserContext.ctx().getPermissionTypeSet().contains(permission)) {
            throw PortableException.of("A-02-007", permission);
        }
        targetUserData.getPermissionTypeSet().remove(permission);
        userDataManager.updateUserData(targetUserData);
    }

    @Override
    public String uploadAvatar(InputStream inputStream,
                               String name,
                               String contentType,
                               Integer left,
                               Integer top,
                               Integer width,
                               Integer height) throws PortableException {
        UserContext userContext = UserContext.ctx();
        if (!AccountType.NORMAL.equals(userContext.getType())) {
            throw PortableException.of("A-02-008", UserContext.ctx().getType());
        }
        NormalUserData normalUserData = userDataManager.getNormalUserDataById(userContext.getDataId());
        InputStream avatarStream = ImageUtils.cut(inputStream, left, top, width, height);
        String fileId = gridFsManager.uploadAvatar(normalUserData.getAvatar(), avatarStream, name, contentType);
        normalUserData.setAvatar(fileId);
        userDataManager.updateUserData(normalUserData);
        return fileId;
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws PortableException {
        UserContext userContext = UserContext.ctx();
        User user = userManager.getAccountById(userContext.getId());
        if (!AccountType.NORMAL.equals(user.getType())) {
            throw PortableException.of("A-01-011");
        }
        if (!BCryptEncoder.match(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw PortableException.of("A-01-002");
        }
        userManager.updatePassword(user.getId(), BCryptEncoder.encoder(updatePasswordRequest.getNewPassword()));
    }

    private NormalUserData organizationCheck(Long target) throws PortableException {
        User user = userManager.getAccountById(target);
        if (user == null) {
            throw PortableException.of("A-01-001");
        }
        if (!AccountType.NORMAL.equals(user.getType())) {
            throw PortableException.of("A-02-003");
        }

        NormalUserData targetUserData = userDataManager.getNormalUserDataById(user.getDataId());
        if (targetUserData == null) {
            throw PortableException.of("S-02-001");
        }

        if (!UserContext.ctx().getOrganization().isDominate(targetUserData.getOrganization())) {
            throw PortableException.of("A-03-001", user.getHandle(), targetUserData.getOrganization(), UserContext.ctx().getOrganization());
        }
        return targetUserData;
    }

    private UserBasicInfoResponse getUserBasicInfoResponse(User user) throws PortableException {
        switch (user.getType()) {
            case NORMAL:
                NormalUserData normalUserData = userDataManager.getNormalUserDataById(user.getDataId());
                if (normalUserData == null) {
                    throw PortableException.of("S-02-001");
                }
                return NormalUserInfoResponse.of(user, normalUserData);
            case BATCH:
                BatchUserData batchUserData = userDataManager.getBatchUserDataById(user.getDataId());
                if (batchUserData == null) {
                    throw PortableException.of("S-02-001");
                }
                Batch batch = batchManager.selectBatchById(batchUserData.getBatchId());
                return BatchUserInfoResponse.of(user, batch);
            default:
                throw PortableException.of("S-02-002", user.getType());
        }
    }

}
