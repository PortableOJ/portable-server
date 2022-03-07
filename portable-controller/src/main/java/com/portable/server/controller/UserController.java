package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.OrganizationChangeRequest;
import com.portable.server.model.request.user.PermissionRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.response.user.UserBasicInfoResponse;
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
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

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

    @NeedLogin(false)
    @PostMapping("/login")
    public Response<UserBasicInfoResponse> login(HttpServletRequest request, @Valid @RequestBody LoginRequest loginRequest) throws PortableException {
        UserContext.set(UserContext.getNullUser());
        UserBasicInfoResponse userBasicInfoResponse = userService.login(loginRequest);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(RequestSessionConstant.USER_ID, userBasicInfoResponse.getId());
        return Response.ofOk(userBasicInfoResponse);
    }

    @NeedLogin(false)
    @PostMapping("/register")
    public Response<NormalUserInfoResponse> register(HttpServletRequest request, @Valid @RequestBody RegisterRequest registerRequest) throws PortableException {
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

    @NeedLogin(false)
    @GetMapping("/check")
    public Response<UserBasicInfoResponse> check() throws PortableException {
        if (!UserContext.ctx().isLogin()) {
            return Response.ofOk();
        }
        return Response.ofOk(userService.getUserInfo(UserContext.ctx().getId()));
    }

    @NeedLogin(false)
    @GetMapping("/getUserInfo")
    public Response<UserBasicInfoResponse> getUserInfo(@NotBlank(message = "A-01-006") String handle) throws PortableException {
        return Response.ofOk(userService.getUserInfo(handle));
    }

    @NeedLogin
    @PostMapping("/changeOrganization")
    @PermissionRequirement(PermissionType.CHANGE_ORGANIZATION)
    public Response<Void> changeOrganization(@Valid @RequestBody OrganizationChangeRequest organizationChangeRequest) throws PortableException {
        userService.changeOrganization(organizationChangeRequest.getTargetId(), organizationChangeRequest.getNewOrganization());
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/addPermission")
    @PermissionRequirement(PermissionType.GRANT)
    public Response<Void> addPermission(@Valid @RequestBody PermissionRequest permissionRequest) throws PortableException {
        userService.addPermission(permissionRequest.getTargetId(), permissionRequest.getPermissionType());
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/removePermission")
    @PermissionRequirement(PermissionType.GRANT)
    public Response<Void> removePermission(@Valid @RequestBody PermissionRequest permissionRequest) throws PortableException {
        userService.removePermission(permissionRequest.getTargetId(), permissionRequest.getPermissionType());
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/avatar")
    public Response<Void> uploadAvatar(@NotNull(message = "A-01-010") MultipartFile fileData) throws PortableException {
        if (IMAGE_FILE_MAX_SIZE.compareTo(fileData.getSize()) < 0) {
            throw PortableException.of("A-09-002", IMAGE_FILE_MAX_SIZE);
        }
        try {
            userService.uploadAvatar(fileData.getInputStream(), fileData.getOriginalFilename(), fileData.getContentType());
            return Response.ofOk();
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
    }
}
