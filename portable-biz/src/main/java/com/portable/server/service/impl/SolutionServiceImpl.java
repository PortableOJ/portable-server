package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.service.SolutionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class SolutionServiceImpl implements SolutionService {

    @Resource
    private SolutionManager solutionManager;

    @Override
    public PageResponse<SolutionListResponse> getPublicStatus(PageRequest<Void> pageRequest) {
//        Integer solutionCount = solutionManager.countPublicSolution();
//        PageResponse<SolutionListResponse> response = PageResponse.of(pageRequest, solutionCount);
//        List<Solution> solutionList = solutionManager.selectPublicSolutionByPage(response.getPageSize(), response.offset());
//        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
//                .parallel()
//                .map(solution -> {
//
//                })
//                .collect(Collectors.toList());
        return null;
    }

    @Override
    public SolutionDetailResponse getSolution(Long id) throws PortableException {
        return null;
    }

    @Override
    public SolutionDetailResponse submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        return null;
    }
}
