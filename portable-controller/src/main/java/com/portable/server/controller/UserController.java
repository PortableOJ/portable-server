package com.portable.server.controller;

import com.portable.server.annotation.CheckCaptcha;
import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.request.NameRequest;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.OrganizationChangeRequest;
import com.portable.server.model.request.user.PermissionRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.request.user.ResetPasswordRequest;
import com.portable.server.model.request.user.UpdatePasswordRequest;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.user.BaseUserInfoResponse;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.service.UserService;
import com.portable.server.type.PermissionType;
import com.portable.server.util.RequestSessionConstant;
import com.portable.server.util.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;

/**
 * @author shiroha
 */
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    private static final Long IMAGE_FILE_MAX_SIZE = 1024 * 1024 * 10L;

    @PostMapping("/login")
    public Response<BaseUserInfoResponse> login(HttpServletRequest request, @Validated @RequestBody LoginRequest loginRequest) throws PortableException {
        UserContext.set(UserContext.getNullUser());
        BaseUserInfoResponse baseUserInfoResponse = userService.login(loginRequest, getIp(request));
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(RequestSessionConstant.USER_ID, baseUserInfoResponse.getId());
        return Response.ofOk(baseUserInfoResponse);
    }

    @CheckCaptcha
    @PostMapping("/register")
    public Response<NormalUserInfoResponse> register(HttpServletRequest request, @Validated @RequestBody RegisterRequest registerRequest) throws PortableException {
        UserContext.set(UserContext.getNullUser());
        NormalUserInfoResponse normalUserInfoResponse = userService.register(registerRequest);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(RequestSessionConstant.USER_ID, normalUserInfoResponse.getId());
        return Response.ofOk(normalUserInfoResponse);
    }

    @NeedLogin
    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request) {
        UserContext.set(UserContext.getNullUser());
        HttpSession httpSession = request.getSession();
        httpSession.removeAttribute(RequestSessionConstant.USER_ID);
        return Response.ofOk();
    }

    @GetMapping("/check")
    public Response<BaseUserInfoResponse> check() throws PortableException {
        return Response.ofOk(userService.check());
    }

    @GetMapping("/getUserInfo")
    public Response<BaseUserInfoResponse> getUserInfo(@NotBlank(message = "A-01-006") String handle) throws PortableException {
        return Response.ofOk(userService.getUserInfo(handle));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getBatchUserAdminInfo")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_BATCH)
    public Response<BaseUserInfoResponse> getBatchUserAdminInfo(@NotBlank(message = "A-01-006") String handle) throws PortableException {
        return Response.ofOk(userService.getBatchUserInfo(handle));
    }

    @NeedLogin(normal = true)
    @PostMapping("/changeOrganization")
    @PermissionRequirement(PermissionType.CHANGE_ORGANIZATION)
    public Response<Void> changeOrganization(@Validated @RequestBody OrganizationChangeRequest organizationChangeRequest) throws PortableException {
        userService.changeOrganization(organizationChangeRequest.getTargetHandle(), organizationChangeRequest.getNewOrganization());
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/addPermission")
    @PermissionRequirement(PermissionType.GRANT)
    public Response<Void> grantPermission(@Validated @RequestBody PermissionRequest permissionRequest) throws PortableException {
        userService.addPermission(permissionRequest.getTargetHandle(), permissionRequest.getPermissionType());
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/removePermission")
    @PermissionRequirement(PermissionType.GRANT)
    public Response<Void> removePermission(@Validated @RequestBody PermissionRequest permissionRequest) throws PortableException {
        userService.removePermission(permissionRequest.getTargetHandle(), permissionRequest.getPermissionType());
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/avatar")
    public Response<String> uploadAvatar(@NotNull(message = "A-01-010") MultipartFile fileData,
                                         @NotNull(message = "A-00-001") Integer left,
                                         @NotNull(message = "A-00-001") Integer top,
                                         @NotNull(message = "A-00-001") Integer width,
                                         @NotNull(message = "A-00-001") Integer height) throws PortableException {
        if (IMAGE_FILE_MAX_SIZE.compareTo(fileData.getSize()) < 0) {
            throw PortableException.of("A-09-002", IMAGE_FILE_MAX_SIZE);
        }
        try {
            return Response.ofOk(
                    userService.uploadAvatar(fileData.getInputStream(),
                            fileData.getOriginalFilename(),
                            fileData.getContentType(),
                            left, top, width, height)
            );
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }

    @NeedLogin(normal = true)
    @PostMapping("/changePassword")
    public Response<Void> changePassword(HttpServletRequest request, @Validated @RequestBody UpdatePasswordRequest updatePasswordRequest) throws PortableException {
        userService.updatePassword(updatePasswordRequest);
        UserContext.set(UserContext.getNullUser());
        HttpSession httpSession = request.getSession();
        httpSession.removeAttribute(RequestSessionConstant.USER_ID);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/resetPassword")
    @PermissionRequirement(PermissionType.RESET_PASSWORD)
    public Response<Void> resetPassword(HttpServletRequest request, @Validated @RequestBody ResetPasswordRequest resetPasswordRequest) throws PortableException {
        userService.resetPassword(resetPasswordRequest.getHandle(), resetPasswordRequest.getNewPassword());
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/clearIpList")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_BATCH)
    public Response<Void> clearBatchUserIpList(@Validated @RequestBody NameRequest nameRequest) throws PortableException {
        userService.clearBatchUserIpList(nameRequest.getName());
        return Response.ofOk();
    }

    private String getIp(HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getHeader("X-FORWARDED-FOR"))
                .orElseGet(servletRequest::getRemoteAddr);
    }
}
