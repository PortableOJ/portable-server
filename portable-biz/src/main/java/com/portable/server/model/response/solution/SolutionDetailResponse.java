package com.portable.server.model.response.solution;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.User;
import com.portable.server.type.LanguageType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import lombok.Data;

import java.util.Date;

/**
 * @author shiroha
 */
@Data
public class SolutionDetailResponse {

    /**
     * 提交的 id
     */
    private Long id;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 提交者 id
     */
    private Long userId;

    /**
     * 提交者 handle
     */
    private String userHandle;

    /**
     * 问题的 id
     */
    private Long problemId;

    /**
     * 问题的标题
     */
    private String problemTitle;

    /**
     * 提交至比赛的 id
     */
    private Long contestId;

    /**
     * 语言
     */
    private LanguageType languageType;

    /**
     * 当前状态
     */
    private SolutionStatusType status;

    /**
     * solution 的类型
     */
    private SolutionType solutionType;

    /**
     * 耗时（ms）
     */
    private Integer timeCost;

    /**
     * 内存消耗（mb）
     */
    private Integer memoryCost;

    /**
     * 代码内容
     */
    private String code;

    /**
     * 编译信息
     */
    private String compileMsg;

    private SolutionDetailResponse(Solution solution, SolutionData solutionData, User user, Problem problem) {
        this.id = solution.getId();
        this.submitTime = solution.getSubmitTime();
        this.userId = solution.getUserId();
        this.userHandle = user.getHandle();
        this.problemId = solution.getProblemId();
        this.problemTitle = problem.getTitle();
        this.contestId = solution.getContestId();
        this.languageType = solution.getLanguageType();
        this.status = solution.getStatus();
        this.solutionType = solution.getSolutionType();
        this.timeCost = solution.getTimeCost();
        this.memoryCost = solution.getMemoryCost();
        this.code = solutionData.getCode();
        this.compileMsg = solutionData.getCompileMsg();
    }

    public static SolutionDetailResponse of(Solution solution, SolutionData solutionData, User user, Problem problem) {
        return new SolutionDetailResponse(solution, solutionData, user, problem);
    }
}
