package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestManager;
import com.portable.server.manager.ProblemDataManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserDataManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.problem.ProblemCodeRequest;
import com.portable.server.model.request.problem.ProblemContentRequest;
import com.portable.server.model.request.problem.ProblemJudgeRequest;
import com.portable.server.model.request.problem.ProblemNameRequest;
import com.portable.server.model.request.problem.ProblemSettingRequest;
import com.portable.server.model.request.problem.ProblemTestRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.response.problem.ProblemStdTestCodeResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.service.ProblemService;
import com.portable.server.support.FileSupport;
import com.portable.server.support.JudgeSupport;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.PermissionType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.type.ProblemVisitType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.StreamUtils;
import com.portable.server.util.UserContext;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class ProblemServiceImpl implements ProblemService {

    @Data
    @Builder
    public static class ProblemPackage {
        private Problem problem;
        private ProblemData problemData;
        private Contest contest;
    }

    @Resource
    private ProblemManager problemManager;

    @Resource
    private ProblemDataManager problemDataManager;

    @Resource
    private UserManager userManager;

    @Resource
    private UserDataManager userDataManager;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private SolutionDataManager solutionDataManager;

    @Resource
    private ContestManager contestManager;

    @Resource
    private FileSupport fileSupport;

    @Resource
    private JudgeSupport judgeSupport;

    @Value("${portable.problem.test.show.limit}")
    private Integer maxTestShowLen;

    @Value("${portable.service.search.size}")
    private Integer searchPageSize;

    @Override
    public PageResponse<ProblemListResponse, Void> getProblemList(PageRequest<Void> pageRequest) {
        boolean isLogin = UserContext.ctx().isLogin();
        Long userId = isLogin ? UserContext.ctx().getId() : null;
        boolean viewHiddenProblem = isLogin && UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_HIDDEN_PROBLEM);
        List<ProblemAccessType> problemAccessTypeList = viewHiddenProblem ? Arrays.asList(ProblemAccessType.PUBLIC, ProblemAccessType.HIDDEN) : Collections.singletonList(ProblemAccessType.PUBLIC);

        Integer problemCount = problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, userId);
        PageResponse<ProblemListResponse, Void> problemPageResponse = PageResponse.of(pageRequest, problemCount);
        List<Problem> problemList = problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, userId, problemPageResponse.getPageSize(), problemPageResponse.offset());
        List<ProblemListResponse> problemDataResponseList = problemList.stream()
                .parallel()
                .map(problem -> {
                    if (isLogin) {
                        Solution solution = solutionManager.selectLastSolution(userId, problem.getId()).orElse(null);
                        return ProblemListResponse.of(problem, solution);
                    }
                    return ProblemListResponse.of(problem, null);
                })
                .collect(Collectors.toList());

        problemPageResponse.setData(problemDataResponseList);
        return problemPageResponse;
    }

    @Override
    public List<ProblemListResponse> searchProblemSetList(String keyword) {
        boolean viewHiddenProblem = UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_HIDDEN_PROBLEM);
        List<ProblemAccessType> problemAccessTypeList = viewHiddenProblem
                ? Arrays.asList(ProblemAccessType.PUBLIC, ProblemAccessType.HIDDEN)
                : Collections.singletonList(ProblemAccessType.PUBLIC);
        List<Problem> problemList = problemManager.searchRecentProblemByTypedAndKeyword(problemAccessTypeList, keyword, searchPageSize);
        return problemList.stream()
                .map(problem -> ProblemListResponse.of(problem, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProblemListResponse> searchPrivateProblemList(String keyword) {
        List<Problem> problemList = problemManager.searchRecentProblemByOwnerIdAndKeyword(UserContext.ctx().getId(), keyword, searchPageSize);
        return problemList.stream()
                .map(problem -> ProblemListResponse.of(problem, null))
                .collect(Collectors.toList());
    }

    @Override
    public ProblemDetailResponse getProblem(Long id) throws PortableException {
        ProblemPackage problemPackage = getForViewProblem(id);
        User user = userManager.getAccountById(problemPackage.getProblem().getOwner()).orElse(null);
        return ProblemDetailResponse.of(problemPackage.getProblem(), problemPackage.getProblemData(), user);
    }

    @Override
    public List<String> getProblemTestList(Long id) throws PortableException {
        ProblemPackage problemPackage = getForViewProblemTest(id);
        return problemPackage.getProblemData().getTestName();
    }

    @Override
    public String showTestInput(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getForViewProblemTest(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        return StreamUtils.read(fileSupport.getTestInput(problemNameRequest.getId(), problemNameRequest.getName()), maxTestShowLen);
    }

    @Override
    public String showTestOutput(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getForViewProblemTest(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        return StreamUtils.read(fileSupport.getTestOutput(problemNameRequest.getId(), problemNameRequest.getName()), maxTestShowLen);
    }

    @Override
    public void downloadTestInput(ProblemNameRequest problemNameRequest, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getForViewProblemTest(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        StreamUtils.copy(fileSupport.getTestInput(problemNameRequest.getId(), problemNameRequest.getName()), outputStream);
    }

    @Override
    public void downloadTestOutput(ProblemNameRequest problemNameRequest, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getForViewProblemTest(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        StreamUtils.copy(fileSupport.getTestOutput(problemNameRequest.getId(), problemNameRequest.getName()), outputStream);
    }

    @Override
    public Problem newProblem(ProblemContentRequest problemContentRequest) throws PortableException {
        Problem problem = problemManager.newProblem();
        ProblemData problemData = problemDataManager.newProblemData();
        problemContentRequest.toProblem(problem);
        problemContentRequest.toProblemData(problemData);
        problem.setOwner(UserContext.ctx().getId());

        problemDataManager.insertProblemData(problemData);
        problem.setDataId(problemData.get_id());
        problemManager.insertProblem(problem);
        fileSupport.createProblem(problem.getId());

        return problem;
    }

    @Override
    public void updateProblemContent(ProblemContentRequest problemContentRequest) throws PortableException {
        ProblemPackage problemPackage = getForFullAccessProblem(problemContentRequest.getId());
        problemContentRequest.toProblemData(problemPackage.getProblemData());

        problemManager.updateProblemTitle(problemContentRequest.getId(), problemContentRequest.getTitle());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void updateProblemSetting(ProblemSettingRequest problemSettingRequest) throws PortableException {
        ProblemPackage problemPackage = getForEditProblem(problemSettingRequest.getId());

        // 发生题目访问权限更新
        if (!Objects.equals(problemPackage.getProblem().getAccessType(), problemSettingRequest.getAccessType())) {
            // 从公开转私有 => 拒绝
            if (ProblemAccessType.PRIVATE.equals(problemSettingRequest.getAccessType())) {
                throw PortableException.of("A-04-015");
            }
            // 其他任何转换 => 不允许在比赛期间发生
            if (problemPackage.getProblemData().getContestId() != null) {
                Contest contest = problemPackage.getContest();
                if (!contest.isEnd()) {
                    throw PortableException.of("A-04-014", problemPackage.getProblemData().getContestId());
                }
            }
        }

        boolean isChecked = problemPackage.getProblem().getStatusType().getChecked();
        boolean needCheck = problemSettingRequest.toProblemData(problemPackage.getProblemData());

        if (isChecked) {
            // 可能因为修改了耗时导致了部分的原来通过的代码变得不通过了
            if (needCheck || checkAnyStdCodeNotPass(problemPackage.getProblemData())) {
                problemPackage.getProblem().toUncheck();
            }
        }

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemManager.updateProblemAccessStatus(problemPackage.getProblem().getId(), problemSettingRequest.getAccessType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void updateProblemJudge(ProblemJudgeRequest problemJudgeRequest) throws PortableException {
        if (!JudgeCodeType.DIY.equals(problemJudgeRequest.getJudgeCodeType())) {
            problemJudgeRequest.setJudgeCode("");
        }
        ProblemPackage problemPackage = getForEditProblem(problemJudgeRequest.getId());
        problemJudgeRequest.toProblemData(problemPackage.getProblemData());
        if (ProblemStatusType.NORMAL.equals(problemPackage.getProblem().getStatusType())) {
            problemPackage.getProblemData().nextVersion();
        }
        problemPackage.getProblem().toUncheck();
        problemPackage.getProblemData().setGmtModifyTime(new Date());

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void addProblemTest(ProblemTestRequest problemTestRequest) throws PortableException {
        ProblemPackage problemPackage = getForEditProblem(problemTestRequest.getId());

        if (!problemPackage.getProblemData().getTestName().contains(problemTestRequest.getName())) {
            problemPackage.getProblemData().getTestName().add(problemTestRequest.getName());
        }
        fileSupport.saveTestInput(problemTestRequest.getId(), problemTestRequest.getName(), problemTestRequest.getInputStream());
        if (problemPackage.getProblem().getStatusType().getTreated()) {
            problemPackage.getProblemData().nextVersion();
        }
        problemPackage.getProblem().toUntreated();

        problemPackage.getProblemData().setGmtModifyTime(new Date());

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void removeProblemTest(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getForEditProblem(problemNameRequest.getId());

        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        problemPackage.getProblemData().getTestName().remove(problemNameRequest.getName());

        fileSupport.removeTest(problemNameRequest.getId(), problemNameRequest.getName());

        if (ProblemStatusType.NORMAL.equals(problemPackage.getProblem().getStatusType())) {
            problemPackage.getProblemData().nextVersion();
        }

        problemPackage.getProblemData().setGmtModifyTime(new Date());
        problemPackage.getProblem().toUncheck();

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public ProblemStdTestCodeResponse getProblemStdTestCode(Long id) throws PortableException {
        ProblemPackage problemPackage = getForFullAccessProblem(id);
        ProblemStdTestCodeResponse problemStdTestCodeResponse = ProblemStdTestCodeResponse.of(problemPackage.getProblemData());
        problemStdTestCodeResponse.getTestCodeList().forEach(stdCode -> {
            if (stdCode.getSolutionId() == null) {
                return;
            }
            Optional<Solution> solutionOptional = solutionManager.selectSolutionById(stdCode.getSolutionId());
            solutionOptional.ifPresent(solution -> stdCode.setSolutionStatusType(solution.getStatus()));
        });
        return problemStdTestCodeResponse;
    }

    @Override
    public void updateProblemStdCode(ProblemCodeRequest problemCodeRequest) throws PortableException {
        ProblemPackage problemPackage = getForEditProblem(problemCodeRequest.getId());

        problemCodeRequest.toStdCode(problemPackage.getProblemData().getStdCode());
        problemPackage.getProblemData().getStdCode().setExpectResultType(SolutionStatusType.ACCEPT);
        if (problemPackage.getProblem().getStatusType().getTreated()) {
            problemPackage.getProblemData().nextVersion();
        }

        problemPackage.getProblem().toUntreated();

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void addProblemTestCode(ProblemCodeRequest problemStdCodeRequest) throws PortableException {
        ProblemPackage problemPackage = getForEditProblem(problemStdCodeRequest.getId());

        try {
            ProblemData.StdCode stdCode = problemPackage.getProblemData().findStdCode(problemStdCodeRequest.getCodeName());
            problemStdCodeRequest.toStdCode(stdCode);
        } catch (PortableException exception) {
            ProblemData.StdCode stdCode = ProblemData.StdCode.builder()
                    .name(problemStdCodeRequest.getCodeName())
                    .code(problemStdCodeRequest.getCode())
                    .expectResultType(problemStdCodeRequest.getResultType())
                    .languageType(problemStdCodeRequest.getLanguageType())
                    .solutionId(null)
                    .build();
            problemPackage.getProblemData().getTestCodeList().add(stdCode);
        }

        problemPackage.getProblem().toUncheck();

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void removeProblemTestCode(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getForEditProblem(problemNameRequest.getId());
        problemPackage.getProblemData().getTestCodeList().removeIf(stdCode -> Objects.equals(stdCode.getName(), problemNameRequest.getName()));
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public String showStdCode(Long id) throws PortableException {
        ProblemPackage problemPackage = getForFullAccessProblem(id);
        return problemPackage.getProblemData().getStdCode().getCode();
    }

    @Override
    public String showTestCode(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getForFullAccessProblem(problemNameRequest.getId());
        return problemPackage.getProblemData().findStdCode(problemNameRequest.getName()).getCode();
    }

    @Override
    public void downloadStdCode(Long id, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getForFullAccessProblem(id);
        StreamUtils.write(problemPackage.getProblemData().getStdCode().getCode(), outputStream);
    }

    @Override
    public void downloadTestCode(ProblemNameRequest problemNameRequest, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getForFullAccessProblem(problemNameRequest.getId());
        StreamUtils.write(problemPackage.getProblemData().findStdCode(problemNameRequest.getName()).getCode(), outputStream);
    }

    @Override
    public void treatAndCheckProblem(Long id) throws PortableException {
        ProblemPackage problemPackage = getForEditProblem(id);
        // 已经是 normal 了
        if (problemPackage.getProblem().getStatusType().getChecked()) {
            return;
        }
        // 只需要 check 时
        if (problemPackage.getProblem().getStatusType().getTreated()) {
            judgeSupport.removeProblemJudge(id);
            judgeSupport.reportTestOver(id);
        } else {
            // 校验是否满足能够 treat 的条件
            ProblemData.StdCode stdCode = problemPackage.getProblemData().getStdCode();
            if (stdCode.getCode() == null || stdCode.getLanguageType() == null) {
                throw PortableException.of("A-04-009");
            }
            if (problemPackage.getProblemData().getTestName().isEmpty()) {
                throw PortableException.of("A-04-010");
            }

            judgeSupport.removeProblemJudge(id);
            judgeSupport.removeProblemCache(id);
            judgeSupport.addTestTask(id);
        }
    }

    @Override
    public Long submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        ProblemPackage problemPackage = getForViewProblem(submitSolutionRequest.getProblemId());
        if (!ProblemStatusType.NORMAL.equals(problemPackage.getProblem().getStatusType())) {
            throw PortableException.of("A-05-004", problemPackage.getProblem().getStatusType());
        }
        UserContext userContext = UserContext.ctx();

        // 更新用户和题目的提交统计数量
        problemManager.updateProblemCount(submitSolutionRequest.getProblemId(), 1, 0);
        NormalUserData normalUserData = userDataManager.getNormalUserDataById(userContext.getDataId());
        normalUserData.setSubmission(normalUserData.getSubmission() + 1);
        userDataManager.updateUserData(normalUserData);

        // 创建提交信息
        SolutionData solutionData = solutionDataManager.newSolutionData(problemPackage.getProblemData());
        submitSolutionRequest.toSolutionData(solutionData);
        solutionDataManager.insertSolutionData(solutionData);

        Solution solution = solutionManager.newSolution();
        submitSolutionRequest.toSolution(solution);
        solution.setDataId(solutionData.get_id());
        solution.setUserId(userContext.getId());
        solution.setSolutionType(SolutionType.PUBLIC);
        solutionManager.insertSolution(solution);

        judgeSupport.addJudgeTask(solution.getId());

        return solution.getId();
    }

    private ProblemPackage getForViewProblem(Long id) throws PortableException {
        ProblemPackage problemPackage = getProblemPackage(id);
        ProblemVisitType accessType = ProblemVisitType.of(problemPackage.getProblem(), problemPackage.getContest());
        if (accessType.getViewProblem()) {
            return problemPackage;
        }
        throw PortableException.of("A-02-004", id);
    }

    private ProblemPackage getForViewProblemTest(Long id) throws PortableException {
        ProblemPackage problemPackage = getProblemPackage(id);
        ProblemVisitType accessType = ProblemVisitType.of(problemPackage.getProblem(), problemPackage.getContest());
        boolean viewAndShare = accessType.getViewProblem() && problemPackage.getProblemData().getShareTest();
        if (accessType.getEditProblem() || viewAndShare) {
            return problemPackage;
        }
        throw PortableException.of("A-02-006");
    }

    private ProblemPackage getForFullAccessProblem(Long id) throws PortableException {
        ProblemPackage problemPackage = getProblemPackage(id);
        ProblemVisitType accessType = ProblemVisitType.of(problemPackage.getProblem(), problemPackage.getContest());
        if (accessType.getEditProblem()) {
            return problemPackage;
        }
        throw PortableException.of("A-02-005", id);
    }

    private ProblemPackage getForEditProblem(Long id) throws PortableException {
        ProblemPackage problemPackage = getForFullAccessProblem(id);
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }
        return problemPackage;
    }

    private ProblemPackage getProblemPackage(Long id) throws PortableException {
        Problem problem = problemManager.getProblemById(id)
                .orElseThrow(PortableException.from("A-04-001", id));
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        Contest contest = contestManager.getContestById(problemData.getContestId()).orElse(null);
        return ProblemPackage.builder()
                .problem(problem)
                .problemData(problemData)
                .contest(contest)
                .build();
    }

    /**
     * 检查新的时间和内存限制是否导致了部分原来通过的代码无法通过了
     *
     * @return 导致不通过则返回 true
     */
    private Boolean checkAnyStdCodeNotPass(ProblemData problemData) {
        if (checkStdCodeNotPass(problemData.getStdCode(), problemData)) {
            return true;
        }
        for (ProblemData.StdCode stdCode : problemData.getTestCodeList()) {
            if (checkStdCodeNotPass(stdCode, problemData)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查新的时间和内存限制是否导致了某个代码无法通过了
     *
     * @param stdCode 需要检查的代码
     * @return 导致不通过则返回 true
     */
    private Boolean checkStdCodeNotPass(ProblemData.StdCode stdCode, ProblemData problemData) {
        if (stdCode.getSolutionId() == null) {
            return false;
        }
        Optional<Solution> solutionOptional = solutionManager.selectSolutionById(stdCode.getSolutionId());
        if (!solutionOptional.isPresent()) {
            return false;
        }
        Solution solution = solutionOptional.get();
        Integer timeLimit = problemData.getTimeLimit(stdCode.getLanguageType());
        Integer memoryLimit = problemData.getMemoryLimit(stdCode.getLanguageType());
        if (!SolutionStatusType.TIME_LIMIT_EXCEEDED.equals(stdCode.getExpectResultType())) {
            if (timeLimit <= solution.getTimeCost()) {
                return true;
            }
        }
        return memoryLimit <= solution.getMemoryCost();
    }
}
