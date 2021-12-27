package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.SolutionDataManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.request.PageRequest;
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
}
