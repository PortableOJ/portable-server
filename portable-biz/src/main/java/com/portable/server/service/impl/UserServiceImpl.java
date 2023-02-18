package com.portable.server.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableErrors;
import com.portable.server.manager.BatchManager;
import com.portable.server.manager.ImageManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.request.user.UpdatePasswordRequest;
import com.portable.server.model.response.user.BaseUserInfoResponse;
import com.portable.server.model.response.user.BatchAdminUserInfoResponse;
import com.portable.server.model.response.user.BatchUserInfoResponse;
import com.portable.server.model.response.user.NormalUserInfoResponse;
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class UserServiceImpl implements UserService {

    @Resource
    private UserManager userManager;

    @Resource
    private BatchManager batchManager;

    @Resource
    private ImageManager imageManager;

    @Value("${ROOT_NAME}")
    private String rootName;

    @Value("${ROOT_PWD}")
    private String rootPassword;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchUserPackage {

        /**
         * 用户信息
         */
        @Delegate
        private User user;

        /**
         * 用户属性
         */
        @Delegate
        private BatchUserData userData;

        /**
         * 批量用户的信息
         */
        @Delegate
        private Batch batch;
    }

    @PostConstruct
    public void init() {
        // 创建 root 账户
        Optional<User> rootUser = userManager.getAccountByHandle(rootName);
        if (rootUser.isPresent()) {
            NormalUserData normalUserData = userManager.getNormalUserDataById(rootUser.get().getDataId());
            normalUserData.setPermissionTypeSet(Arrays.stream(PermissionType.values()).collect(Collectors.toSet()));
            userManager.updateUserData(normalUserData);
        } else {
            NormalUserData normalUserData = userManager.newNormalUserData();
            normalUserData.setOrganization(OrganizationType.ADMIN);
            normalUserData.setPermissionTypeSet(Arrays.stream(PermissionType.values()).collect(Collectors.toSet()));
            userManager.insertUserData(normalUserData);

            User user = userManager.newNormalAccount();
            user.setHandle(rootName);
            user.setPassword(BCryptEncoder.encoder(rootPassword));
            user.setDataId(normalUserData.getId());
            userManager.insertAccount(user);
        }
    }

    @Override
    public BaseUserInfoResponse login(LoginRequest loginRequest, String ip) {
        User user = userManager.getAccountByHandle(loginRequest.getHandle())
                .orElseThrow(PortableErrors.from("A-01-001"));
        if (!BCryptEncoder.match(loginRequest.getPassword(), user.getPassword())) {
            throw PortableErrors.of("A-01-002");
        }
        switch (user.getType()) {
            case LOCKED_NORMAL:
                user.setType(AccountType.NORMAL);
                userManager.updateUserType(user.getId(), AccountType.NORMAL);
                // 锁定的账号只需要修改用户的状态后，剩下的和正常账号完全相同
            case NORMAL:
                NormalUserData normalUserData = userManager.getNormalUserDataById(user.getDataId());
                UserContext.set(user);
                UserContext.set(normalUserData);
                return NormalUserInfoResponse.of(user, normalUserData);
            case BATCH:
                BatchUserData batchUserData = userManager.getBatchUserDataById(user.getDataId());
                Batch batch = batchManager.selectBatchById(batchUserData.getBatchId())
                        .orElseThrow(PortableErrors.from("A-10-006", batchUserData.getBatchId()));
                if (!BatchStatusType.NORMAL.equals(batch.getStatus())) {
                    throw PortableErrors.of("A-01-013");
                }
                Boolean isUpdateIp = batchUserData.addIpRecord(ip);
                if (isUpdateIp) {
                    // 首先得是锁定着的，其次必须要不是第一个 IP
                    if (batch.getIpLock() && batchUserData.getIpList().size() > 1) {
                        throw PortableErrors.of("A-01-012");
                    } else {
                        userManager.updateUserData(batchUserData);
                    }
                }
                UserContext.set(user);
                UserContext.set(batch);
                return BatchUserInfoResponse.of(user, batch, false);
            default:
                throw PortableErrors.of("S-02-002", user.getType());
        }
    }

    @Override
    public synchronized NormalUserInfoResponse register(RegisterRequest registerRequest) {
        Optional<User> userOptional = userManager.getAccountByHandle(registerRequest.getHandle());
        if (userOptional.isPresent()) {
            throw PortableErrors.of("A-01-003");
        }

        NormalUserData normalUserData = userManager.newNormalUserData();
        userManager.insertUserData(normalUserData);

        User user = userManager.newNormalAccount();
        user.setHandle(registerRequest.getHandle());
        user.setPassword(BCryptEncoder.encoder(registerRequest.getPassword()));
        user.setDataId(normalUserData.getId());
        userManager.insertAccount(user);

        UserContext.set(user);
        UserContext.set(normalUserData);

        return NormalUserInfoResponse.of(user, normalUserData);
    }

    @Override
    public BaseUserInfoResponse check() {
        UserContext userContext = UserContext.ctx();
        if (!userContext.isLogin()) {
            return null;
        }
        User user = userManager.getAccountById(userContext.getId())
                .orElseThrow(PortableErrors.from("A-01-001"));
        return getUserBasicInfoResponse(user);
    }

    @Override
    public BaseUserInfoResponse getUserInfo(String handle) {
        User user = userManager.getAccountByHandle(handle)
                .orElseThrow(PortableErrors.from("A-01-001"));
        return getUserBasicInfoResponse(user);
    }

    @Override
    public BatchAdminUserInfoResponse getBatchUserInfo(String handle) {
        BatchUserPackage batchUserPackage = checkBatchUser(handle);
        return BatchAdminUserInfoResponse.of(batchUserPackage.getUser(), batchUserPackage.getUserData(), batchUserPackage.getBatch(), true);
    }

    @Override
    public void changeOrganization(String targetHandle, OrganizationType newOrganization) {
        NormalUserData targetUserData = organizationCheck(targetHandle);
        targetUserData.setOrganization(newOrganization);
        userManager.updateUserData(targetUserData);
    }

    @Override
    public void addPermission(String targetHandle, PermissionType newPermission) {
        NormalUserData targetUserData = organizationCheck(targetHandle);
        if (!UserContext.ctx().getPermissionTypeSet().contains(newPermission)) {
            throw PortableErrors.of("A-02-007", newPermission);
        }
        if (targetUserData.getPermissionTypeSet() == null) {
            targetUserData.setPermissionTypeSet(new HashSet<>());
        }
        targetUserData.getPermissionTypeSet().add(newPermission);
        userManager.updateUserData(targetUserData);
    }

    @Override
    public void removePermission(String targetHandle, PermissionType permission) {
        NormalUserData targetUserData = organizationCheck(targetHandle);
        if (!UserContext.ctx().getPermissionTypeSet().contains(permission)) {
            throw PortableErrors.of("A-02-007", permission);
        }
        targetUserData.getPermissionTypeSet().remove(permission);
        userManager.updateUserData(targetUserData);
    }

    @Override
    public String uploadAvatar(InputStream inputStream,
                               String name,
                               String contentType,
                               Integer left,
                               Integer top,
                               Integer width,
                               Integer height) {
        UserContext userContext = UserContext.ctx();
        if (!userContext.getType().getIsNormal()) {
            throw PortableErrors.of("A-02-008", UserContext.ctx().getType());
        }
        NormalUserData normalUserData = userManager.getNormalUserDataById(userContext.getDataId());
        InputStream avatarStream = ImageUtils.cut(inputStream, left, top, width, height);
        String fileId = imageManager.replaceImage(normalUserData.getAvatar(), avatarStream, name, contentType);
        normalUserData.setAvatar(fileId);
        userManager.updateUserData(normalUserData);
        return fileId;
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        UserContext userContext = UserContext.ctx();
        if (!userContext.getType().getIsNormal()) {
            throw PortableErrors.of("A-01-011");
        }
        User user = userManager.getAccountById(userContext.getId())
                .orElseThrow(PortableErrors.from("A-01-001"));
        if (!BCryptEncoder.match(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw PortableErrors.of("A-01-002");
        }
        userManager.updatePassword(user.getId(), BCryptEncoder.encoder(updatePasswordRequest.getNewPassword()));
    }

    @Override
    public void resetPassword(String handle, String newPassword) {
        organizationCheck(handle);
        Long userId = userManager.changeHandleToUserId(handle).orElseThrow(PortableErrors.from("A-01-001"));
        userManager.updatePassword(userId, BCryptEncoder.encoder(newPassword));
    }

    @Override
    public void clearBatchUserIpList(String handle) {
        BatchUserPackage batchUserPackage = checkBatchUser(handle);
        batchUserPackage.getUserData().setIpList(new ArrayList<>());
        userManager.updateUserData(batchUserPackage.getUserData());
    }

    @NotNull
    private NormalUserData organizationCheck(String handle) {
        User user = userManager.getAccountByHandle(handle)
                .orElseThrow(PortableErrors.from("A-01-001"));
        if (!user.getType().getIsNormal()) {
            throw PortableErrors.of("A-02-003");
        }
        NormalUserData targetUserData = userManager.getNormalUserDataById(user.getDataId());
        if (!UserContext.ctx().getOrganization().isDominate(targetUserData.getOrganization())) {
            throw PortableErrors.of("A-03-001",
                    user.getHandle(),
                    targetUserData.getOrganization(),
                    UserContext.ctx().getOrganization());
        }
        return targetUserData;
    }

    private BaseUserInfoResponse getUserBasicInfoResponse(User user) {
        switch (user.getType()) {
            case LOCKED_NORMAL:
            case NORMAL:
                NormalUserData normalUserData = userManager.getNormalUserDataById(user.getDataId());
                return NormalUserInfoResponse.of(user, normalUserData);
            case BATCH:
                BatchUserData batchUserData = userManager.getBatchUserDataById(user.getDataId());

                Batch batch = batchManager.selectBatchById(batchUserData.getBatchId())
                        .orElseThrow(PortableErrors.from("A-10-006", batchUserData.getBatchId()));
                return BatchUserInfoResponse.of(user, batch, Objects.equals(UserContext.ctx().getId(), batch.getOwner()));
            default:
                throw PortableErrors.of("S-02-002", user.getType());
        }
    }

    private BatchUserPackage checkBatchUser(String handle) {
        User user = userManager.getAccountByHandle(handle).orElseThrow(PortableErrors.from("A-01-001"));
        if (!AccountType.BATCH.equals(user.getType())) {
            throw PortableErrors.of("A-01-014");
        }
        BatchUserData batchUserData = userManager.getBatchUserDataById(user.getDataId());
        Batch batch = batchManager.selectBatchById(batchUserData.getBatchId())
                .orElseThrow(PortableErrors.from("A-10-006", batchUserData.getBatchId()));
        // 校验是否是自己的
        if (!Objects.equals(UserContext.ctx().getId(), batch.getOwner())) {
            throw PortableErrors.of("A-10-002");
        }
        return BatchUserPackage.builder()
                .user(user)
                .userData(batchUserData)
                .batch(batch)
                .build();
    }

}
