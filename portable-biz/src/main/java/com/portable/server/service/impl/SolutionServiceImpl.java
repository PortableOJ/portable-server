package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.service.SolutionService;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class SolutionServiceImpl implements SolutionService {
    @Override
    public PageResponse<SolutionListResponse> getPublicStatus(PageRequest<Void> pageRequest) {
        return null;
    }

    @Override
    public PageResponse<SolutionListResponse> getContestStatus(PageRequest<Long> pageRequest) {
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
