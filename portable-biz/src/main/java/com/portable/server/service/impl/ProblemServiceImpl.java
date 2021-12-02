package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.AccountManager;
import com.portable.server.manager.ProblemDataManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.problem.*;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.problem.ProblemDataResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.response.problem.ProblemStdTestCodeResponse;
import com.portable.server.model.user.User;
import com.portable.server.support.FileSupport;
import com.portable.server.service.ProblemService;
import com.portable.server.type.PermissionType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.util.StreamUtils;
import com.portable.server.util.UserContext;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
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
    }

    @Getter
    private enum User2ProblemAccessType {

        /**
         * 无访问权限
         */
        NO_ACCESS(false, false),

        /**
         * 仅查看
         */
        VIEW(true, false),

        /**
         * 完全的访问权限
         */
        FULL_ACCESS(true, true),
        ;

        private final Boolean viewProblem;
        private final Boolean editProblem;

        User2ProblemAccessType(Boolean viewProblem, Boolean editProblem) {
            this.viewProblem = viewProblem;
            this.editProblem = editProblem;
        }

        public static User2ProblemAccessType of(Problem problem) {

            // 题目拥有者拥有完整权限
            if (UserContext.ctx().isLogin()
                    && Objects.equals(problem.getOwner(), UserContext.ctx().getId())
                    && UserContext.ctx().getPermissionTypeSet().contains(PermissionType.CREATE_AND_EDIT_PROBLEM)) {
                return FULL_ACCESS;
            }

            User2ProblemAccessType resultAccessType;
            switch (problem.getAccessType()) {
                case PUBLIC:
                    resultAccessType = VIEW;
                    break;
                case HIDDEN:
                    if (UserContext.ctx().isLogin()
                            && UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_HIDDEN_PROBLEM)) {
                        resultAccessType = VIEW;
                    } else {
                        resultAccessType = NO_ACCESS;
                    }
                    break;
                case PRIVATE:
                default:
                    return NO_ACCESS;
            }

            if (User2ProblemAccessType.VIEW.equals(resultAccessType)
                    && UserContext.ctx().isLogin()
                    && UserContext.ctx().getPermissionTypeSet().contains(PermissionType.EDIT_NOT_OWNER_PROBLEM)) {
                return FULL_ACCESS;
            }
            return resultAccessType;
        }
    }

    @Value("${portable.problem.test.show.limit}")
    private Integer maxTestShowLen;

    @Resource
    private ProblemManager problemManager;

    @Resource
    private AccountManager accountManager;

    @Resource
    private ProblemDataManager problemDataManager;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private FileSupport fileSupport;

    @Override
    public PageResponse<ProblemListResponse> getProblemList(PageRequest<Void> pageRequest) {
        boolean isLogin = UserContext.ctx().isLogin();
        Long userId = isLogin ? UserContext.ctx().getId() : null;
        boolean viewHiddenProblem = UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_HIDDEN_PROBLEM);
        List<ProblemAccessType> problemAccessTypeList = viewHiddenProblem ? Arrays.asList(ProblemAccessType.PUBLIC, ProblemAccessType.HIDDEN) : Collections.singletonList(ProblemAccessType.PUBLIC);

        Integer problemCount = problemManager.countProblemByTypeAndOwnerId(problemAccessTypeList, userId);
        PageResponse<ProblemListResponse> problemPageResponse = PageResponse.of(pageRequest, problemCount);
        List<Problem> problemList = problemManager.getProblemListByTypeAndOwnerIdAndPaged(problemAccessTypeList, userId, pageRequest.getPageSize(), pageRequest.offset());
        List<ProblemListResponse> problemDataResponseList = isLogin
                ? problemList.stream()
                .parallel()
                .map(problem -> ProblemListResponse.of(problem, solutionManager.selectLastSolutionByUserIdAndProblemId(userId, problem.getId())))
                .collect(Collectors.toList())
                : problemList.stream()
                .parallel()
                .map(problem -> ProblemListResponse.of(problem, null))
                .collect(Collectors.toList());

        problemPageResponse.setData(problemDataResponseList);
        return problemPageResponse;
    }

    @Override
    public ProblemDataResponse getProblem(Long id) throws PortableException {
        ProblemPackage problemPackage = getViewProblem(id);
        return ProblemDataResponse.of(problemPackage.getProblem(), problemPackage.getProblemData());
    }

    @Override
    public List<String> getProblemTestList(Long id) throws PortableException {
        ProblemPackage problemPackage = getViewProblemTest(id);
        return problemPackage.getProblemData().getTestName();
    }

    @Override
    public String showTestInput(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getViewProblem(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        return StreamUtils.read(fileSupport.getTestInput(problemNameRequest.getId(), problemNameRequest.getName()), maxTestShowLen);
    }

    @Override
    public String showTestOutput(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getViewProblem(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        return StreamUtils.read(fileSupport.getTestOutput(problemNameRequest.getId(), problemNameRequest.getName()), maxTestShowLen);
    }

    @Override
    public void downloadTestInput(ProblemNameRequest problemNameRequest, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getViewProblem(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        StreamUtils.copy(fileSupport.getTestInput(problemNameRequest.getId(), problemNameRequest.getName()), outputStream);
    }

    @Override
    public void downloadTestOutput(ProblemNameRequest problemNameRequest, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getViewProblem(problemNameRequest.getId());
        problemPackage.getProblemData().findTest(problemNameRequest.getName());
        StreamUtils.copy(fileSupport.getTestOutput(problemNameRequest.getId(), problemNameRequest.getName()), outputStream);
    }

    @Override
    public synchronized Problem newProblem(ProblemContentRequest problemContentRequest) throws PortableException {
        Problem problem = problemManager.newProblem();
        ProblemData problemData = problemDataManager.newProblemData();
        problemContentRequest.toProblem(problem);
        problemContentRequest.toProblemData(problemData);

        problemDataManager.insertProblemData(problemData);
        problem.setDataId(problemData.get_id());
        problemManager.insertProblem(problem);
        fileSupport.createProblem(problem.getId());

        return problem;
    }

    @Override
    public void updateProblemContent(ProblemContentRequest problemContentRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemContentRequest.getId());
        problemContentRequest.toProblemData(problemPackage.getProblemData());

        problemManager.updateProblemTitle(problemContentRequest.getId(), problemContentRequest.getTitle());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void updateProblemSetting(ProblemSettingRequest problemSettingRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemSettingRequest.getId());
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }

        if (problemSettingRequest.toProblemData(problemPackage.getProblemData())) {
            problemPackage.getProblem().toUncheck();
        }

        problemManager.updateProblemAccessStatus(problemPackage.getProblem().getId(), problemSettingRequest.getAccessType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void updateProblemJudge(ProblemJudgeRequest problemJudgeRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemJudgeRequest.getId());
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }
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
        ProblemPackage problemPackage = getEditProblem(problemTestRequest.getId());
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }

        if (!problemPackage.getProblemData().getTestName().contains(problemTestRequest.getName())) {
            problemPackage.getProblemData().getTestName().add(problemTestRequest.getName());
        }

        try {
            fileSupport.saveTestInput(problemTestRequest.getId(), problemTestRequest.getName(), problemTestRequest.getFileData().getInputStream());
        } catch (IOException e) {
            throw PortableException.of("A-04-005");
        }

        problemPackage.getProblem().toUntreated();

        if (ProblemStatusType.NORMAL.equals(problemPackage.getProblem().getStatusType())) {
            problemPackage.getProblemData().nextVersion();
        }

        problemPackage.getProblemData().setGmtModifyTime(new Date());

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void removeProblemTest(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemNameRequest.getId());
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }
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
        ProblemPackage problemPackage = getEditProblem(id);
        return ProblemStdTestCodeResponse.of(problemPackage.getProblemData());
    }

    @Override
    public void updateProblemStdCode(ProblemCodeRequest problemCodeRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemCodeRequest.getId());
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }
        problemCodeRequest.toStdCode(problemPackage.getProblemData().getStdCode());
        if (ProblemStatusType.NORMAL.equals(problemPackage.getProblem().getStatusType())) {
            problemPackage.getProblemData().nextVersion();
        }

        problemPackage.getProblem().toUntreated();

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void addProblemTestCode(ProblemCodeRequest problemStdCodeRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemStdCodeRequest.getId());
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }

        try {
            ProblemData.StdCode stdCode = problemPackage.getProblemData().findStdCode(problemStdCodeRequest.getCodeName());
            problemStdCodeRequest.toStdCode(stdCode);
        } catch (PortableException exception) {
            ProblemData.StdCode stdCode = ProblemData.StdCode.builder()
                    .name(problemStdCodeRequest.getCodeName())
                    .code(problemStdCodeRequest.getCode())
                    .expectResultType(problemStdCodeRequest.getResultType())
                    .languageType(problemStdCodeRequest.getLanguageType())
                    .solutionStatusType(SolutionStatusType.PENDING)
                    .timeCost(0)
                    .memoryCost(0)
                    .build();
            problemPackage.getProblemData().getTestCodeList().add(stdCode);
        }

        problemPackage.getProblem().toUncheck();

        problemManager.updateProblemStatus(problemPackage.getProblem().getId(), problemPackage.getProblem().getStatusType());
        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public void removeProblemTestCode(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemNameRequest.getId());
        if (problemPackage.getProblem().getStatusType().getOnTreatedOrCheck()) {
            throw PortableException.of("A-04-007");
        }

        problemPackage.getProblemData().getTestCodeList().removeIf(stdCode -> Objects.equals(stdCode.getName(), problemNameRequest.getName()));

        problemDataManager.updateProblemData(problemPackage.getProblemData());
    }

    @Override
    public String showStdCode(Long id) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(id);
        return problemPackage.getProblemData().getStdCode().getCode();
    }

    @Override
    public String showTestCode(ProblemNameRequest problemNameRequest) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemNameRequest.getId());
        return problemPackage.getProblemData().findStdCode(problemNameRequest.getName()).getCode();
    }

    @Override
    public void downloadStdCode(Long id, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(id);
        StreamUtils.write(problemPackage.getProblemData().getStdCode().getCode(), outputStream);
    }

    @Override
    public void downloadTestCode(ProblemNameRequest problemNameRequest, OutputStream outputStream) throws PortableException {
        ProblemPackage problemPackage = getEditProblem(problemNameRequest.getId());
        StreamUtils.write(problemPackage.getProblemData().findStdCode(problemNameRequest.getName()).getCode(), outputStream);
    }

    @Override
    public void treatAndCheckProblem(Long id) throws PortableException {
        // TODO treat it!
    }

    private ProblemPackage getViewProblem(Long id) throws PortableException {
        Problem problem = problemManager.getProblemById(id);
        User2ProblemAccessType accessType = User2ProblemAccessType.of(problem);
        if (accessType.getViewProblem()) {
            ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
            return ProblemPackage.builder()
                    .problem(problem)
                    .problemData(problemData)
                    .build();
        }
        throw PortableException.of("A-02-004", id);
    }

    private ProblemPackage getViewProblemTest(Long id) throws PortableException {
        Problem problem = problemManager.getProblemById(id);
        User2ProblemAccessType accessType = User2ProblemAccessType.of(problem);
        if (!accessType.getEditProblem() && !accessType.getViewProblem()) {
            throw PortableException.of("A-02-004", id);
        }
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        if (accessType.getEditProblem() || problemData.getShareTest()) {
            return ProblemPackage.builder()
                    .problem(problem)
                    .problemData(problemData)
                    .build();
        }
        throw PortableException.of("A-02-006");
    }

    private ProblemPackage getEditProblem(Long id) throws PortableException {
        Problem problem = problemManager.getProblemById(id);
        User2ProblemAccessType accessType = User2ProblemAccessType.of(problem);
        if (accessType.getEditProblem()) {
            ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
            return ProblemPackage.builder()
                    .problem(problem)
                    .problemData(problemData)
                    .build();
        }
        User user = accountManager.getAccountById(problem.getOwner());
        throw PortableException.of("A-02-005", id, user.getHandle());
    }
}
