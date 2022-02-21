package com.portable.server.model.response.problem;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import com.portable.server.model.user.User;
import com.portable.server.type.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shiroha
 */
@Data
public class ProblemDetailResponse {

    /**
     * 问题 ID
     */
    private Long id;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 题目的状态
     */
    private ProblemStatusType statusType;

    /**
     * 题目的访问权限
     */
    private ProblemAccessType accessType;

    /**
     * 历史提交数量
     */
    private Integer submissionCount;

    /**
     * 历史通过的数量
     */
    private Integer acceptCount;

    /**
     * 作者 handle
     */
    private String ownerHandle;

    /**
     * 默认的耗时限制，单位（s）
     */
    private Integer defaultTimeLimit;

    /**
     * 默认的内存限制，单位（mb）
     */
    private Integer defaultMemoryLimit;

    /**
     * 部分语言的特殊时间限制
     */
    private Map<LanguageType, Integer> specialTimeLimit;

    /**
     * 部分语言的特殊内存限制
     */
    private Map<LanguageType, Integer> specialMemoryLimit;

    /**
     * 允许使用的语言类型
     */
    private List<LanguageType> supportLanguage;

    /**
     * 题目描述
     */
    private String description;

    /**
     * 输入描述
     */
    private String input;

    /**
     * 输出描述
     */
    private String output;

    /**
     * 输入输出样例
     */
    private List<ProblemData.Example> example;

    /**
     * 题目类型
     */
    private ProblemType type;

    /**
     * judge 模式
     */
    private JudgeCodeType judgeCodeType;

    /**
     * DIY judge code
     */
    private String judgeCode;

    /**
     * 是否允许下载样例
     */
    private Boolean shareTest;

    /**
     * 首次关联至的比赛 ID
     */
    private Long contestId;

    /**
     * 题目版本号
     */
    private Integer version;

    /**
     * 题目最后更新时间
     */
    private Date gmtModifyTime;

    public ProblemDetailResponse(Problem problem, ProblemData problemData, User user) {
        this.id = problem.getId();
        this.title = problem.getTitle();
        this.statusType = problem.getStatusType();
        this.accessType = problem.getAccessType();
        this.submissionCount = problem.getSubmissionCount();
        this.acceptCount = problem.getAcceptCount();
        this.ownerHandle = user == null ? "" : user.getHandle();

        this.defaultTimeLimit = problemData.getDefaultTimeLimit();
        this.defaultMemoryLimit = problemData.getDefaultMemoryLimit();
        this.specialTimeLimit = problemData.getSpecialTimeLimit();
        this.specialMemoryLimit = problemData.getSpecialMemoryLimit();
        this.supportLanguage = problemData.getSupportLanguage();
        this.description = problemData.getDescription();
        this.input = problemData.getInput();
        this.output = problemData.getOutput();
        this.example = problemData.getExample();
        this.type = problemData.getType();
        this.judgeCodeType = problemData.getJudgeCodeType();
        this.judgeCode = problemData.getJudgeCode();
        this.shareTest = problemData.getShareTest();
        this.contestId = problemData.getContestId();
        this.version = problemData.getVersion();
        this.gmtModifyTime = problemData.getGmtModifyTime();
    }

    public static ProblemDetailResponse of(Problem problem, ProblemData problemData, User user) {
        return new ProblemDetailResponse(problem, problemData, user);
    }
}
