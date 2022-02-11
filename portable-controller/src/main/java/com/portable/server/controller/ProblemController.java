package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.request.IdRequest;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.problem.*;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.problem.ProblemDataResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.response.problem.ProblemStdTestCodeResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.service.ProblemService;
import com.portable.server.type.PermissionType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author shiroha
 */
@RestController
@RequestMapping("/api/problem")
public class ProblemController {

    private static final Long MAX_SUBMIT_CODE_LENGTH = 65536L;

    @Resource
    private ProblemService problemService;

    @NeedLogin(false)
    @GetMapping("/getList")
    public Response<PageResponse<ProblemListResponse>> getProblemList(Integer pageNum, Integer pageSize) {
        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                        .pageNum(pageNum)
                        .pageSize(pageSize)
                        .build();
        pageRequest.verify();
        return Response.ofOk(problemService.getProblemList(pageRequest));
    }

    @NeedLogin(false)
    @GetMapping("/getData")
    public Response<ProblemDataResponse> getProblem(Long id) throws PortableException {
        return Response.ofOk(problemService.getProblem(id));
    }

    @NeedLogin
    @GetMapping("/getTestList")
    public Response<List<String>> getTestList(Long id) throws PortableException {
        return Response.ofOk(problemService.getProblemTestList(id));
    }

    @NeedLogin
    @GetMapping("/getTestInputShow")
    public Response<String> getTestInputShow(Long id, String name) throws PortableException {
        return Response.ofOk(problemService.showTestInput(
                ProblemNameRequest.builder()
                        .id(id)
                        .name(name)
                        .build()
        ));
    }

    @NeedLogin
    @GetMapping("/getTestOutputShow")
    public Response<String> getTestOutputShow(Long id, String name) throws PortableException {
        return Response.ofOk(problemService.showTestOutput(
                ProblemNameRequest.builder()
                        .id(id)
                        .name(name)
                        .build()
        ));
    }

    @NeedLogin
    @GetMapping("/getTestInput")
    public void getTestInput(HttpServletResponse response, Long id, String name) throws PortableException {
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

    @NeedLogin
    @GetMapping("/getTestOutput")
    public void getTestOutput(HttpServletResponse response, Long id, String name) throws PortableException {
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

    @NeedLogin
    @PostMapping("/newProblem")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Long> newProblem(@RequestBody ProblemContentRequest problemContentRequest) throws PortableException {
        Problem problem = problemService.newProblem(problemContentRequest);
        return Response.ofOk(problem.getId());
    }

    @NeedLogin
    @PostMapping("/updateContent")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateContent(@RequestBody ProblemContentRequest problemContentRequest) throws PortableException {
        problemService.updateProblemContent(problemContentRequest);
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/updateSetting")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateSetting(@RequestBody ProblemSettingRequest problemSettingRequest) throws PortableException {
        problemService.updateProblemSetting(problemSettingRequest);
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/updateJudge")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateJudge(@RequestBody ProblemJudgeRequest problemJudgeRequest) throws PortableException {
        problemService.updateProblemJudge(problemJudgeRequest);
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/addTest")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> addTest(Long id, String name, MultipartFile fileData) throws PortableException {
        problemService.addProblemTest(
                ProblemTestRequest.builder()
                        .id(id)
                        .fileData(fileData)
                        .name(name)
                        .build()
        );
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/removeTest")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> removeTest(@RequestBody ProblemNameRequest problemNameRequest) throws PortableException {
        problemService.removeProblemTest(problemNameRequest);
        return Response.ofOk();
    }

    @NeedLogin
    @GetMapping("/getStdTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<ProblemStdTestCodeResponse> getStdTestCode(Long id) throws PortableException {
        return Response.ofOk(problemService.getProblemStdTestCode(id));
    }

    @NeedLogin
    @PostMapping("/updateStdCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> updateStdCode(@RequestBody ProblemCodeRequest problemCodeRequest) throws PortableException {
        checkCodeLength(problemCodeRequest.getCode());
        problemService.updateProblemStdCode(problemCodeRequest);
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/addTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> addTestCode(@RequestBody ProblemCodeRequest problemCodeRequest) throws PortableException {
        checkCodeLength(problemCodeRequest.getCode());
        problemService.addProblemTestCode(problemCodeRequest);
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/removeTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> removeTestCode(@RequestBody ProblemNameRequest problemNameRequest) throws PortableException {
        problemService.removeProblemTestCode(problemNameRequest);
        return Response.ofOk();
    }

    @NeedLogin
    @GetMapping("/getStdCodeShow")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<String> getStdCodeShow(Long id) throws PortableException {
        return Response.ofOk(problemService.showStdCode(id));
    }

    @NeedLogin
    @GetMapping("/getTestCodeShow")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<String> getStdCodeShow(Long id, String name) throws PortableException {
        return Response.ofOk(problemService.showTestCode(
                ProblemNameRequest.builder()
                        .id(id)
                        .name(name)
                        .build()
        ));
    }

    @NeedLogin
    @GetMapping("/getStdCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public void getStdCode(HttpServletResponse httpServletResponse, Long id) throws PortableException {
        try {
            problemService.downloadStdCode(id, httpServletResponse.getOutputStream());
        } catch (IOException e) {
            throw PortableException.of("S-01-002");
        }
    }

    @NeedLogin
    @GetMapping("/getTestCode")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public void getTestCode(HttpServletResponse httpServletResponse, Long id, String name) throws PortableException {
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

    @NeedLogin
    @PostMapping("/treatAndCheckProblem")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_PROBLEM)
    public Response<Void> treatAndCheckProblem(@RequestBody IdRequest idRequest) throws PortableException {
        problemService.treatAndCheckProblem(idRequest.getId());
        return Response.ofOk();
    }

    @NeedLogin
    @PostMapping("/submit")
    public Response<Long> submit(@RequestBody SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        checkCodeLength(submitSolutionRequest.getCode());
        return Response.ofOk(problemService.submit(submitSolutionRequest));
    }

    private void checkCodeLength(String code) throws PortableException {
        if (code.length() > MAX_SUBMIT_CODE_LENGTH) {
            throw PortableException.of("A-04-011", MAX_SUBMIT_CODE_LENGTH);
        }
    }
}
