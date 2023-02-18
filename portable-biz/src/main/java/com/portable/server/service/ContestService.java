package com.portable.server.service;

import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAddProblem;
import com.portable.server.model.request.contest.ContestAuth;
import com.portable.server.model.request.contest.ContestContentRequest;
import com.portable.server.model.request.contest.ContestRankPageRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestInfoResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.contest.ContestRankListResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;
import com.portable.server.type.ContestVisitType;

/**
 * @author shiroha
 */
public interface ContestService {

    /**
     * 查看比赛列表
     * @param pageRequest 比赛页码请求信息
     * @return 比赛列表
     */
    PageResponse<ContestListResponse, Void> getContestList(PageRequest<Void> pageRequest);

    /**
     * 通过密码认证比赛
     *
     * @param contestAuth 验证信息
     * @return 访问权限
     */
    ContestVisitType authorizeContest(ContestAuth contestAuth);

    /**
     * 获取比赛的简介
     * @param contestId 比赛的 id
     * @return 比赛的详情
     */
    ContestInfoResponse getContestInfo(Long contestId);

    /**
     * 获取比赛的详情
     * @param contestId 比赛的 id
     * @return 比赛的详情
     */
    ContestDetailResponse getContestData(Long contestId);

    /**
     * 获取比赛的管理员级别信息
     * @param contestId 比赛的 id
     * @return 比赛的详情
     */
    ContestAdminDetailResponse getContestAdminData(Long contestId);

    /**
     * 查看比赛中的题目信息
     * @param contestId 比赛 id
     * @param problemIndex 题目序号
     * @return 比赛的详情
     */
    ProblemDetailResponse getContestProblem(Long contestId, Integer problemIndex);

    /**
     * 获取比赛的所有提交信息
     * @param contestId 比赛的 id
     * @param pageRequest 提交的过滤条件
     * @return 比赛的提交列表
     */
    PageResponse<SolutionListResponse, Void> getContestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest);

    /**
     * 查看比赛中的提交信息
     * @param solutionId 提交信息
     * @return 提交详情
     */
    SolutionDetailResponse getContestSolution(Long solutionId);

    /**
     * 获取比赛的所有<span color="red">测试</span>提交信息
     * @param contestId 比赛的 id
     * @param pageRequest 提交的过滤条件
     * @return 比赛的提交列表
     */
    PageResponse<SolutionListResponse, Void> getContestTestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest);

    /**
     * 查看比赛中的<span color="red">测试</span>提交信息
     * @param solutionId 提交信息
     * @return 提交详情
     */
    SolutionDetailResponse getContestTestSolution(Long solutionId);

    /**
     * 获取比赛的榜单
     *
     * @param contestId   比赛 id
     * @param pageRequest 比赛榜单过滤条件
     * @return 比赛榜单
     */
    PageResponse<ContestRankListResponse, ContestRankListResponse> getContestRank(Long contestId, PageRequest<ContestRankPageRequest> pageRequest);

    /**
     * 提交代码
     * @param submitSolutionRequest 提交信息
     * @return 提交的 id
     */
    Long submit(SubmitSolutionRequest submitSolutionRequest);

    /**
     * 创建比赛
     * @param contestContentRequest 比赛的创建信息
     * @return 比赛创建后的 id
     */
    Long createContest(ContestContentRequest contestContentRequest);

    /**
     * 更新比赛的信息
     *
     * @param contestContentRequest 比赛的更新后信息
     */
    void updateContest(ContestContentRequest contestContentRequest);

    /**
     * 比赛合作出题人新增题目
     *
     * @param contestAddProblem 比赛增加的题目
     */
    void addContestProblem(ContestAddProblem contestAddProblem);
}
