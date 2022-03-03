package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.User;
import com.portable.server.service.SolutionService;
import com.portable.server.type.PermissionType;
import com.portable.server.type.SolutionType;
import com.portable.server.util.UserContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class SolutionServiceImpl implements SolutionService {

    @Resource
    private UserManager userManager;

    @Resource
    private ProblemManager problemManager;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private SolutionDataManager solutionDataManager;

    @Override
    public PageResponse<SolutionListResponse, Void> getPublicStatus(PageRequest<SolutionListQueryRequest> pageRequest) {
        Integer solutionCount = solutionManager.countPublicSolution(pageRequest.getQueryData().getUserId(), pageRequest.getQueryData().getProblemId(), pageRequest.getQueryData().getStatusType());
        PageResponse<SolutionListResponse, Void> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectPublicSolutionByPage(
                response.getPageSize(),
                response.offset(),
                pageRequest.getQueryData().getUserId(),
                pageRequest.getQueryData().getProblemId(),
                pageRequest.getQueryData().getStatusType()
        );

        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
                .parallel()
                .map(solution -> {
                    User user = userManager.getAccountById(solution.getUserId());
                    Problem problem = problemManager.getProblemById(solution.getProblemId());
                    return SolutionListResponse.of(solution, user, problem);
                })
                .collect(Collectors.toList());
        response.setData(solutionListResponseList);
        return response;
    }

    @Override
    public SolutionDetailResponse getSolution(Long id) throws PortableException {
        Solution solution = solutionManager.selectSolutionById(id);
        if (solution == null) {
            throw PortableException.of("A-05-001", id);
        }
        if (!SolutionType.PUBLIC.equals(solution.getSolutionType())) {
            throw PortableException.of("A-05-002");
        }
        UserContext userContext = UserContext.ctx();
        boolean isHost = Objects.equals(solution.getUserId(), userContext.getId());
        boolean hasPermission = UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_PUBLIC_SOLUTION);
        if (!isHost && !hasPermission) {
            throw PortableException.of("A-05-003");
        }
        SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
        if (solutionData == null) {
            throw PortableException.of("S-05-001");
        }
        User user = userManager.getAccountById(solution.getUserId());
        Problem problem = problemManager.getProblemById(solution.getProblemId());
        boolean shareJudgeMsg = Objects.equals(problem.getOwner(), userContext.getId())
                || userContext.getPermissionTypeSet().contains(PermissionType.VIEW_SOLUTION_MESSAGE);
        return SolutionDetailResponse.of(solution, solutionData, user, problem, shareJudgeMsg);
    }
}
