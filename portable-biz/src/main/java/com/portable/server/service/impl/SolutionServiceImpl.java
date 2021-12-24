package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ProblemDataManager;
import com.portable.server.manager.ProblemManager;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
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
    private ProblemManager problemManager;

    @Resource
    private ProblemDataManager problemDataManager;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private SolutionDataManager solutionDataManager;

    @Override
    public PageResponse<SolutionListResponse> getPublicStatus(PageRequest<Void> pageRequest) {
        Integer solutionCount = solutionManager.countPublicSolution();
        PageResponse<SolutionListResponse> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectPublicSolutionByPage(response.getPageSize(), response.offset());
        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
                .parallel()
                .map(SolutionListResponse::of)
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
        if (!Objects.equals(solution.getUserId(), UserContext.ctx().getId())
                && !UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_PUBLIC_SOLUTION)) {
            throw PortableException.of("A-05-003");
        }
        SolutionData solutionData = solutionDataManager.getSolutionData(solution.getDataId());
        if (solutionData == null) {
            throw PortableException.of("S-05-001");
        }
        return SolutionDetailResponse.of(solution, solutionData);
    }

    @Override
    public SolutionDetailResponse submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        Problem problem = problemManager.getProblemById(submitSolutionRequest.getProblemId());
        if (problem == null) {
            throw PortableException.of("A-04-001", submitSolutionRequest.getProblemId());
        }
        switch (problem.getAccessType()) {
            case PUBLIC:
                break;
            case HIDDEN:
                if (!UserContext.ctx().getPermissionTypeSet().contains(PermissionType.VIEW_HIDDEN_PROBLEM)) {
                    throw PortableException.of("A-05-004");
                }
                break;
            case PRIVATE:
                if (!Objects.equals(UserContext.ctx().getId(), problem.getOwner())) {
                    throw PortableException.of("A-05-004");
                }
                break;
            default:
                throw PortableException.of("S-03-003");
        }

        ProblemData problemData = problemDataManager.getProblemData(problem.getDataId());
        if (problemData == null) {
            throw PortableException.of("S-03-001");
        }

        Solution solution = solutionManager.newSolution();
        submitSolutionRequest.toSolution(solution);
        SolutionData solutionData = solutionDataManager.newSolutionData(problemData);
        submitSolutionRequest.toSolutionData(solutionData);
        solutionDataManager.insertSolutionData(solutionData);
        solution.setDataId(solutionData.get_id());
        solutionManager.insertSolution(solution);
        return SolutionDetailResponse.of(solution, solutionData);
    }
}
