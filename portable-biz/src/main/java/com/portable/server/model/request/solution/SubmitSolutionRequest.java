package com.portable.server.model.request.solution;

import com.portable.server.model.solution.Solution;
import com.portable.server.type.LanguageType;
import lombok.Data;

@Data
public class SubmitSolutionRequest {

    /**
     * 问题的 id
     */
    private Long problemId;

    /**
     * 提交至比赛的 id
     */
    private Long contestId;

    /**
     * 语言
     */
    private LanguageType languageType;

    /**
     * 代码内容
     */
    private String code;

    public void toSolution(Solution solution) {
        solution.setProblemId(problemId);
        solution.setContestId(contestId);
        solution.setLanguageType(languageType);
    }
}
