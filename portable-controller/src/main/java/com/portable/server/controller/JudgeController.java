package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.ServiceVerifyCode;
import com.portable.server.model.judge.entity.JudgeContainer;
import com.portable.server.model.judge.entity.UpdateJudgeContainer;
import com.portable.server.model.request.IdRequest;
import com.portable.server.model.response.Response;
import com.portable.server.service.JudgeService;
import com.portable.server.type.PermissionType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shiroha
 */
@RestController
@RequestMapping("/api/judge")
public class JudgeController {

    @Resource
    private JudgeService judgeService;

    @NeedLogin
    @GetMapping("/serverCode")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<ServiceVerifyCode> getServerCode() {
        return Response.ofOk(judgeService.getServerCode());
    }

    @NeedLogin
    @GetMapping("/judgeList")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<List<JudgeContainer>> getJudgeContainerList() {
        return Response.ofOk(judgeService.getJudgeContainerList());
    }

    @NeedLogin
    @PostMapping("/updateJudge")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<Void> updateJudgeContainer(@RequestBody UpdateJudgeContainer updateJudgeContainer) throws PortableException {
        judgeService.updateJudgeContainer(updateJudgeContainer);
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/killJudge")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<Void> killJudge(@RequestBody IdRequest idRequest) {
        judgeService.killJudge(idRequest.getId());
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/killTest")
    @PermissionRequirement(PermissionType.MANAGER_JUDGE)
    public Response<Void> killTest(@RequestBody IdRequest idRequest) {
        judgeService.killTest(idRequest.getId());
        return Response.ofOk();
    }
}