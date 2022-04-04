package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAddProblem;
import com.portable.server.model.request.contest.ContestAuth;
import com.portable.server.model.request.contest.ContestContentRequest;
import com.portable.server.model.request.contest.ContestRankPageRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestInfoResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.contest.ContestRankListResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.service.ContestService;
import com.portable.server.type.ContestVisitPermission;
import com.portable.server.type.PermissionType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.validation.Insert;
import com.portable.server.validation.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/api/contest")
public class ContestController {

    @Resource
    private ContestService contestService;

    @GetMapping("/getList")
    public Response<PageResponse<ContestListResponse, Void>> getContestList(Integer pageNum,
                                                                            Integer pageSize) {
        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
        pageRequest.verify();
        return Response.ofOk(contestService.getContestList(pageRequest));
    }

    @NeedLogin
    @PostMapping("/auth")
    public Response<ContestVisitPermission> authorizeContest(@Validated @RequestBody ContestAuth contestAuth) throws PortableException {
        return Response.ofOk(contestService.authorizeContest(contestAuth));
    }

    @NeedLogin
    @GetMapping("/getInfo")
    public Response<ContestInfoResponse> getContestInfo(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-08-002") Long contestId) throws PortableException {
        return Response.ofOk(contestService.getContestInfo(contestId));
    }

    @NeedLogin
    @GetMapping("/get")
    public Response<ContestDetailResponse> getContestData(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-08-002") Long contestId) throws PortableException {
        return Response.ofOk(contestService.getContestData(contestId));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getAdmin")
    public Response<ContestAdminDetailResponse> getContestAdminData(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-08-002") Long contestId) throws PortableException {
        return Response.ofOk(contestService.getContestAdminData(contestId));
    }

    @NeedLogin
    @GetMapping("/problem")
    public Response<ProblemDetailResponse> getContestProblem(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-08-002") Long contestId, @NotNull(message = "A-08-002") @Min(value = 0, message = "A-08-021") Integer problemIndex) throws PortableException {
        return Response.ofOk(contestService.getContestProblem(contestId, problemIndex));
    }

    @NeedLogin
    @GetMapping("/status")
    public Response<PageResponse<SolutionListResponse, Void>> getContestStatusList(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-08-002") Long contestId,
                                                                                   Integer pageNum,
                                                                                   Integer pageSize,
                                                                                   Long userId,
                                                                                   Long problemId,
                                                                                   SolutionStatusType statusType) throws PortableException {
        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .queryData(SolutionListQueryRequest.builder()
                        .userId(userId)
                        .problemId(problemId)
                        .statusType(statusType)
                        .build()
                )
                .build();
        pageRequest.verify();
        return Response.ofOk(contestService.getContestStatusList(contestId, pageRequest));
    }

    @NeedLogin
    @GetMapping("/statusDetail")
    public Response<SolutionDetailResponse> getContestSolution(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-05-001") Long solutionId) throws PortableException {
        return Response.ofOk(contestService.getContestSolution(solutionId));
    }

    @NeedLogin(normal = true)
    @GetMapping("/testStatus")
    public Response<PageResponse<SolutionListResponse, Void>> getContestTestStatusList(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-08-002") Long contestId,
                                                                                       Integer pageNum,
                                                                                       Integer pageSize,
                                                                                       Long userId,
                                                                                       Long problemId,
                                                                                       SolutionStatusType statusType) throws PortableException {
        PageRequest<SolutionListQueryRequest> pageRequest = PageRequest.<SolutionListQueryRequest>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .queryData(SolutionListQueryRequest.builder()
                        .userId(userId)
                        .problemId(problemId)
                        .statusType(statusType)
                        .build()
                )
                .build();
        pageRequest.verify();
        return Response.ofOk(contestService.getContestTestStatusList(contestId, pageRequest));
    }

    @NeedLogin(normal = true)
    @GetMapping("/testStatusDetail")
    public Response<SolutionDetailResponse> getContestTestSolution(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-05-001") Long solutionId) throws PortableException {
        return Response.ofOk(contestService.getContestTestSolution(solutionId));
    }

    @NeedLogin
    @GetMapping("/rank")
    public Response<PageResponse<ContestRankListResponse, ContestRankListResponse>> getContestRank(@NotNull(message = "A-08-002") @Min(value = 1, message = "A-08-002") Long contestId,
                                                                                                   Integer pageNum,
                                                                                                   Integer pageSize,
                                                                                                   @NotNull(message = "A-08-002") Boolean freeze) throws PortableException {
        PageRequest<ContestRankPageRequest> pageRequest = PageRequest.<ContestRankPageRequest>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .queryData(ContestRankPageRequest.builder()
                        .freeze(freeze)
                        .build())
                .build();
        pageRequest.verify();
        return Response.ofOk(contestService.getContestRank(contestId, pageRequest));
    }

    @NeedLogin
    @PostMapping("/submit")
    public Response<Long> submit(@Validated @RequestBody SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        return Response.ofOk(contestService.submit(submitSolutionRequest));
    }

    @NeedLogin(normal = true)
    @PostMapping("/newContest")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_CONTEST)
    public Response<Long> createContest(@Validated({Insert.class}) @RequestBody ContestContentRequest contestContentRequest) throws PortableException {
        return Response.ofOk(contestService.createContest(contestContentRequest));
    }

    @NeedLogin(normal = true)
    @PostMapping("/updateContest")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_CONTEST)
    public Response<Void> updateContest(@Validated({Update.class}) @RequestBody ContestContentRequest contestContentRequest) throws PortableException {
        contestService.updateContest(contestContentRequest);
        return Response.ofOk();

    }

    @NeedLogin(normal = true)
    @PostMapping("/addProblem")
    public Response<Void> addContestProblem(@Validated @RequestBody ContestAddProblem contestAddProblem) throws PortableException {
        contestService.addContestProblem(contestAddProblem);
        return Response.ofOk();
    }
}
