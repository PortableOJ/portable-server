package com.portable.server.controller;

import com.portable.server.annotation.CheckCaptcha;
import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.request.IdRequest;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.problem.ProblemCodeRequest;
import com.portable.server.model.request.problem.ProblemContentRequest;
import com.portable.server.model.request.problem.ProblemJudgeRequest;
import com.portable.server.model.request.problem.ProblemNameRequest;
import com.portable.server.model.request.problem.ProblemSettingRequest;
import com.portable.server.model.request.problem.ProblemTestRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.response.problem.ProblemStdTestCodeResponse;
import com.portable.server.service.ProblemService;
import com.portable.server.type.PermissionType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.List;

/**
 * @author shiroha
 */
@Validated
@RestController
@RequestMapping("/api/problem")
public class ProblemController {

    @Resource
    private ProblemService problemService;

    @GetMapping("/getList")
    public Response<PageResponse<ProblemListResponse, Void>> getProblemList(Integer pageNum,
                                                                            Integer pageSize) {
        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
        pageRequest.verify();
        return Response.ofOk(problemService.getProblemList(pageRequest));
    }

    @NeedLogin(normal = true)
    @GetMapping("/search")
    public Response<List<ProblemListResponse>> searchProblem(String keyword) {
        return Response.ofOk(problemService.searchProblemSetList(keyword));
    }

    @NeedLogin(normal = true)
    @GetMapping("/searchPrivate")
    public Response<List<ProblemListResponse>> searchProblemPrivate(String keyword) {
        return Response.ofOk(problemService.searchPrivateProblemList(keyword));
    }

    @GetMapping("/getData")
    public Response<ProblemDetailResponse> getProblem(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id) throws PortableException {
        return Response.ofOk(problemService.getProblem(id));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getTestList")
    public Response<List<String>> getTestList(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id) throws PortableException {
        return Response.ofOk(problemService.getProblemTestList(id));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getTestInputShow")
    public Response<String> getTestInputShow(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id,
                                             String name) throws PortableException {
        return Response.ofOk(problemService.showTestInput(
                ProblemNameRequest.builder()
                        .id(id)
                        .name(name)
                        .build()
        ));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getTestOutputShow")
    public Response<String> getTestOutputShow(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id,
                                              String name) throws PortableException {
        return Response.ofOk(problemService.showTestOutput(
                ProblemNameRequest.builder()
                        .id(id)
                        .name(name)
                        .build()
        ));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getTestInput")
    public void getTestInput(HttpServletResponse response,
                             @NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id,
                             String name) throws PortableException {
        try {
            problemService.downloadTestInput(
                    ProblemNameRequest.builder()
                            .id(id)
                            .name(name)
                            .build(),
                    response.getOutputStream()
            );
        } catch (IOException e) {
            throw PortableException.of("S-01-002");
        }
    }

    @NeedLogin(normal = true)
    @GetMapping("/getTestOutput")
    public void getTestOutput(HttpServletResponse response,
                              @NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id,
                              String name) throws PortableException {
        try {
            problemService.downloadTestOutput(
                    ProblemNameRequest.builder()
                            .id(id)
                            .name(name)
                            .build(),
                    response.getOutputStream()
            );
        } catch (IOException e) {
            throw PortableException.of("S-01-002");
        }
    }

    @NeedLogin(normal = true)
    @PostMapping("/newProblem")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Long> newProblem(@Validated @RequestBody ProblemContentRequest problemContentRequest) throws PortableException {
        Problem problem = problemService.newProblem(problemContentRequest);
        return Response.ofOk(problem.getId());
    }

    @NeedLogin(normal = true)
    @PostMapping("/updateContent")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateContent(@Validated @RequestBody ProblemContentRequest problemContentRequest) throws PortableException {
        problemService.updateProblemContent(problemContentRequest);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/updateSetting")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateSetting(@Validated @RequestBody ProblemSettingRequest problemSettingRequest) throws PortableException {
        problemService.updateProblemSetting(problemSettingRequest);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/updateJudge")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateJudge(@Validated @RequestBody ProblemJudgeRequest problemJudgeRequest) throws PortableException {
        problemService.updateProblemJudge(problemJudgeRequest);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/addTest")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> addTest(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id,
                                  @NotNull(message = "A-04-001") @Pattern(regexp = "^[a-zA-Z0-9_\\-]{1,15}$", message = "A-04-012") String name,
                                  @NotNull(message = "A-04-005") MultipartFile fileData) throws PortableException {
        try {
            problemService.addProblemTest(
                    ProblemTestRequest.builder()
                            .id(id)
                            .inputStream(fileData.getInputStream())
                            .name(name)
                            .build()
            );
        } catch (IOException e) {
            throw PortableException.of("A-04-005");
        }
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/removeTest")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> removeTest(@Validated @RequestBody ProblemNameRequest problemNameRequest) throws PortableException {
        problemService.removeProblemTest(problemNameRequest);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @GetMapping("/getStdTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<ProblemStdTestCodeResponse> getStdTestCode(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id) throws PortableException {
        return Response.ofOk(problemService.getProblemStdTestCode(id));
    }

    @NeedLogin(normal = true)
    @PostMapping("/updateStdCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateStdCode(@Validated @RequestBody ProblemCodeRequest problemCodeRequest) throws PortableException {
        problemService.updateProblemStdCode(problemCodeRequest);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/addTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> addTestCode(@Validated @RequestBody ProblemCodeRequest problemCodeRequest) throws PortableException {
        problemService.addProblemTestCode(problemCodeRequest);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/removeTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> removeTestCode(@Validated @RequestBody ProblemNameRequest problemNameRequest) throws PortableException {
        problemService.removeProblemTestCode(problemNameRequest);
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @GetMapping("/getStdCodeShow")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<String> getStdCodeShow(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id) throws PortableException {
        return Response.ofOk(problemService.showStdCode(id));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getTestCodeShow")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<String> getStdCodeShow(@NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id, String name) throws PortableException {
        return Response.ofOk(problemService.showTestCode(
                ProblemNameRequest.builder()
                        .id(id)
                        .name(name)
                        .build()
        ));
    }

    @NeedLogin(normal = true)
    @GetMapping("/getStdCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public void getStdCode(HttpServletResponse httpServletResponse, @NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id) throws PortableException {
        try {
            problemService.downloadStdCode(id, httpServletResponse.getOutputStream());
        } catch (IOException e) {
            throw PortableException.of("S-01-002");
        }
    }

    @NeedLogin(normal = true)
    @GetMapping("/getTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public void getTestCode(HttpServletResponse httpServletResponse, @NotNull(message = "A-04-001") @Min(value = 1, message = "A-04-001") Long id, String name) throws PortableException {
        try {
            problemService.downloadTestCode(
                    ProblemNameRequest.builder()
                            .id(id)
                            .name(name)
                            .build(),
                    httpServletResponse.getOutputStream());
        } catch (IOException e) {
            throw PortableException.of("S-01-002");
        }
    }

    @NeedLogin(normal = true)
    @PostMapping("/treatAndCheckProblem")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> treatAndCheckProblem(@Validated @RequestBody IdRequest idRequest) throws PortableException {
        problemService.treatAndCheckProblem(idRequest.getId());
        return Response.ofOk();
    }

    @NeedLogin(normal = true)
    @PostMapping("/submit")
    @CheckCaptcha(value = 60000, name = "submit")
    public Response<Long> submit(@Validated @RequestBody SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        return Response.ofOk(problemService.submit(submitSolutionRequest));
    }
}
