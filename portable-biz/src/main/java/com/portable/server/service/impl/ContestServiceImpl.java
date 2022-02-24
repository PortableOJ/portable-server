package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestDataManager;
import com.portable.server.manager.ContestManager;
import com.portable.server.model.contest.BasicContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.ContestVisitPermission;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAddProblem;
import com.portable.server.model.request.contest.ContestContestRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.contest.ContestRankResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.service.ContestService;
import com.portable.server.type.PermissionType;
import com.portable.server.util.UserContext;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class ContestServiceImpl implements ContestService {

    @Resource
    private ContestManager contestManager;

    @Resource
    private ContestDataManager contestDataManager;

    @Data
    @Builder
    public static class ContestPackage {
        private Contest contest;
        private BasicContestData contestData;
    }

    @Override
    public PageResponse<ContestListResponse> getContestList(PageRequest<Void> pageRequest) {
        Integer contestCount = contestManager.getAllContestNumber();
        PageResponse<ContestListResponse> response = PageResponse.of(pageRequest, contestCount);
        List<Contest> contestList = contestManager.getContestByPage(response.getPageSize(), response.offset());
        List<ContestListResponse> contestListResponses = contestList.stream()
                .map(ContestListResponse::of)
                .collect(Collectors.toList());
        response.setData(contestListResponses);
        return response;
    }

    @Override
    public ContestDetailResponse getContestData(Long contestId) throws PortableException {
        ContestPackage contestPackage = getContestPackage(contestId);
        ContestVisitPermission contestVisitPermission = checkPermission(contestPackage);
        if (!ContestVisitPermission.VISIT.approve(contestVisitPermission)) {
            // TODO
        }
        return null;
    }

    @Override
    public ContestAdminDetailResponse getContestAdminData(Long contestId) {
        return null;
    }

    @Override
    public ProblemDetailResponse getContestProblem(Long contestId, Long problemIndex) {
        return null;
    }

    @Override
    public PageResponse<SolutionListResponse> getContestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) {
        return null;
    }

    @Override
    public SolutionDetailResponse getContestSolution(Long solutionId) {
        return null;
    }

    @Override
    public PageResponse<SolutionListResponse> getContestTestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) {
        return null;
    }

    @Override
    public SolutionDetailResponse getContestTestSolution(Long solutionId) {
        return null;
    }

    @Override
    public PageResponse<ContestRankResponse> getContestRank(Long contestId, PageRequest<Void> pageRequest) {
        return null;
    }

    @Override
    public Long submit(SubmitSolutionRequest submitSolutionRequest) {
        return null;
    }

    @Override
    public Long createContest(ContestContestRequest contestContestRequest) {
        return null;
    }

    @Override
    public void updateContest(ContestContestRequest contestContestRequest) {

    }

    @Override
    public void addContestProblem(ContestAddProblem contestAddProblem) {

    }

    private ContestPackage getContestPackage(Long contestId) throws PortableException {
        Contest contest = contestManager.getContestById(contestId);
        if (contest == null) {
            throw PortableException.of("A-08-002", contestId);
        }
        BasicContestData contestData;
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
            default:
                throw PortableException.of("A-08-001", contest.getAccessType());
        }
        return ContestPackage.builder()
                .contest(contest)
                .contestData(contestData)
                .build();
    }

    private ContestVisitPermission checkPermission(ContestPackage contestPackage) {
        Contest contest = contestPackage.getContest();
        BasicContestData contestData = contestPackage.getContestData();
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

        if (!contestVisitPermission.approve(ContestVisitPermission.VISIT)
                && userContext.getPermissionTypeSet().contains(PermissionType.VIEW_ALL_CONTEST)) {
            contestVisitPermission = ContestVisitPermission.VISIT;
        }
        userContext.addContestVisit(contest.getId(), contestVisitPermission);
        return contestVisitPermission;
    }
}
