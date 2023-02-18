package com.portable.server.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import com.portable.server.type.ProblemType;
import com.portable.server.type.SolutionStatusType;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public interface ProblemManager {

    /**
     * 创建一个新的题目
     *
     * @return 新题目
     */
    @NotNull
    default Problem newProblem() {
        return Problem.builder()
                .id(null)
                .dataId(null)
                .title(null)
                .statusType(ProblemStatusType.UNTREATED)
                .accessType(ProblemAccessType.PRIVATE)
                .submissionCount(0)
                .acceptCount(0)
                .owner(null)
                .build();
    }

    /**
     * 新建一个问题数据
     *
     * @return 新问题数据
     */
    @NotNull
    default ProblemData newProblemData() {
        return ProblemData.builder()
                .id(null)
                .contestId(null)
                .defaultTimeLimit(1)
                .defaultMemoryLimit(128)
                .specialTimeLimit(new HashMap<>(0))
                .specialMemoryLimit(new HashMap<>(0))
                .supportLanguage(new ArrayList<>())
                .description(null)
                .input(null)
                .output(null)
                .example(new ArrayList<>())
                .type(ProblemType.STANDARD)
                .judgeCodeType(JudgeCodeType.ALL_SAME)
                .judgeCode(null)
                .testName(new ArrayList<>())
                .shareTest(false)
                .stdCode(ProblemData.StdCode.builder()
                        .name("STD")
                        .code(null)
                        .expectResultType(SolutionStatusType.ACCEPT)
                        .languageType(LanguageType.CPP17)
                        .solutionId(null)
                        .build())
                .testCodeList(new ArrayList<>())
                .version(0)
                .gmtModifyTime(new Date())
                .build();
    }

    /**
     * 根据题目的访问类型和当前用户的 ID 获取题目总数
     *
     * @param accessTypeList 访问权限
     * @param ownerId        所拥有的用户 ID
     * @return 总匹配的题目数量
     */
    @NotNull
    Integer countProblemByTypeAndOwnerId(List<ProblemAccessType> accessTypeList, Long ownerId);

    /**
     * 分页获取匹配的题目访问类型和当前用户的 ID 的题目
     *
     * @param accessTypeList 访问类型
     * @param ownerId        所拥有的用户 ID
     * @param pageSize       单页数量
     * @param offset         偏移量
     * @return 题目的列表
     */
    @NotNull
    List<Problem> getProblemListByTypeAndOwnerIdAndPaged(List<ProblemAccessType> accessTypeList, Long ownerId, Integer pageSize, Integer offset);

    /**
     * 获取匹配标题的一定数量的最新题目
     *
     * @param accessTypeList 匹配的访问权限列表
     * @param keyword        关键字
     * @param num            总需要数量
     * @return 问题列表
     */
    @NotNull
    List<Problem> searchRecentProblemByTypedAndKeyword(List<ProblemAccessType> accessTypeList, String keyword, Integer num);

    /**
     * 获取匹配标题的一定数量的最新私人题目
     *
     * @param ownerId 用户 id
     * @param keyword 关键字
     * @param num     总需要数量
     * @return 问题列表
     */
    @NotNull
    List<Problem> searchRecentProblemByOwnerIdAndKeyword(Long ownerId, String keyword, Integer num);

    /**
     * 获取对应 ID 的题目内容
     *
     * @param id 题目的 ID
     * @return 题目内容
     */
    @NotNull
    Optional<Problem> getProblemById(Long id);

    /**
     * 校验题目列表是否存在
     *
     * @param problemList 题目列表
     * @return 不存在的题目列表
     */
    @NotNull
    List<Long> checkProblemListExist(List<Long> problemList);

    /**
     * 插入题目，强调是新增
     *
     * @param problem 需要新增的题目，其中的 id 字段会被新的值覆盖
     */
    void insertProblem(Problem problem);

    /**
     * 更新题目的标题
     *
     * @param id       题目的 ID
     * @param newTitle 题目的新标题
     */
    void updateProblemTitle(Long id, String newTitle);

    /**
     * 更新题目的访问状态
     *
     * @param id        题目的 ID
     * @param newStatus 新的访问状态
     */
    void updateProblemAccessStatus(Long id, ProblemAccessType newStatus);

    /**
     * 更新题目的状态
     *
     * @param id         题目的 ID
     * @param statusType 新的状态
     */
    void updateProblemStatus(Long id, ProblemStatusType statusType);

    /**
     * 更新题目的提交情况
     *
     * @param id          题目 ID
     * @param submitCount 新的提交量
     * @param acceptCount 新的通过量
     */
    void updateProblemCount(Long id, Integer submitCount, Integer acceptCount);

    /**
     * 更新题目所有者
     *
     * @param id       题目 ID
     * @param newOwner 新的所有者
     */
    void updateProblemOwner(Long id, Long newOwner);

    /**
     * 更新所有的状态
     *
     * @param fromStatus 需要更新的状态
     * @param toStatus   更新至的状态
     */
    void updateAllStatus(ProblemStatusType fromStatus, ProblemStatusType toStatus);

    /**
     * 根据 dataID 获取题目数据
     *
     * @param dataId 题目数据 ID
     * @return 题目数据
     */
    @NotNull
    ProblemData getProblemData(String dataId);

    /**
     * 新增题目数据
     *
     * @param problemData 题目数据信息
     */
    void insertProblemData(ProblemData problemData);

    /**
     * 更新题目数据
     *
     * @param problemData 题目数据
     */
    void updateProblemData(ProblemData problemData);

    /// region problem 的文件操作

    /**
     * 创建问题的目录
     *
     * @param problemId 问题 ID
     */
    void createProblem(@NotNull Long problemId);

    /**
     * 获取测试输入的文件流
     *
     * @param problemId 题目 ID
     * @param testName  测试数据名称
     * @return 测试数据输入流
     */
    InputStream getTestInput(Long problemId, String testName);

    /**
     * 获取测试输出的文件流
     *
     * @param problemId 题目 ID
     * @param testName  测试数据名称
     * @return 测试数据输出流
     */
    InputStream getTestOutput(Long problemId, String testName);

    /**
     * 保存测试输入文件流
     *
     * @param problemId   题目 ID
     * @param testName    测试数据名称
     * @param inputStream 输入流
     */
    void saveTestInput(Long problemId, String testName, InputStream inputStream);

    /**
     * 保存测试输出文件流
     *
     * @param problemId   题目 ID
     * @param testName    测试数据名称
     * @param inputStream 输入流
     */
    void saveTestOutput(Long problemId, String testName, InputStream inputStream);

    /**
     * 创建一个新的测试输出文件
     *
     * @param problemId 题目 ID
     * @param testName  测试数据名称
     * @param value     开头的字符串
     */
    void createTestOutput(Long problemId, String testName, byte[] value);

    /**
     * 创建一个新的测试输出文件
     *
     * @param problemId 题目 ID
     * @param testName  测试数据名称
     * @param value     新增加的字符串
     */
    void appendTestOutput(Long problemId, String testName, byte[] value);

    /**
     * 删除测试文件，包括输入输出
     *
     * @param problemId 题目 ID
     * @param testName  测试数据名称
     */
    void removeTest(Long problemId, String testName);

    /// endregion
}
