package com.portable.server.service;

import com.portable.server.model.request.PageRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestListResponse;

/**
 * @author shiroha
 */
public interface ContestService {

    /**
     * 查看比赛列表
     * @param pageRequest 比赛页码请求信息
     * @return 比赛列表
     */
    PageResponse<ContestListResponse> getContestList(PageRequest<Void> pageRequest);


}
