package com.portable.server.model.request.solution;

import com.portable.server.model.solution.Solution;
import com.portable.server.model.solution.SolutionData;
import com.portable.server.type.LanguageType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author shiroha
 */
@Data
public class SubmitSolutionRequest {

    /**
     * 问题的 id/序号
     */
    @NotNull(message = "A-04-001")
    private Long problemId;

    /**
     * 提交至比赛的 id
     */
    private Long contestId;

    /**
     * 语言
     */
    @NotNull(message = "A-04-016")
    private LanguageType languageType;

    /**
     * 代码内容
     */
    @NotNull(message = "A-04-017")
    @Size(min = 10, max = 65535, message = "A-04-017")
    private String code;

    public void toSolution(Solution solution) {
        solution.setProblemId(problemId);
        solution.setContestId(contestId);
        solution.setLanguageType(languageType);
    }

    public void toSolutionData(SolutionData solutionData) {
        solutionData.setCode(code);
    }
}
