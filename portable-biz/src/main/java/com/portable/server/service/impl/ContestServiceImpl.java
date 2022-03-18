package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.BatchManager;
import com.portable.server.manager.ContestDataManager;
import com.portable.server.manager.ContestManager;
import com.portable.server.manager.ProblemDataManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.ContestRankItem;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAddProblem;
import com.portable.server.model.request.contest.ContestAuth;
import com.portable.server.model.request.contest.ContestContentRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestInfoResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.contest.ContestRankListResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.User;
import com.portable.server.service.ContestService;
import com.portable.server.support.ContestSupport;
import com.portable.server.support.JudgeSupport;
import com.portable.server.type.AccountType;
import com.portable.server.type.ContestAccessType;
import com.portable.server.type.ContestVisitPermission;
import com.portable.server.type.PermissionType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.UserContext;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author shiroha
 */
@Component
public class ContestServiceImpl implements ContestService {

    @Data
    @Builder
    public static class ContestPackage {
        private Contest contest;
        private BaseContestData contestData;
    }

    @Resource
    private ContestManager contestManager;

    @Resource
    private ContestDataManager contestDataManager;

    @Resource
    private UserManager userManager;

    @Resource
    private ProblemManager problemManager;

    @Resource
    private ProblemDataManager problemDataManager;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private SolutionDataManager solutionDataManager;

    @Resource
    private BatchManager batchManager;

    @Resource
    private JudgeSupport judgeSupport;

    @Resource
    private ContestSupport contestSupport;

    @Override
    public PageResponse<ContestListResponse, Void> getContestList(PageRequest<Void> pageRequest) {
        Integer contestCount = contestManager.getAllContestNumber();
        PageResponse<ContestListResponse, Void> response = PageResponse.of(pageRequest, contestCount);
        List<Contest> contestList = contestManager.getContestByPage(response.getPageSize(), response.offset());
        List<ContestListResponse> contestListResponseList = contestList.stream()
                .map(ContestListResponse::of)
                .collect(Collectors.toList());
        response.setData(contestListResponseList);
        return response;
    }

