package com.portable.server.service;

import java.io.OutputStream;
import java.util.List;

import com.portable.server.exception.PortableException;
import com.portable.server.model.problem.Problem;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.problem.ProblemCodeRequest;
import com.portable.server.model.request.problem.ProblemContentRequest;
import com.portable.server.model.request.problem.ProblemJudgeRequest;
import com.portable.server.model.request.problem.ProblemNameRequest;
import com.portable.server.model.request.problem.ProblemSettingRequest;
import com.portable.server.model.request.problem.ProblemTestRequest;
import com.portable.server.model.request.solution.SubmitSolutionRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.problem.ProblemDetailResponse;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.response.problem.ProblemStdTestCodeResponse;

/**
 * @author shiroha
 */
public interface ProblemService {

    /**
     * 查看题目列表
     *
     * @param pageRequest 分页请求
     * @return 分页后的题目列表
     */
    PageResponse<ProblemListResponse, Void> getProblemList(PageRequest<Void> pageRequest);

    /**
     * 获取公开的题库中匹配关键字的题目，可能搜索私有的
     * @param keyword 关键字
     * @return 题目列表
     */
    List<ProblemListResponse> searchProblemSetList(String keyword);

    /**
     * 获取私人题库中匹配关键字的题目，仅限私人
     * @param keyword 关键字
     * @return 题目列表
     */
    List<ProblemListResponse> searchPrivateProblemList(String keyword);

    /**
     * 查看题目详情
     *
     * @param id 题目 ID
     * @return 题目详情内容
     * @throws PortableException 遇到意外情况抛出错误
     */
    ProblemDetailResponse getProblem(Long id);

    /**
     * 查看题目输入输出文件列表
     *
     * @param id 题目 ID
     * @return 题目测试数据的
     * @throws PortableException 遇到意外情况抛出错误
     */
    List<String> getProblemTestList(Long id);


    /**
     * 获取题目输入文件的预览
     *
     * @param problemNameRequest 题目的 ID 以及测试的名称
     * @return 题目输入文件的预览
     * @throws PortableException 遇到意外情况抛出错误
     */
    String showTestInput(ProblemNameRequest problemNameRequest);

    /**
     * 获取题目输出文件的预览
     *
     * @param problemNameRequest 题目的 ID 以及测试的名称
     * @return 题目输出文件的预览
     * @throws PortableException 遇到意外情况抛出错误
     */
    String showTestOutput(ProblemNameRequest problemNameRequest);

    /**
     * 下载题目的输入文件
     *
     * @param problemNameRequest 题目的 ID 以及测试的名称
     * @param outputStream       需要写入的文件流
     * @throws PortableException 遇到意外情况抛出错误
     */
    void downloadTestInput(ProblemNameRequest problemNameRequest, OutputStream outputStream);

    /**
     * 下载题目的输出文件
     *
     * @param problemNameRequest 题目的 ID 以及测试的名称
     * @param outputStream       需要写入的文件流
     * @throws PortableException 遇到意外情况抛出错误
     */
    void downloadTestOutput(ProblemNameRequest problemNameRequest, OutputStream outputStream);

    /**
     * 创建题目内容（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemContentRequest 题目内容
     * @return 创建后的题目内容的简单参数，主要是 ID
     * @throws PortableException 遇到意外情况抛出错误
     */
    Problem newProblem(ProblemContentRequest problemContentRequest);

    /**
     * 更新题目内容（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemContentRequest 题目内容
     * @throws PortableException 遇到意外情况抛出错误
     */
    void updateProblemContent(ProblemContentRequest problemContentRequest);

    /**
     * 修改题目配置（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemSettingRequest 题目的设置
     * @throws PortableException 遇到意外情况抛出错误
     */
    void updateProblemSetting(ProblemSettingRequest problemSettingRequest);

    /**
     * 修改题目的 judge 配置（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemJudgeRequest 题目的 judge 配置
     * @throws PortableException 遇到意外情况抛出错误
     */
    void updateProblemJudge(ProblemJudgeRequest problemJudgeRequest);

    /**
     * 上传题目的 Test 输入数据（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemTestRequest 需要添加新的测试数据
     * @throws PortableException 遇到意外情况抛出错误
     */
    void addProblemTest(ProblemTestRequest problemTestRequest);

    /**
     * 删除题目的 Test 数据（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemNameRequest 需要删除的测试数据名
     * @throws PortableException 遇到意外情况抛出错误
     */
    void removeProblemTest(ProblemNameRequest problemNameRequest);

    /**
     * 查看标准/测试代码列表（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param id 题目的 ID
     * @return 标准代码和测试代码以及其期望结果和实际结果
     * @throws PortableException 遇到意外情况抛出错误
     */
    ProblemStdTestCodeResponse getProblemStdTestCode(Long id);

    /**
     * 变更标准代码（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemCodeRequest 标准代码的内容
     * @throws PortableException 遇到意外情况抛出错误
     */
    void updateProblemStdCode(ProblemCodeRequest problemCodeRequest);

    /**
     * 上传测试代码（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemStdCodeRequest 测试代码的内容
     * @throws PortableException 遇到意外情况抛出错误
     */
    void addProblemTestCode(ProblemCodeRequest problemStdCodeRequest);

    /**
     * 删除测试代码（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemNameRequest 需要删除的测试代码名
     * @throws PortableException 遇到意外情况抛出错误
     */
    void removeProblemTestCode(ProblemNameRequest problemNameRequest);

    /**
     * 预览标准代码（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param id 问题 ID
     * @return 代码内容
     * @throws PortableException 遇到意外情况抛出错误
     */
    String showStdCode(Long id);

    /**
     * 预览测试代码（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemNameRequest 需要预览的测试数据
     * @return 代码内容
     * @throws PortableException 遇到意外情况抛出错误
     */
    String showTestCode(ProblemNameRequest problemNameRequest);

    /**
     * 下载标准代码（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param id 题目的 ID
     * @param outputStream 输出流
     * @throws PortableException 遇到意外情况抛出错误
     */
    void downloadStdCode(Long id, OutputStream outputStream);

    /**
     * 下载测试代码（需要权限 {@link com.portable.server.type.PermissionType#CREATE_AND_EDIT_PROBLEM}）
     *
     * @param problemNameRequest 需要预览的测试代码信息
     * @param outputStream 输出流
     * @throws PortableException 遇到意外情况抛出错误
     */
    void downloadTestCode(ProblemNameRequest problemNameRequest, OutputStream outputStream);

    /**
     * 执行处理校验
     * @param id 题目的 ID
     * @throws PortableException 遇到意外情况抛出错误
     */
    void treatAndCheckProblem(Long id);

    /**
     * 提交代码
     * @param submitSolutionRequest 提交的代码信息
     * @return 提交的内容
     * @throws PortableException 出现非法提交或不存在对应题目则抛出错误
     */
    Long submit(SubmitSolutionRequest submitSolutionRequest);
}
