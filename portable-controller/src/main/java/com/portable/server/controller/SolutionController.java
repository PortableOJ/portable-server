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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Validated
@RestController
@RequestMapping("/api/solution")
public class SolutionController {

    @Resource
    private SolutionService solutionService;

    @GetMapping("/getPublicStatus")
    public Response<PageResponse<SolutionListResponse, Void>> getPublicSolutionList(Integer pageNum,
                                                                                    Integer pageSize,
                                                                                    String userHandle,
                                                                                    Long problemId,
                                                                                    SolutionStatusType statusType) throws PortableException {
        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .queryData(
                        SolutionListQueryRequest.builder()
                                .userHandle(userHandle)
                                .problemId(problemId)
                                .statusType(statusType)
                                .build()
                )
                .build();
        pageRequest.verify();
        return Response.ofOk(solutionService.getPublicStatus(pageRequest));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getSolution")
    public Response<SolutionDetailResponse> getSolution(@NotNull(message = "A-05-001") @Min(value = 1, message = "A-05-001") Long id) throws PortableException {
        return Response.ofOk(solutionService.getSolution(id));
    }
}