    @Override
    public ContestVisitPermission authorizeContest(ContestAuth contestAuth) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestAuth.getContestId());
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);

        // 如果已经至少是参与者了，则返回
        if (ContestVisitPermission.PARTICIPANT.approve(contestVisitPermission)) {
            return contestVisitPermission;
        }

        // 没有密码，说明只是来尝试验证访问权限
        if (contestAuth.getPassword() == null) {
            return contestVisitPermission;
        }

        // 通过密码可以验证用户是否可以参与到这个比赛中
        if (ContestAccessType.PASSWORD.equals(contestPackage.getContest().getAccessType())) {
            PasswordContestData contestData = (PasswordContestData) contestPackage.getContestData();
            if (!Objects.equals(contestData.getPassword(), contestAuth.getPassword())) {
                throw PortableException.of("A-08-003");
            }
            UserContext.addCurUserContestVisit(contestAuth.getContestId(), ContestVisitPermission.PARTICIPANT);
            return ContestVisitPermission.PARTICIPANT;
        }
        return contestVisitPermission;
    }

    @Override
    public ContestInfoResponse getContestInfo(Long contestId) throws PortableException {
        return getContestDetail(contestId, true, false);
    }

    @Override
    public ContestDetailResponse getContestData(Long contestId) throws PortableException {
        return (ContestDetailResponse) getContestDetail(contestId, false, false);
    }

    @Override
    public ContestAdminDetailResponse getContestAdminData(Long contestId) throws PortableException {
        return (ContestAdminDetailResponse) getContestDetail(contestId, false, true);
    }

    @Override
    public ProblemDetailResponse getContestProblem(Long contestId, Integer problemIndex) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.VISIT.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-004", contestId);
        }
        // 比赛开始前，仅管理员可以查看比赛内容
        if (!contestPackage.getContest().isStarted() && !ContestVisitPermission.CO_AUTHOR.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-019", contestId);
        }
        if (problemIndex >= contestPackage.getContestData().getProblemList().size() || problemIndex < 0) {
            throw PortableException.of("A-08-018", contestId, problemIndex);
        }
        BaseContestData.ContestProblemData contestProblemData = contestPackage.getContestData().getProblemList().get(problemIndex);
        Problem problem = problemManager.getProblemById(contestProblemData.getProblemId());
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        User user = userManager.getAccountById(problem.getOwner());
        ProblemDetailResponse problemDetailResponse = ProblemDetailResponse.of(problem, problemData, user);
        problemDetailResponse.setId(Long.valueOf(problemIndex));
        return problemDetailResponse;
    }

    @Override
    public PageResponse<SolutionListResponse, Void> getContestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.VISIT.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-004", contestId);
        }

        SolutionListQueryRequest queryData = pageRequest.getQueryData();

        if (queryData.getProblemId() != null) {
            queryData.setProblemId(contestPackage.getContestData()
                    .getProblemList()
                    .get(Math.toIntExact(queryData.getProblemId()))
                    .getProblemId());
        }

        Integer solutionCount = solutionManager.countSolution(
                SolutionType.CONTEST,
                queryData.getUserId(),
                contestId,
                queryData.getProblemId(),
                queryData.getStatusType()
        );
        PageResponse<SolutionListResponse, Void> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectSolutionByPage(
                response.getPageSize(),
                response.offset(),
                SolutionType.CONTEST,
                queryData.getUserId(),
                contestId,
                queryData.getProblemId(),
                queryData.getStatusType()
        );
        @SuppressWarnings("DuplicatedCode")
        Map<Long, Integer> problemIdToProblemIndexMap = contestPackage.getContestData().idToIndex();
        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
                .map(solution -> {
                    User user = userManager.getAccountById(solution.getUserId());
                    Problem problem = problemManager.getProblemById(solution.getProblemId());
                    SolutionListResponse solutionListResponse = SolutionListResponse.of(solution, user, problem);
                    solutionListResponse.setProblemId(Long.valueOf(problemIdToProblemIndexMap.get(problem.getId())));
                    return solutionListResponse;
                })
                .collect(Collectors.toList());
        response.setData(solutionListResponseList);
        return response;
    }

    @Override
    public SolutionDetailResponse getContestSolution(Long solutionId) throws PortableException {
        Solution solution = solutionManager.selectSolutionById(solutionId);
        ContestPackage contestPackage = getContestPackage(solution.getContestId());
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.VISIT.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-004", contestPackage.getContest().getId());
        }

        Map<Long, Integer> problemIdToProblemIndexMap = contestPackage.getContestData().idToIndex();
        // 比赛结束之后，所有人都可以查看其他人的提交。否则，仅本人和管理员可以查看
        Long curUserId = UserContext.ctx().getId();
        boolean endContest = contestPackage.getContest().isEnd();
        boolean self = Objects.equals(solution.getUserId(), curUserId);
        boolean admin = Objects.equals(contestPackage.getContest().getOwner(), curUserId)
                || contestPackage.getContestData().getCoAuthor().contains(curUserId);
        if (endContest || self || admin) {
            SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
            User user = userManager.getAccountById(solution.getUserId());
            Problem problem = problemManager.getProblemById(solution.getProblemId());
            SolutionDetailResponse solutionDetailResponse = SolutionDetailResponse.of(solution, solutionData, user, problem, admin);
            solutionDetailResponse.setProblemId(Long.valueOf(problemIdToProblemIndexMap.get(problem.getId())));
            return solutionDetailResponse;
        }
        throw PortableException.of("A-08-005", solutionId);
    }

    @Override
    public PageResponse<SolutionListResponse, Void> getContestTestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.CO_AUTHOR.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-006", contestId);
        }

        SolutionListQueryRequest queryData = pageRequest.getQueryData();

        if (queryData.getProblemId() != null) {
            queryData.setProblemId(contestPackage.getContestData()
                    .getProblemList()
                    .get(Math.toIntExact(queryData.getProblemId()))
                    .getProblemId());
        }

        Integer solutionCount = solutionManager.countSolution(
                SolutionType.TEST_CONTEST,
                queryData.getUserId(),
                contestId,
                queryData.getProblemId(),
                queryData.getStatusType()
        );
        PageResponse<SolutionListResponse, Void> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectSolutionByPage(
                response.getPageSize(),
                response.offset(),
                SolutionType.TEST_CONTEST,
                queryData.getUserId(),
                contestId,
                queryData.getProblemId(),
                queryData.getStatusType()
        );
        @SuppressWarnings("DuplicatedCode")
        Map<Long, Integer> problemIdToProblemIndexMap = contestPackage.getContestData().idToIndex();
        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
                .map(solution -> {
                    User user = userManager.getAccountById(solution.getUserId());
                    Problem problem = problemManager.getProblemById(solution.getProblemId());
                    SolutionListResponse solutionListResponse = SolutionListResponse.of(solution, user, problem);
                    solutionListResponse.setProblemId(Long.valueOf(problemIdToProblemIndexMap.get(problem.getId())));
                    return solutionListResponse;
                })
                .collect(Collectors.toList());
        response.setData(solutionListResponseList);
        return response;
    }

    @Override
    public SolutionDetailResponse getContestTestSolution(Long solutionId) throws PortableException {
        Solution solution = solutionManager.selectSolutionById(solutionId);
        ContestPackage contestPackage = getContestPackage(solution.getContestId());
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.CO_AUTHOR.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-006", contestPackage.getContest().getId());
        }

        Map<Long, Integer> problemIdToProblemIndexMap = contestPackage.getContestData().idToIndex();
        SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
        User user = userManager.getAccountById(solution.getUserId());
        Problem problem = problemManager.getProblemById(solution.getProblemId());
        SolutionDetailResponse solutionDetailResponse = SolutionDetailResponse.of(solution, solutionData, user, problem, true);
        solutionDetailResponse.setProblemId(Long.valueOf(problemIdToProblemIndexMap.get(problem.getId())));
        return solutionDetailResponse;
    }

    @Override
    public PageResponse<ContestRankListResponse, ContestRankListResponse> getContestRank(Long contestId, PageRequest<Void> pageRequest) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.VISIT.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-004", contestId);
        }
        contestSupport.ensureRank(contestId);
        Integer totalNum = contestSupport.getContestRankLen(contestId);
        PageResponse<ContestRankListResponse, ContestRankListResponse> response = PageResponse.of(pageRequest, totalNum);
        List<ContestRankItem> contestRankItemList = contestSupport.getContestRank(contestId, response.getPageSize(), response.offset());
        List<ContestRankListResponse> contestRankListResponseList = contestRankItemList.stream()
                .map(contestRankItem -> {
                    User user = userManager.getAccountById(contestRankItem.getUserId());
                    if (user == null) {
                        return ContestRankListResponse.of(contestRankItem, "");
                    }
                    return ContestRankListResponse.of(contestRankItem, user.getHandle());
                })
                .collect(Collectors.toList());
        UserContext userContext = UserContext.ctx();
        ContestRankItem userItem = contestSupport.getContestByUserId(contestId, userContext.getId());
        ContestRankListResponse metaData = null;
        if (userItem != null) {
            metaData = ContestRankListResponse.of(userItem, userContext.getHandle());
        }
        response.setData(contestRankListResponseList);
        response.setMetaData(metaData);
        return response;
    }

    @Override
    public Long submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        ContestPackage contestPackage = getContestPackage(submitSolutionRequest.getContestId());
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.PARTICIPANT.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-007", submitSolutionRequest.getContestId());
        }

        // 普通参赛的仅允许在比赛开始期间进行提交
        if (ContestVisitPermission.PARTICIPANT.equals(contestVisitPermission)) {
            if (!contestPackage.getContest().isStarted() || contestPackage.getContest().isEnd()) {
                throw PortableException.of("A-08-008", submitSolutionRequest.getContestId());
            }
        }

        BaseContestData.ContestProblemData contestProblemData = contestPackage.getContestData()
                .getProblemList()
                .get(Math.toIntExact(submitSolutionRequest.getProblemId()));

        Problem problem = problemManager.getProblemById(contestProblemData.getProblemId());
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        submitSolutionRequest.setProblemId(contestProblemData.getProblemId());

        Solution solution = solutionManager.newSolution();
        SolutionData solutionData = solutionDataManager.newSolutionData(problemData);
        submitSolutionRequest.toSolution(solution);
        submitSolutionRequest.toSolutionData(solutionData);

        // 普通参赛选手提交至普通提交列表
        if (ContestVisitPermission.PARTICIPANT.equals(contestVisitPermission)) {
            contestProblemData.setSubmissionCount(contestProblemData.getSubmissionCount() + 1);
            contestDataManager.saveContestData(contestPackage.getContestData());
            solution.setSolutionType(SolutionType.CONTEST);
        } else {
            solution.setSolutionType(SolutionType.TEST_CONTEST);
        }
        solutionDataManager.insertSolutionData(solutionData);
        solution.setDataId(solutionData.get_id());
        solution.setUserId(UserContext.ctx().getId());
        solutionManager.insertSolution(solution);
        judgeSupport.addJudgeTask(solution.getId());
        return solution.getId();
    }

    @Override
    public Long createContest(ContestContentRequest contestContentRequest) throws PortableException {
        Contest contest = contestManager.insertContest();
        BaseContestData contestData = contestDataManager.newContestData(contestContentRequest.getAccessType());
        contestContentRequest.toContest(contest);
        contest.setOwner(UserContext.ctx().getId());
        checkSameProblem(contestContentRequest);
        setContestContentToContestData(contestContentRequest, contestData);

        contestDataManager.insertContestData(contestData);
        contest.setDataId(contestData.get_id());
        contestManager.insertContest(contest);

        ContestPackage contestPackage = ContestPackage.builder()
                .contest(contest)
                .contestData(contestData)
                .build();
        updateContestLock(contestPackage, contestContentRequest.getProblemList());
        return contest.getId();
    }

    @Override
    public void updateContest(ContestContentRequest contestContentRequest) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestContentRequest.getId());
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.ADMIN.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-011", contestContentRequest.getId());
        }

        Contest contest = contestPackage.getContest();
        BaseContestData contestData = contestPackage.getContestData();

        // 根据状态修改参赛条件。注意，任何时候都不可以修改题目的权限配置
        contestContentRequest.setAccessType(contest.getAccessType());
        if (!contest.isStarted()) {
            // 未开始
            contestContentRequest.toContest(contest);
            if (contest.isStarted()) {
                throw PortableException.of("A-08-012");
            }
            checkSameProblem(contestContentRequest);
            setContestContentToContestData(contestContentRequest, contestData);
            contestManager.updateStartTime(contestContentRequest.getId(), contestContentRequest.getStartTime());
            contestManager.updateDuration(contestContentRequest.getId(), contestContentRequest.getDuration());
            contestDataManager.saveContestData(contestData);
            updateContestLock(contestPackage, contestContentRequest.getProblemList());
        } else if (contest.isStarted() && !contest.isEnd()) {
            // 已经开始的比赛不允许修改比赛开始时间
            contestContentRequest.setStartTime(contest.getStartTime());
            // 检查新的比赛结束时间是否已经超过当前时间了
            contestContentRequest.toContest(contest);
            if (contest.isEnd()) {
                throw PortableException.of("A-08-013");
            }
            checkSameProblem(contestContentRequest);
            contestManager.updateDuration(contestContentRequest.getId(), contestContentRequest.getDuration());
            // 下面的函数已经保证了不会删除题目
            contestContentRequest.toContestData(contestData);
            contestDataManager.saveContestData(contestData);
            updateContestLock(contestPackage, contestContentRequest.getProblemList());
        } else {
            // 比赛已经结束，仅允许修改比赛公告
            contestData.setAnnouncement(contestContentRequest.getAnnouncement());
            contestDataManager.saveContestData(contestData);
        }
    }

    @Override
    public void addContestProblem(ContestAddProblem contestAddProblem) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestAddProblem.getContestId());
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.CO_AUTHOR.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-014", contestAddProblem.getContestId());
        }
        // 仅允许添加自己拥有的，且为私有的题目
        Problem problem = problemManager.getProblemById(contestAddProblem.getProblemId());
        if (problem == null) {
            throw PortableException.of("A-08-017");
        }
        if (!Objects.equals(problem.getOwner(), UserContext.ctx().getId())
                || !ProblemAccessType.PRIVATE.equals(problem.getAccessType())) {
            throw PortableException.of("A-08-015");
        }
        boolean isExistProblem = contestPackage.getContestData().getProblemList()
                .stream()
                .anyMatch(contestProblemData -> Objects.equals(contestProblemData.getProblemId(), contestAddProblem.getProblemId()));
        if (isExistProblem) {
            throw PortableException.of("A-08-016");
        }
        contestPackage.getContestData().getProblemList().add(new BaseContestData.ContestProblemData(contestAddProblem.getProblemId()));
        contestDataManager.saveContestData(contestPackage.getContestData());
        updateContestLock(contestPackage, Collections.singletonList(contestAddProblem.getProblemId()));
    }

    private ContestPackage getContestPackage(Long contestId) throws PortableException {
        Contest contest = contestManager.getContestById(contestId);
        if (contest == null) {
            throw PortableException.of("A-08-002", contestId);
        }
        BaseContestData contestData;
        switch (contest.getAccessType()) {
            case PUBLIC:
                contestData = contestDataManager.getPublicContestDataById(contest.getDataId());
                break;
            case PASSWORD:
                contestData = contestDataManager.getPasswordContestDataById(contest.getDataId());
                break;
            case PRIVATE:
                contestData = contestDataManager.getPrivateContestDataById(contest.getDataId());
                break;
            case BATCH:
                contestData = contestDataManager.getBatchContestDataById(contest.getDataId());
                break;
            default:
                throw PortableException.of("A-08-001", contest.getAccessType());
        }
        return ContestPackage.builder()
                .contest(contest)
                .contestData(contestData)
                .build();
    }

    /**
     * 验证题目的访问权限，并获取题目信息
     *
     * @param contestId 比赛 id
     * @param info      只需要比赛简介，不需要详情
     * @param admin     是否是管理员查看
     * @return 比赛详情
     * @throws PortableException 出现非法访问则抛出错误
     */
    public ContestInfoResponse getContestDetail(Long contestId, Boolean info, Boolean admin) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        boolean noAdminPermission = admin && !ContestVisitPermission.CO_AUTHOR.approve(contestVisitPermission);
        if (noAdminPermission || !ContestVisitPermission.VISIT.approve(contestVisitPermission)) {
            throw PortableException.of("A-08-004", contestId);
        }

        // 校验比赛是否开始
        if (!info) {
            if (!ContestVisitPermission.CO_AUTHOR.approve(contestVisitPermission) && !contestPackage.getContest().isStarted()) {
                throw PortableException.of("A-08-019", contestId);
            }
        }

        // 获取主办方和出题人的名称
        User owner = userManager.getAccountById(contestPackage.getContest().getOwner());
        Set<String> coAuthor = contestPackage.getContestData().getCoAuthor().stream()
                .parallel()
                .map(aLong -> {
                    User author = userManager.getAccountById(aLong);
                    return author == null ? "" : author.getHandle();
                })
                .collect(Collectors.toSet());

        // 获取题目信息
        UserContext userContext = UserContext.ctx();
        List<Boolean> problemLock = new ArrayList<>();

        List<ProblemListResponse> problemListResponses = null;
        if (!info) {
            problemListResponses = IntStream.range(0, contestPackage.getContestData().getProblemList().size())
                    .mapToObj(i -> {
                        BaseContestData.ContestProblemData contestProblemData = contestPackage.getContestData().getProblemList().get(i);
                        Problem problem = problemManager.getProblemById(contestProblemData.getProblemId());
                        if (problem == null) {
                            return null;
                        }
                        Solution solution = solutionManager.selectLastSolutionByUserIdAndProblemIdAndContestId(userContext.getId(), problem.getId(), contestId);
                        ProblemListResponse problemListResponse = ProblemListResponse.of(problem, solution);
                        problemListResponse.setAcceptCount(contestProblemData.getAcceptCount());
                        problemListResponse.setSubmissionCount(contestProblemData.getSubmissionCount());

                        // 将每道题目的序号设置为比赛中的序号
                        problemListResponse.setId((long) i);
                        if (admin) {
                            ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
                            problemLock.add(Objects.equals(problemData.getContestId(), contestId));
                        }
                        return problemListResponse;
                    })
                    .collect(Collectors.toList());
            if (problemListResponses.contains(null)) {
                throw PortableException.of("S-07-001", contestId);
            }
        }
        if (info) {
            return ContestInfoResponse.of(contestPackage.getContest(), contestPackage.getContestData(), owner.getHandle(), coAuthor);
        }
        if (admin) {
            return getContestDetailAdmin(contestPackage, owner, problemListResponses, problemLock, coAuthor);
        }
        return ContestAdminDetailResponse.of(contestPackage.getContest(),
                contestPackage.getContestData(),
                owner.getHandle(),
                problemListResponses,
                coAuthor);
    }

    public ContestDetailResponse getContestDetailAdmin(ContestPackage contestPackage,
                                                       User owner,
                                                       List<ProblemListResponse> problemListResponses,
                                                       List<Boolean> problemLock,
                                                       Set<String> coAuthor) throws PortableException {
        Set<String> inviteUserSet = null;
        switch (contestPackage.getContest().getAccessType()) {
            case PUBLIC:
            case PASSWORD:
            case BATCH:
                break;
            case PRIVATE:
                PrivateContestData privateContestData = (PrivateContestData) contestPackage.getContestData();
                inviteUserSet = privateContestData.getInviteUserSet().stream()
                        .map(aLong -> {
                            User user = userManager.getAccountById(aLong);
                            if (user == null) {
                                return null;
                            }
                            return user.getHandle();
                        })
                        .filter(s -> !Objects.isNull(s))
                        .collect(Collectors.toSet());
                break;
            default:
                throw PortableException.of("A-08-001", contestPackage.getContest().getAccessType());
        }
        return ContestAdminDetailResponse.of(contestPackage.getContest(),
                contestPackage.getContestData(),
                owner.getHandle(),
                problemListResponses,
                coAuthor,
                problemLock,
                inviteUserSet);
    }

    private ContestVisitPermission checkPermission(ContestPackage contestPackage) {
        Contest contest = contestPackage.getContest();
        BaseContestData contestData = contestPackage.getContestData();
        UserContext userContext = UserContext.ctx();
        ContestVisitPermission contestVisitPermission = userContext.getContestVisitPermissionMap().get(contest.getId());
        if (contestVisitPermission != null) {
            return contestVisitPermission;
        }
        // 检查拥有者和出题人情况
        if (Objects.equals(contest.getOwner(), userContext.getId())) {
            contestVisitPermission = ContestVisitPermission.ADMIN;
        } else if (contestData.getCoAuthor().contains(userContext.getId())) {
            contestVisitPermission = ContestVisitPermission.CO_AUTHOR;
        } else {
            // 根据比赛的类型判断状态
            switch (contest.getAccessType()) {
                case PUBLIC:
                    contestVisitPermission = ContestVisitPermission.PARTICIPANT;
                    break;
                case PRIVATE:
                    PrivateContestData privateContestData = (PrivateContestData) contestData;
                    contestVisitPermission = privateContestData.getInviteUserSet().contains(userContext.getId())
                            ? ContestVisitPermission.PARTICIPANT
                            : ContestVisitPermission.NO_ACCESS;
                    break;
                case BATCH:
                    contestVisitPermission = (AccountType.BATCH.equals(userContext.getType()) && Objects.equals(userContext.getContestId(), contest.getId()))
                            ? ContestVisitPermission.PARTICIPANT
                            : ContestVisitPermission.NO_ACCESS;
                    break;
                case PASSWORD:
                default:
                    contestVisitPermission = ContestVisitPermission.NO_ACCESS;
                    break;
            }
        }
        // 根据用户所具有的权利判断
        if (userContext.getPermissionTypeSet().contains(PermissionType.EDIT_NOT_OWNER_CONTEST)) {
            contestVisitPermission = ContestVisitPermission.ADMIN;
        }
        if (!ContestVisitPermission.VISIT.approve(contestVisitPermission)
                && userContext.getPermissionTypeSet().contains(PermissionType.VIEW_ALL_CONTEST)) {
            contestVisitPermission = ContestVisitPermission.VISIT;
        }
        userContext.addContestVisit(contest.getId(), contestVisitPermission);
        return contestVisitPermission;
    }

    private void checkSameProblem(ContestContentRequest contestContentRequest) throws PortableException {
        // 校验题目是否有重复
        Set<Long> problemIdSet = new HashSet<>(contestContentRequest.getProblemList());
        if (!Objects.equals(problemIdSet.size(), contestContentRequest.getProblemList().size())) {
            throw PortableException.of("A-08-020");
        }
        // 校验题目是否都存在
        List<Long> notExistProblemList = problemManager.checkProblemListExist(contestContentRequest.getProblemList());
        if (!notExistProblemList.isEmpty()) {
            String notExistProblem = notExistProblemList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw PortableException.of("A-08-010", notExistProblem);
        }
    }

    private void setContestContentToContestData(ContestContentRequest contestContentRequest, BaseContestData contestData) throws PortableException {
        // 过滤掉不存在的邀请用户和邀请的合作出题人
        Set<Long> coAuthorIdSet = userManager.changeUserHandleToUserId(contestContentRequest.getCoAuthor())
                .filter(aLong -> !Objects.isNull(aLong))
                .collect(Collectors.toSet());
        Set<Long> inviteUserIdSet = null;
        if (contestContentRequest.getInviteUserSet() != null) {
            inviteUserIdSet = userManager.changeUserHandleToUserId(contestContentRequest.getInviteUserSet())
                    .filter(aLong -> !Objects.isNull(aLong))
                    .collect(Collectors.toSet());
        }
        Long lastBatchId = null;
        // 校验批量用户组是否存在
        if (ContestAccessType.BATCH.equals(contestContentRequest.getAccessType()) && contestContentRequest.getBatchId() != null) {
            Batch batch = batchManager.selectBatchById(contestContentRequest.getBatchId());
            if (batch == null) {
                throw PortableException.of("A-10-006", contestContentRequest.getBatchId());
            }
            if (!Objects.equals(batch.getOwner(), UserContext.ctx().getId())) {
                throw PortableException.of("A-10-008");
            }

            lastBatchId = ((BatchContestData) contestData).getBatchId();
        }
        List<Long> lastProblemList = contestData.getProblemList().stream()
                .map(BaseContestData.ContestProblemData::getProblemId)
                .collect(Collectors.toList());

        contestContentRequest.toContestData(contestData, coAuthorIdSet, inviteUserIdSet);

        // 在数据完成输入后，再解除旧题目的锁定状态
        lastProblemList.stream()
                .parallel()
                .forEach(aLong -> {
                    // 已经校验过题目是否合法了
                    Problem problem = problemManager.getProblemById(aLong);
                    ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
                    if (Objects.equals(problemData.getContestId(), contestContentRequest.getId())) {
                        problemData.setContestId(null);
                        problemDataManager.updateProblemData(problemData);
                    }
                });

        if (lastBatchId != null) {
            batchManager.updateBatchContest(lastBatchId, null);
        }
    }

    private void updateContestLock(ContestPackage contestPackage, List<Long> problemIdList) {
        // 锁定题目状态
        problemIdList.stream()
                .parallel()
                .forEach(aLong -> {
                    Problem problem = problemManager.getProblemById(aLong);
                    ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
                    if (problemData.getContestId() == null) {
                        problemData.setContestId(contestPackage.contest.getId());
                        problemDataManager.updateProblemData(problemData);
                    }
                });
        // 若为提供账号的比赛，则修改批量账号的绑定权限
        if (ContestAccessType.BATCH.equals(contestPackage.getContest().getAccessType())) {
            batchManager.updateBatchContest(
                    ((BatchContestData) contestPackage.getContestData()).getBatchId(),
                    contestPackage.getContest().getId()
            );
        }
    }
}
