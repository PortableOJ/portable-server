package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;

/**
 * @author shiroha
 */
public interface SolutionService {

    /**
     * 获取公共提交列表
     * @param pageRequest 分页
     * @return 公共提交列表
     */
    PageResponse<SolutionListResponse> getPublicStatus(PageRequest<Void> pageRequest);

    /**
     * 获取公共提交中的某次详细信息
     * @param id 提交的 ID
     * @return 提交的内容
     * @throws PortableException 出现非法访问或不存在此提交则抛出错误
     */
    SolutionDetailResponse getSolution(Long id) throws PortableException;
}
