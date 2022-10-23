package com.portable.server.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ProblemManager;
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

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

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

    @Override
    public PageResponse<SolutionListResponse, Void> getPublicStatus(PageRequest<SolutionListQueryRequest> pageRequest) {
        SolutionListQueryRequest queryRequest = pageRequest.getQueryData();
        // 当提供了 userhandle 的时候，若用户不存在，则抛出错误，若为空值，则当作不设置条件
        Long userId = null;
        if (Strings.isNotBlank(queryRequest.getUserHandle())) {
            userId = userManager.changeHandleToUserId(queryRequest.getUserHandle()).orElseThrow(PortableException.from("A-01-001"));
        }
        Integer solutionCount = solutionManager.countSolution(
                SolutionType.PUBLIC, userId, null,
                queryRequest.getProblemId(), queryRequest.getStatusType()
        );
        PageResponse<SolutionListResponse, Void> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectSolutionByPage(
                response.getPageSize(), response.offset(),
                SolutionType.PUBLIC, userId, null,
                queryRequest.getProblemId(), queryRequest.getStatusType(),
                queryRequest.getBeforeId(), queryRequest.getAfterId());

        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
                .parallel()
                .map(solution -> {
                    User user = userManager.getAccountById(solution.getUserId()).orElse(null);
                    Problem problem = problemManager.getProblemById(solution.getProblemId()).orElse(null);
                    return SolutionListResponse.of(solution, user, problem);
                })
                .collect(Collectors.toList());
        response.setData(solutionListResponseList);
        return response;
    }

    @Override
    public SolutionDetailResponse getSolution(Long id) {
        Solution solution = solutionManager.selectSolutionById(id)
                .orElseThrow(PortableException.from("A-05-001", id));
        if (!SolutionType.PUBLIC.equals(solution.getSolutionType())) {
            throw PortableException.of("A-05-002");
        }
        UserContext userContext = UserContext.ctx();
        boolean isOwner = Objects.equals(solution.getUserId(), userContext.getId());
        boolean hasPermission = UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_PUBLIC_SOLUTION);
        if (!isOwner && !hasPermission) {
            throw PortableException.of("A-05-003");
        }
        SolutionData solutionData = solutionManager.getSolutionData(solution.getDataId());
        User user = userManager.getAccountById(solution.getUserId()).orElse(null);
        Problem problem = problemManager.getProblemById(solution.getProblemId()).orElse(null);
        Boolean ownerProblem = problem != null && Objects.equals(problem.getOwner(), userContext.getId());
        Boolean permissionMsg = userContext.getPermissionTypeSet().contains(PermissionType.VIEW_SOLUTION_MESSAGE);
        return SolutionDetailResponse.of(solution, solutionData, user, problem, ownerProblem || permissionMsg);
    }
}
