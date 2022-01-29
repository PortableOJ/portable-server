package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.request.user.PermissionRequest;
import com.portable.server.model.request.user.LoginRequest;
import com.portable.server.model.request.user.OrganizationChangeRequest;
import com.portable.server.model.request.user.RegisterRequest;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.user.NormalUserInfoResponse;
import com.portable.server.model.response.user.UserBasicInfoResponse;
import com.portable.server.service.UserService;
import com.portable.server.type.PermissionType;
import com.portable.server.util.RequestSessionConstant;
import com.portable.server.util.UserContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author shiroha
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 账号长度限制
     */
    private static final Integer MIN_HANDLE_LENGTH = 3;
    private static final Integer MAX_HANDLE_LENGTH = 30;

    /**
     * 密码长度限制
     */
    private static final Integer MIN_PASSWORD_LENGTH = 4;
    private static final Integer MAX_PASSWORD_LENGTH = 16;

    @NeedLogin(false)
    @PostMapping("/login")
    public Response<UserBasicInfoResponse> login(HttpServletRequest request, @RequestBody LoginRequest loginRequest) throws PortableException {
        lengthCheck(loginRequest.getHandle(), MIN_HANDLE_LENGTH, MAX_HANDLE_LENGTH, "A-01-004");
        lengthCheck(loginRequest.getPassword(), MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, "A-01-005");

        UserBasicInfoResponse userBasicInfoResponse = userService.login(loginRequest);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(RequestSessionConstant.USER_ID, userBasicInfoResponse.getId());
        return Response.ofOk(userBasicInfoResponse);
    }

    @NeedLogin(false)
    @PostMapping("/register")
    public Response<NormalUserInfoResponse> register(HttpServletRequest request, @RequestBody RegisterRequest registerRequest) throws PortableException {
        lengthCheck(registerRequest.getHandle(), MIN_HANDLE_LENGTH, MAX_HANDLE_LENGTH, "A-01-004");
        lengthCheck(registerRequest.getPassword(), MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, "A-01-005");

        NormalUserInfoResponse normalUserInfoResponse = userService.register(registerRequest);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(RequestSessionConstant.USER_ID, normalUserInfoResponse.getId());
        return Response.ofOk(normalUserInfoResponse);
    }

    @NeedLogin
    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        httpSession.removeAttribute(RequestSessionConstant.USER_ID);
        return Response.ofOk();
    }

    @NeedLogin(false)
    @GetMapping("/check")
    public Response<UserBasicInfoResponse> check(HttpServletRequest request) throws PortableException {
        if (!UserContext.ctx().isLogin()) {
            return Response.ofOk();
        }
        return Response.ofOk(userService.getUserInfo(UserContext.ctx().getId()));
    }

    @NeedLogin
    @PostMapping("/changeOrganization")
    @PermissionRequirement(PermissionType.CHANGE_ORGANIZATION)
    public Response<Void> changeOrganization(@RequestBody OrganizationChangeRequest organizationChangeRequest) throws PortableException {
        userService.changeOrganization(organizationChangeRequest.getTargetId(), organizationChangeRequest.getNewOrganization());
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/addPermission")
    @PermissionRequirement(PermissionType.GRANT)
    public Response<Void> addPermission(@RequestBody PermissionRequest permissionRequest) throws PortableException {
        userService.addPermission(permissionRequest.getTargetId(), permissionRequest.getPermissionType());
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/removePermission")
    @PermissionRequirement(PermissionType.GRANT)
    public Response<Void> removePermission(@RequestBody PermissionRequest permissionRequest) throws PortableException {
        userService.removePermission(permissionRequest.getTargetId(), permissionRequest.getPermissionType());
        return Response.ofOk();
    }

    private void lengthCheck(String text, Integer min, Integer max, String code) throws PortableException {
        if (Strings.isEmpty(text) || text.length() < min || text.length() > max) {
            throw PortableException.of(code, min, max);
        }
    }
}
