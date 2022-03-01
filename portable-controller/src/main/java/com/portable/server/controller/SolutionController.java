package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.service.SolutionService;
import com.portable.server.type.SolutionStatusType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@RestController
@RequestMapping("/api/solution")
public class SolutionController {

    @Resource
    private SolutionService solutionService;

    @NeedLogin(false)
    @GetMapping("/getPublicStatus")
    public Response<PageResponse<SolutionListResponse>> getPublicSolutionList(Integer pageNum,
                                                                              Integer pageSize,
                                                                              Long userId,
                                                                              Long problemId,
                                                                              SolutionStatusType statusType) {
        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .queryData(
                        SolutionListQueryRequest.builder()
                                .userId(userId)
                                .problemId(problemId)
                                .statusType(statusType)
                                .build()
                )
                .build();
        pageRequest.verify();
        return Response.ofOk(solutionService.getPublicStatus(pageRequest));
    }

    @NeedLogin(false)
    @GetMapping("/getSolution")
    public Response<SolutionDetailResponse> getSolution(Long id) throws PortableException {
        return Response.ofOk(solutionService.getSolution(id));
    }
}
