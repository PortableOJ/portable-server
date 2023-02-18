package com.portable.server.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.model.request.IdRequest;
import com.portable.server.model.response.Response;
import com.portable.server.service.JudgeMaintenanceService;
import com.portable.server.type.PermissionType;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shiroha
 */
@Validated
@RestController
@RequestMapping("/api/judge")
public class JudgeController {

    @Resource
    private JudgeMaintenanceService judgeMaintenanceService;

    @NeedLogin(normal = true)
    @GetMapping("/serverCode")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<ServiceVerifyCode> getServerCode() {
        return Response.ofOk(judgeMaintenanceService.getServiceCode());
    }

    @GetMapping("/initCode")
    public void getServerCodeFirst(HttpServletResponse response) {
        String code = judgeMaintenanceService.getTheServiceCodeFirstTime();
        try {
            if (code != null) {
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(code.getBytes(StandardCharsets.UTF_8));
            } else {
                response.setStatus(404);
            }
        } catch (IOException ignore) {
        }
    }

    @NeedLogin(normal = true)
    @GetMapping("/judgeList")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<List<JudgeContainer>> getJudgeContainerList() {
        return Response.ofOk(judgeMaintenanceService.getJudgeContainerList());
    }

    @NeedLogin(normal = true)
    @PostMapping("/updateJudge")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<Void> updateJudgeContainer(@Validated @RequestBody UpdateJudgeContainer updateJudgeContainer) {
        judgeMaintenanceService.updateJudgeContainer(updateJudgeContainer);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/killJudge")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<Void> killJudge(@Validated @RequestBody IdRequest idRequest) {
        judgeMaintenanceService.killJudge(idRequest.getId());
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/killTest")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<Void> killTest(@Validated @RequestBody IdRequest idRequest) {
        judgeMaintenanceService.killTest(idRequest.getId());
        return Response.ofOk();
    }
}
