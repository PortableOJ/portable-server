package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.service.SolutionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/solution")
public class SolutionController {

    @Resource
    private SolutionService solutionService;

    @NeedLogin(false)
    @GetMapping("/getPublicStatus")
    public Response<PageResponse<SolutionListResponse>> getPublicSolutionList(Integer pageNum, Integer pageSize) {
        return Response.ofOk(solutionService.getPublicStatus(
                PageRequest.<Void>builder()
                        .pageNum(pageNum)
                        .pageSize(pageSize)
                        .build()
        ));
    }

    @NeedLogin(false)
    @GetMapping("/getContestStatus")
    public Response<PageResponse<SolutionListResponse>> getContestSolutionList(Integer pageNum, Integer pageSize, Long contestId) {
        return Response.ofOk(solutionService.getContestStatus(
                PageRequest.<Long>builder()
                        .pageNum(pageNum)
                        .pageSize(pageSize)
                        .queryData(contestId)
                        .build()
        ));
    }

    @NeedLogin
    @PostMapping("/submit")
    public Response<SolutionDetailResponse> submit(@RequestBody SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        return Response.ofOk(solutionService.submit(submitSolutionRequest));
    }

    @NeedLogin(false)
    @GetMapping("/getSolution")
    public Response<SolutionDetailResponse> getSolution(Long id) throws PortableException {
        return Response.ofOk(solutionService.getSolution(id));
    }
}
