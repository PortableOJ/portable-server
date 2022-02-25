package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.ContestVisitPermission;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAddProblem;
import com.portable.server.model.request.contest.ContestContestRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.contest.ContestRankResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.solution.SolutionDetailResponse;
import com.portable.server.model.response.solution.SolutionListResponse;

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

    /**
     * 通过密码认证比赛
     * @param contestId 比赛的 id
     * @param password 密码
     * @throws PortableException 比赛 id 错误或者密码错误时抛出
     * @return 访问权限
     */
    ContestVisitPermission authorizeContest(Long contestId, String password) throws PortableException;

    /**
     * 获取比赛的详情
     * @param contestId 比赛的 id
     * @throws PortableException 比赛不存在或者无权访问则抛出错误
     * @return 比赛的详情
     */
    ContestDetailResponse getContestData(Long contestId) throws PortableException;

    /**
     * 获取比赛的管理员级别信息
     * @param contestId 比赛的 id
     * @return 比赛的详情
     */
    ContestAdminDetailResponse getContestAdminData(Long contestId) throws PortableException;

    /**
     * 查看比赛中的题目信息
     * @param contestId 比赛 id
     * @param problemIndex 题目序号
     * @return 比赛的详情
     */
    ProblemDetailResponse getContestProblem(Long contestId, Integer problemIndex) throws PortableException;

    /**
     * 获取比赛的所有提交信息
     * @param contestId 比赛的 id
     * @param pageRequest 提交的过滤条件
     * @return 比赛的提交列表
     */
    PageResponse<SolutionListResponse> getContestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) throws PortableException;

    /**
     * 查看比赛中的提交信息
     * @param solutionId 提交信息
     * @return 提交详情
     */
    SolutionDetailResponse getContestSolution(Long solutionId) throws PortableException;

    /**
     * 获取比赛的所有<span color="red">测试</span>提交信息
     * @param contestId 比赛的 id
     * @param pageRequest 提交的过滤条件
     * @return 比赛的提交列表
     */
    PageResponse<SolutionListResponse> getContestTestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) throws PortableException;

    /**
     * 查看比赛中的<span color="red">测试</span>提交信息
     * @param solutionId 提交信息
     * @return 提交详情
     */
    SolutionDetailResponse getContestTestSolution(Long solutionId) throws PortableException;

    /**
     * 获取比赛的榜单
     * @param contestId 比赛 id
     * @param pageRequest 比赛榜单过滤条件
     * @return 比赛榜单
     */
    PageResponse<ContestRankResponse> getContestRank(Long contestId, PageRequest<Void> pageRequest);

    /**
     * 提交代码
     * @param submitSolutionRequest 提交信息
     * @return 提交的 id
     */
    Long submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException;

    /**
     * 创建比赛
     * @param contestContestRequest 比赛的创建信息
     * @return 比赛创建后的 id
     */
    Long createContest(ContestContestRequest contestContestRequest);

    /**
     * 更新比赛的信息
     *
     * @param contestContestRequest 比赛的更新后信息
     */
    void updateContest(ContestContestRequest contestContestRequest);

    /**
     * 比赛合作出题人新增题目
     *
     * @param contestAddProblem 比赛增加的题目
     */
    void addContestProblem(ContestAddProblem contestAddProblem);
}
