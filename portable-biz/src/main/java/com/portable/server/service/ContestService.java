package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.type.ContestVisitPermission;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.contest.ContestAddProblem;
import com.portable.server.model.request.contest.ContestAuth;
import com.portable.server.model.request.contest.ContestContentRequest;
import com.portable.server.model.request.solution.SolutionListQueryRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.contest.ContestAdminDetailResponse;
import com.portable.server.model.response.contest.ContestDetailResponse;
import com.portable.server.model.response.contest.ContestListResponse;
import com.portable.server.model.response.contest.ContestRankListResponse;
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
     * @param contestAuth 验证信息
     * @throws PortableException 比赛 id 错误或者密码错误时抛出
     * @return 访问权限
     */
    ContestVisitPermission authorizeContest(ContestAuth contestAuth) throws PortableException;

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
     * @throws PortableException 没有权限或者没有此比赛时抛出
     */
    ContestAdminDetailResponse getContestAdminData(Long contestId) throws PortableException;

    /**
     * 查看比赛中的题目信息
     * @param contestId 比赛 id
     * @param problemIndex 题目序号
     * @return 比赛的详情
     * @throws PortableException 没有权限或没有此比赛时抛出
     */
    ProblemDetailResponse getContestProblem(Long contestId, Integer problemIndex) throws PortableException;

    /**
     * 获取比赛的所有提交信息
     * @param contestId 比赛的 id
     * @param pageRequest 提交的过滤条件
     * @return 比赛的提交列表
     * @throws PortableException 没有权限或没有此比赛时抛出
     */
    PageResponse<SolutionListResponse> getContestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) throws PortableException;

    /**
     * 查看比赛中的提交信息
     * @param solutionId 提交信息
     * @return 提交详情
     * @throws PortableException 没有权限或没有此比赛时抛出
     */
    SolutionDetailResponse getContestSolution(Long solutionId) throws PortableException;

    /**
     * 获取比赛的所有<span color="red">测试</span>提交信息
     * @param contestId 比赛的 id
     * @param pageRequest 提交的过滤条件
     * @return 比赛的提交列表
     * @throws PortableException 没有权限或没有此比赛时抛出
     */
    PageResponse<SolutionListResponse> getContestTestStatusList(Long contestId, PageRequest<SolutionListQueryRequest> pageRequest) throws PortableException;

    /**
     * 查看比赛中的<span color="red">测试</span>提交信息
     * @param solutionId 提交信息
     * @return 提交详情
     * @throws PortableException 没有权限或没有此比赛时抛出
     */
    SolutionDetailResponse getContestTestSolution(Long solutionId) throws PortableException;

    /**
     * 获取比赛的榜单
     * @param contestId 比赛 id
     * @param pageRequest 比赛榜单过滤条件
     * @return 比赛榜单
     */
    PageResponse<ContestRankListResponse> getContestRank(Long contestId, PageRequest<Void> pageRequest);

    /**
     * 提交代码
     * @param submitSolutionRequest 提交信息
     * @return 提交的 id
     * @throws PortableException 没有权限或没有此比赛时抛出
     */
    Long submit(SubmitSolutionRequest submitSolutionRequest) throws PortableException;

    /**
     * 创建比赛
     * @param contestContentRequest 比赛的创建信息
     * @return 比赛创建后的 id
     * @throws PortableException 没有权限或没有此比赛时抛出
     */
    Long createContest(ContestContentRequest contestContentRequest) throws PortableException;

    /**
     * 更新比赛的信息
     *
     * @param contestContentRequest 比赛的更新后信息
     * @throws PortableException 无权或者数据非法则抛出
     */
    void updateContest(ContestContentRequest contestContentRequest) throws PortableException;

    /**
     * 比赛合作出题人新增题目
     *
     * @param contestAddProblem 比赛增加的题目
     * @throws PortableException 无权或者数据非法则抛出
     */
    void addContestProblem(ContestAddProblem contestAddProblem) throws PortableException;
}
