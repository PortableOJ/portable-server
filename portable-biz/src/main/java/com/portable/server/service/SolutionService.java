package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;

public interface SolutionService {

    PageResponse<SolutionListResponse> getPublicStatus(PageRequest<Void> pageRequest);

    PageResponse<SolutionListResponse> getContestStatus(PageRequest<Long> pageRequest);

    SolutionDetailResponse getSolution(Long id) throws PortableException;

    SolutionDetailResponse submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException;
}
