package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.model.solution.Solution;
import com.portable.server.support.FileSupport;
import com.portable.server.service.SolutionService;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.util.StreamUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class SolutionServiceImpl implements SolutionService {

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private FileSupport fileSupport;

    @Override
    public PageResponse<SolutionListResponse> getPublicStatus(PageRequest<Void> pageRequest) {
        Integer solutionCount = solutionManager.countPublicSolution();
        PageResponse<SolutionListResponse> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectPublicSolutionByPage(pageRequest.getPageSize(), pageRequest.offset());
        List<SolutionListResponse> solutionListResponseList = solutionList.stream()
                .parallel()
                .map(SolutionListResponse::of)
                .collect(Collectors.toList());
        response.setData(solutionListResponseList);
        return response;
    }

    @Override
    public PageResponse<SolutionListResponse> getContestStatus(PageRequest<Long> pageRequest) {
        // TODO: 若比赛禁止显示其他人的提交情况，则仅显示自己的
        Integer solutionCount = solutionManager.countSolutionByContest(pageRequest.getQueryData());
        PageResponse<SolutionListResponse> response = PageResponse.of(pageRequest, solutionCount);
        List<Solution> solutionList = solutionManager.selectSolutionByContestAndPage(pageRequest.getPageSize(), pageRequest.offset(), pageRequest.getQueryData());
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
        // TODO: 检查是否在比赛中
        InputStream codeInputStream = fileSupport.getSolution(id, solution.getLanguageType());
        String code = StreamUtils.read(codeInputStream);
        return SolutionDetailResponse.of(solution, code);
    }

    @Override
    public SolutionDetailResponse submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException {
        Solution solution = solutionManager.newSolution();
        submitSolutionRequest.toSolution(solution);
        solutionManager.insertSolution(solution);

        try {
            InputStream codeStream = new ByteArrayInputStream(submitSolutionRequest.getCode().getBytes());
            fileSupport.saveSolution(solution.getId(), solution.getLanguageType(), codeStream);
        } catch (PortableException e) {
            solutionManager.updateStatus(solution.getId(), SolutionStatusType.SYSTEM_ERROR);
            throw e;
        }

        // TODO: 加入 Judge
        return SolutionDetailResponse.of(solution, submitSolutionRequest.getCode());
    }
}
