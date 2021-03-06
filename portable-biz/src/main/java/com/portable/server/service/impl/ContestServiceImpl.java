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
import com.portable.server.model.request.contest.ContestRankPageRequest;
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
import com.portable.server.type.ContestAccessType;
import com.portable.server.type.ContestVisitType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.UserContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    @NoArgsConstructor
    @AllArgsConstructor
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
    public ContestVisitType authorizeContest(ContestAuth contestAuth) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestAuth.getContestId());
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());

        // ?????????????????????????????????????????????
        if (ContestVisitType.PARTICIPANT.approve(contestVisitType)) {
            return contestVisitType;
        }

        // ??????????????????????????????????????????????????????
        if (contestAuth.getPassword() == null) {
            return contestVisitType;
        }

        // ??????????????????????????????????????????????????????????????????
        if (ContestAccessType.PASSWORD.equals(contestPackage.getContest().getAccessType())) {
            PasswordContestData contestData = (PasswordContestData) contestPackage.getContestData();
            if (!Objects.equals(contestData.getPassword(), contestAuth.getPassword())) {
                throw PortableException.of("A-08-003");
            }
            UserContext.addCurUserContestVisit(contestAuth.getContestId(), ContestVisitType.PARTICIPANT);
            return ContestVisitType.PARTICIPANT;
        }
        return contestVisitType;
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
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.VISIT.approve(contestVisitType)) {
            throw PortableException.of("A-08-004", contestId);
        }
        // ??????????????????????????????????????????????????????
        if (!contestPackage.getContest().isStarted() && !ContestVisitType.CO_AUTHOR.approve(contestVisitType)) {
            throw PortableException.of("A-08-019", contestId);
        }
        BaseContestData.ContestProblemData contestProblemData = contestPackage.getContestData().atProblem(problemIndex, contestId);
        Problem problem = problemManager.getProblemById(contestProblemData.getProblemId())
                .orElseThrow(PortableException.from("S-07-001", contestId));
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        User user = userManager.getAccountById(problem.getOwner()).orElse(null);
        ProblemDetailResponse problemDetailResponse = ProblemDetailResponse.of(problem, problemData, user);
        problemDetailResponse.setAcceptCount(contestProblemData.getAcceptCount());
        problemDetailResponse.setSubmissionCount(contestProblemData.getSubmissionCount());
        problemDetailResponse.setId(Long.valueOf(problemIndex));
        return problemDetailResponse;
    }

    @Override
    public PageResponse<SolutionListResponse, Void> getContestStatusList(Long contestId,
                                                                         PageRequest<SolutionListQueryRequest> pageRequest)
            throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.VISIT.approve(contestVisitType)) {
            throw PortableException.of("A-08-004", contestId);
        }
        return getSolutionList(contestPackage, pageRequest, SolutionType.CONTEST);
    }

    @Override
    public SolutionDetailResponse getContestSolution(Long solutionId)
            throws PortableException {
        Solution solution = solutionManager.selectSolutionById(solutionId)
                .orElseThrow(PortableException.from("A-05-001", solutionId));
        ContestPackage contestPackage = getContestPackage(solution.getContestId());
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.VISIT.approve(contestVisitType)) {
            throw PortableException.of("A-08-004", contestPackage.getContest().getId());
        }

        Map<Long, Integer> problemIdToProblemIndexMap = contestPackage.getContestData().idToIndex();
        // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
        Long curUserId = UserContext.ctx().getId();
        boolean endContest = contestPackage.getContest().isEnd();
        boolean self = Objects.equals(solution.getUserId(), curUserId);
        boolean admin = ContestVisitType.CO_AUTHOR.approve(contestVisitType);
        if (endContest || self || admin) {
            SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
            User user = userManager.getAccountById(solution.getUserId()).orElse(null);
            Problem problem = problemManager.getProblemById(solution.getProblemId()).orElse(null);
            SolutionDetailResponse solutionDetailResponse = SolutionDetailResponse.of(solution, solutionData, user, problem, admin);
            solutionDetailResponse.setProblemId(Long.valueOf(problemIdToProblemIndexMap.get(solution.getProblemId())));
            return solutionDetailResponse;
        }
        throw PortableException.of("A-08-005", solutionId);
    }

    @Override
    public PageResponse<SolutionListResponse, Void> getContestTestStatusList(Long contestId,
                                                                             PageRequest<SolutionListQueryRequest> pageRequest)
            throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.CO_AUTHOR.approve(contestVisitType)) {
            throw PortableException.of("A-08-006", contestId);
        }
        return getSolutionList(contestPackage, pageRequest, SolutionType.TEST_CONTEST);
    }

    @Override
    public SolutionDetailResponse getContestTestSolution(Long solutionId) throws PortableException {
        Solution solution = solutionManager.selectSolutionById(solutionId)
                .orElseThrow(PortableException.from("A-05-001", solutionId));
        ContestPackage contestPackage = getContestPackage(solution.getContestId());
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.CO_AUTHOR.approve(contestVisitType)) {
            throw PortableException.of("A-08-006", contestPackage.getContest().getId());
        }

        Map<Long, Integer> problemIdToProblemIndexMap = contestPackage.getContestData().idToIndex();
        SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
        User user = userManager.getAccountById(solution.getUserId()).orElse(null);
        Problem problem = problemManager.getProblemById(solution.getProblemId()).orElse(null);
        SolutionDetailResponse solutionDetailResponse = SolutionDetailResponse.of(solution, solutionData, user, problem, true);
        solutionDetailResponse.setProblemId(Long.valueOf(problemIdToProblemIndexMap.get(solution.getProblemId())));
        return solutionDetailResponse;
    }

    @Override
    public PageResponse<ContestRankListResponse, ContestRankListResponse> getContestRank(Long contestId,
                                                                                         PageRequest<ContestRankPageRequest> pageRequest)
            throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.VISIT.approve(contestVisitType)) {
            throw PortableException.of("A-08-004", contestId);
        }

        Boolean freeze = pageRequest.getQueryData().getFreeze();
        if (!freeze && !ContestVisitType.CO_AUTHOR.approve(contestVisitType)) {
            throw PortableException.of("A-08-034", contestId);
        }
        contestSupport.ensureRank(contestId);

        if (Integer.valueOf(0).equals(contestPackage.getContestData().getFreezeTime())) {
            freeze = true;
        }

        // ???????????????
        Integer totalNum = contestSupport.getContestRankLen(contestId, freeze);
        PageResponse<ContestRankListResponse, ContestRankListResponse> response = PageResponse.of(pageRequest, totalNum);
        List<ContestRankItem> contestRankItemList = contestSupport.getContestRank(contestId, response.getPageSize(), response.offset(), freeze);

        final Boolean finalFreeze = freeze;
        List<ContestRankListResponse> contestRankListResponseList = contestRankItemList.stream()
                .map(contestRankItem -> {
                    User user = userManager.getAccountById(contestRankItem.getUserId()).orElse(null);
                    return ContestRankListResponse.of(contestRankItem, user, finalFreeze);
                })
                .collect(Collectors.toList());
        UserContext userContext = UserContext.ctx();
        ContestRankItem userItem = contestSupport.getContestByUserId(contestId, userContext.getId(), freeze);
        ContestRankListResponse metaData = null;
        if (userItem != null) {
            metaData = ContestRankListResponse.of(userItem, null, finalFreeze);
            metaData.setUserHandle(userContext.getHandle());
        }
        response.setData(contestRankListResponseList);
        response.setMetaData(metaData);
        return response;
    }

    @Override
    public Long submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        ContestPackage contestPackage = getContestPackage(submitSolutionRequest.getContestId());
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.PARTICIPANT.approve(contestVisitType)) {
            throw PortableException.of("A-08-007", submitSolutionRequest.getContestId());
        }

        // ?????????????????????????????????????????????????????????
        if (ContestVisitType.PARTICIPANT.equals(contestVisitType)) {
            if (!contestPackage.getContest().isStarted() || contestPackage.getContest().isEnd()) {
                throw PortableException.of("A-08-008", submitSolutionRequest.getContestId());
            }
        }

        BaseContestData.ContestProblemData contestProblemData = contestPackage.getContestData().atProblem(
                Math.toIntExact(submitSolutionRequest.getProblemId()),
                submitSolutionRequest.getContestId()
        );

        Problem problem = problemManager.getProblemById(contestProblemData.getProblemId())
                .orElseThrow(PortableException.from("S-07-001", submitSolutionRequest.getContestId()));
        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        submitSolutionRequest.setProblemId(contestProblemData.getProblemId());

        Solution solution = solutionManager.newSolution();
        SolutionData solutionData = solutionDataManager.newSolutionData(problemData);
        submitSolutionRequest.toSolution(solution);
        submitSolutionRequest.toSolutionData(solutionData);

        // ?????????????????????????????????????????????
        if (ContestVisitType.PARTICIPANT.equals(contestVisitType)) {
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
        Contest contest = contestManager.newContest();
        BaseContestData contestData = contestDataManager.newContestData(contestContentRequest.getAccessType());
        contestContentRequest.toContest(contest);
        contest.setOwner(UserContext.ctx().getId());
        checkContestData(contestContentRequest);
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
        Contest contest = contestPackage.getContest();
        BaseContestData contestData = contestPackage.getContestData();

        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contest, contestData);
        if (!ContestVisitType.ADMIN.approve(contestVisitType)) {
            throw PortableException.of("A-08-011", contestContentRequest.getId());
        }

        // ?????????????????????????????????????????????????????????????????????????????????????????????
        contestContentRequest.setAccessType(contest.getAccessType());
        if (!contest.isStarted()) {
            // ?????????
            contestContentRequest.toContest(contest);
            if (contest.isStarted()) {
                throw PortableException.of("A-08-012");
            }
            checkContestData(contestContentRequest);
            setContestContentToContestData(contestContentRequest, contestData);

            // ??????????????????
            contestManager.updateStartTime(contestContentRequest.getId(), contestContentRequest.getStartTime());
            contestManager.updateDuration(contestContentRequest.getId(), contestContentRequest.getDuration());
            contestManager.updateTitle(contestContentRequest.getId(), contestContentRequest.getTitle());

            contestDataManager.saveContestData(contestData);
            updateContestLock(contestPackage, contestContentRequest.getProblemList());
        } else if (contest.isStarted() && !contest.isEnd()) {
            // ??????????????????????????????????????????????????????
            contestContentRequest.setStartTime(contest.getStartTime());
            checkContestData(contestContentRequest);
            // ????????????????????????????????????????????????
            contestContentRequest.toContestData(contestData);
            contestDataManager.saveContestData(contestData);
            contestManager.updateDuration(contestContentRequest.getId(), contestContentRequest.getDuration());
            contestManager.updateTitle(contestContentRequest.getId(), contestContentRequest.getTitle());
            updateContestLock(contestPackage, contestContentRequest.getProblemList());
        } else {
            // ???????????????????????????????????????????????????????????????
            contestData.setAnnouncement(contestContentRequest.getAnnouncement());
            contestData.setFreezeTime(contestContentRequest.getFreezeTime());
            contestData.setPenaltyTime(contestContentRequest.getPenaltyTime());
            contestDataManager.saveContestData(contestData);
            contestManager.updateTitle(contestContentRequest.getId(), contestContentRequest.getTitle());
        }
    }

    @Override
    public void addContestProblem(ContestAddProblem contestAddProblem) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestAddProblem.getContestId());
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());
        if (!ContestVisitType.CO_AUTHOR.approve(contestVisitType)) {
            throw PortableException.of("A-08-014", contestAddProblem.getContestId());
        }
        // ??????????????????????????????????????????????????????
        Problem problem = problemManager.getProblemById(contestAddProblem.getProblemId())
                .orElseThrow(PortableException.from("A-08-017"));

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
        Contest contest = contestManager.getContestById(contestId)
                .orElseThrow(PortableException.from("A-08-002", contestId));
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
     * ???????????????????????????????????????????????????
     *
     * @param contestId ?????? id
     * @param info      ???????????????????????????????????????
     * @param admin     ????????????????????????
     * @return ????????????
     * @throws PortableException ?????????????????????????????????
     */
    private ContestInfoResponse getContestDetail(Long contestId, Boolean info, Boolean admin) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitType contestVisitType = ContestVisitType.checkPermission(contestPackage.getContest(), contestPackage.getContestData());

        boolean noVisitPermission = !ContestVisitType.VISIT.approve(contestVisitType);
        boolean noAdminPermission = admin && !ContestVisitType.CO_AUTHOR.approve(contestVisitType);
        if (noVisitPermission || noAdminPermission) {
            throw PortableException.of("A-08-004", contestId);
        }

        // ????????????????????????????????????????????????????????????????????????????????????????????????
        if (!info && !ContestVisitType.CO_AUTHOR.approve(contestVisitType) && !contestPackage.getContest().isStarted()) {
            throw PortableException.of("A-08-019", contestId);
        }

        // ????????????????????????????????????
        User owner = userManager.getAccountById(contestPackage.getContest().getOwner()).orElse(null);
        Set<User> coAuthor = contestPackage.getContestData().getCoAuthor().stream()
                .parallel()
                .map(aLong -> userManager.getAccountById(aLong).orElse(null))
                .collect(Collectors.toSet());

        if (info) {
            return ContestInfoResponse.of(contestPackage.getContest(), contestPackage.getContestData(), owner, coAuthor);
        }

        // ??????????????????
        UserContext userContext = UserContext.ctx();
        List<Boolean> problemLock = new ArrayList<>();

        List<ProblemListResponse> problemListResponses = IntStream.range(0, contestPackage.getContestData().getProblemList().size())
                .mapToObj(i -> {
                    BaseContestData.ContestProblemData contestProblemData = contestPackage.getContestData().getProblemList().get(i);
                    Optional<Problem> problemOptional = problemManager.getProblemById(contestProblemData.getProblemId());
                    // ???????????????
                    if (!problemOptional.isPresent()) {
                        return null;
                    }

                    Problem problem = problemOptional.get();

                    Solution solution = solutionManager.selectContestLastSolution(userContext.getId(), problem.getId(), contestId)
                            .orElse(null);
                    ProblemListResponse problemListResponse = ProblemListResponse.of(problem, solution);
                    problemListResponse.setAcceptCount(contestProblemData.getAcceptCount());
                    problemListResponse.setSubmissionCount(contestProblemData.getSubmissionCount());

                    // ???????????????????????????????????????????????????
                    problemListResponse.setId((long) i);
                    if (admin) {
                        try {
                            ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
                            problemLock.add(Objects.equals(problemData.getContestId(), contestId));
                        } catch (PortableException e) {
                            problemLock.add(false);
                        }
                    }
                    return problemListResponse;
                })
                .collect(Collectors.toList());
        if (problemListResponses.contains(null)) {
            throw PortableException.of("S-07-001", contestId);
        }
        if (admin) {
            return getContestDetailAdmin(contestPackage, owner, problemListResponses, problemLock, coAuthor);
        }
        return ContestDetailResponse.of(contestPackage.getContest(),
                contestPackage.getContestData(),
                owner,
                problemListResponses,
                coAuthor);
    }

    @NotNull
    private ContestAdminDetailResponse getContestDetailAdmin(@NotNull ContestPackage contestPackage,
                                                             User owner,
                                                             List<ProblemListResponse> problemListResponses,
                                                             List<Boolean> problemLock,
                                                             Set<User> coAuthor) throws PortableException {
        Set<User> inviteUserSet = null;
        switch (contestPackage.getContest().getAccessType()) {
            case PUBLIC:
            case PASSWORD:
            case BATCH:
                break;
            case PRIVATE:
                PrivateContestData privateContestData = (PrivateContestData) contestPackage.getContestData();
                inviteUserSet = privateContestData.getInviteUserSet().stream()
                        .map(aLong -> userManager.getAccountById(aLong).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                break;
            default:
                throw PortableException.of("A-08-001", contestPackage.getContest().getAccessType());
        }
        return ContestAdminDetailResponse.of(contestPackage.getContest(),
                contestPackage.getContestData(),
                owner,
                problemListResponses,
                coAuthor,
                problemLock,
                inviteUserSet);
    }

    private void checkContestData(@NotNull ContestContentRequest contestContentRequest) throws PortableException {
        // ???????????????????????????
        Set<Long> problemIdSet = new HashSet<>(contestContentRequest.getProblemList());
        // ????????????
        if (!Objects.equals(problemIdSet.size(), contestContentRequest.getProblemList().size())) {
            throw PortableException.of("A-08-020");
        }
        // ???????????????????????????
        List<Long> notExistProblemList = problemManager.checkProblemListExist(contestContentRequest.getProblemList());
        if (!notExistProblemList.isEmpty()) {
            String notExistProblem = notExistProblemList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw PortableException.of("A-08-010", notExistProblem);
        }

        // ?????????????????????????????????
        if (ContestAccessType.BATCH.equals(contestContentRequest.getAccessType())) {
            Batch batch = batchManager.selectBatchById(contestContentRequest.getBatchId())
                    .orElseThrow(PortableException.from("A-10-006", contestContentRequest.getBatchId()));
            if (!Objects.equals(batch.getOwner(), UserContext.ctx().getId())) {
                throw PortableException.of("A-10-008");
            }
        }
    }

    private void setContestContentToContestData(@NotNull ContestContentRequest contestContentRequest, BaseContestData contestData) throws PortableException {
        // ????????????????????????????????????????????????????????????
        Set<Long> coAuthorIdSet = userManager.changeHandleToUserId(contestContentRequest.getCoAuthor());

        Set<Long> inviteUserIdSet = null;
        if (ContestAccessType.PRIVATE.equals(contestContentRequest.getAccessType())) {
            inviteUserIdSet = userManager.changeHandleToUserId(contestContentRequest.getInviteUserSet());
        }

        // ??????????????????????????????????????????
        if (ContestAccessType.BATCH.equals(contestContentRequest.getAccessType())) {
            Long lastBatchId = ((BatchContestData) contestData).getBatchId();
            batchManager.updateBatchContest(lastBatchId, null);
        }

        List<Long> lastProblemList = contestData.getProblemList().stream()
                .map(BaseContestData.ContestProblemData::getProblemId)
                .collect(Collectors.toList());

        // ????????????????????????????????????????????????????????????
        setProblemContestId(lastProblemList, contestContentRequest.getId(), null);

        contestContentRequest.toContestData(contestData, coAuthorIdSet, inviteUserIdSet);
    }

    /**
     * ?????????????????????????????????
     *
     * @param contestPackage ????????????
     * @param problemIdList  ?????? ID ??????
     * @throws PortableException ????????????????????????
     */
    private void updateContestLock(@NotNull ContestPackage contestPackage, List<Long> problemIdList) throws PortableException {
        // ??????????????????
        setProblemContestId(problemIdList, null, contestPackage.contest.getId());
        // ??????????????????????????????????????????????????????????????????
        if (ContestAccessType.BATCH.equals(contestPackage.getContest().getAccessType())) {
            batchManager.updateBatchContest(
                    ((BatchContestData) contestPackage.getContestData()).getBatchId(),
                    contestPackage.getContest().getId()
            );
        }
    }

    private void setProblemContestId(@NotNull List<Long> problemIdList, Long fromContestId, Long toContestId) throws PortableException {
        long notExistProblemCount = problemIdList.stream()
                .filter(problemId -> {
                    try {
                        Problem problem = problemManager.getProblemById(problemId).orElseThrow(PortableException.from("A-04-001", problemId));
                        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
                        if (Objects.equals(problemData.getContestId(), fromContestId)) {
                            problemData.setContestId(toContestId);
                            problemDataManager.updateProblemData(problemData);
                        }
                        return false;
                    } catch (PortableException e) {
                        // ?????????????????????????????????????????????????????????
                        // ????????????????????????????????????
                        // ???????????????????????????????????????
                        // ?????????????????????????????????
                        return true;
                    }
                })
                .count();
        if (notExistProblemCount > 0) {
            throw PortableException.of("S-03-001");
        }
    }

    @NotNull
    private PageResponse<SolutionListResponse, Void> getSolutionList(ContestPackage contestPackage,
                                                                     @NotNull PageRequest<SolutionListQueryRequest> pageRequest,
                                                                     SolutionType solutionType) throws PortableException {
        SolutionListQueryRequest queryData = pageRequest.getQueryData();

        // ?????????????????????????????????????????????????????????????????????????????????
        Problem queryProblem = null;
        if (queryData.getProblemId() != null) {
            queryData.setProblemId(contestPackage.getContestData()
                    .atProblem(Math.toIntExact(queryData.getProblemId()), contestPackage.getContest().getId())
                    .getProblemId());
            queryProblem = problemManager.getProblemById(queryData.getProblemId()).orElse(null);
        }

        Long userId = null;
        // ????????????????????????????????????????????????????????????????????????????????????????????????
        User queryUser = null;
        if (Strings.isNotBlank(queryData.getUserHandle())) {
            // ???????????? userhandle ??????????????????????????????????????????????????????????????????????????????????????????
            queryUser = userManager.getAccountByHandle(queryData.getUserHandle()).orElseThrow(PortableException.from("A-01-001"));
            userId = queryUser.getId();
        }

        Integer solutionCount = solutionManager.countSolution(solutionType,
                userId,
                contestPackage.getContest().getId(),
                queryData.getProblemId(),
                queryData.getStatusType()
        );

        PageResponse<SolutionListResponse, Void> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectSolutionByPage(response.getPageSize(),
                response.offset(),
                solutionType,
                userId, contestPackage.getContest().getId(),
                queryData.getProblemId(), queryData.getStatusType(),
                queryData.getBeforeId(), queryData.getAfterId());

        Map<Long, Integer> problemIdToProblemIndexMap = contestPackage.getContestData().idToIndex();
        final Problem finalQueryProblem = queryProblem;
        final User finalQueryUser = queryUser;
        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
                .parallel()
                .map(solution -> {
                    User user = Objects.isNull(finalQueryUser) ? userManager.getAccountById(solution.getUserId()).orElse(null) : finalQueryUser;
                    Problem problem = Objects.isNull(finalQueryProblem) ? problemManager.getProblemById(solution.getProblemId()).orElse(null) : finalQueryProblem;
                    SolutionListResponse solutionListResponse = SolutionListResponse.of(solution, user, problem);
                    solutionListResponse.setProblemId(Long.valueOf(problemIdToProblemIndexMap.get(solution.getProblemId())));
                    return solutionListResponse;
                })
                .collect(Collectors.toList());
        response.setData(solutionListResponseList);
        return response;
    }
}
