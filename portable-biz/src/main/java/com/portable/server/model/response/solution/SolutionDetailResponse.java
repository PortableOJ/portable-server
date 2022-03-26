package com.portable.server.model.response.solution;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.model.user.User;
import com.portable.server.type.LanguageType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;

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

    /**
     * 每个测试样例的执行结果信息
     */
    private Map<String, SolutionData.JudgeReportMsg> judgeReportMsgMap;

    private SolutionDetailResponse(@NotNull Solution solution,
                                   @NotNull SolutionData solutionData,
                                   @Nullable User user,
                                   @Nullable Problem problem,
                                   @NotNull Boolean shareJudgeMsg) {
        this.id = solution.getId();
        this.submitTime = solution.getSubmitTime();
        this.userId = solution.getUserId();
        this.userHandle = user == null ? "" : user.getHandle();
        this.problemId = solution.getProblemId();
        this.problemTitle = problem == null ? "" : problem.getTitle();
        this.contestId = solution.getContestId();
        this.languageType = solution.getLanguageType();
        this.status = solution.getStatus();
        this.solutionType = solution.getSolutionType();
        this.timeCost = solution.getTimeCost();
        this.memoryCost = solution.getMemoryCost();
        this.code = solutionData.getCode();
        this.compileMsg = solutionData.getCompileMsg();
        this.judgeReportMsgMap = shareJudgeMsg ? solutionData.getRunningMsg() : null;
    }

    public static SolutionDetailResponse of(@NotNull Solution solution,
                                            @NotNull SolutionData solutionData,
                                            @Nullable User user,
                                            @Nullable Problem problem,
                                            @NotNull Boolean shareJudgeMsg) {
        return new SolutionDetailResponse(solution, solutionData, user, problem, shareJudgeMsg);
    }
}
