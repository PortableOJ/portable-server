package com.portable.server.model.request.problem;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.JudgeCodeType;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class ProblemJudgeRequest {

    /**
     * 题目 ID
     */
    private Long id;

    /**
     * judge 模式
     */
    private JudgeCodeType judgeCodeType;

    /**
     * DIY judge code
     */
    private String judgeCode;

    /**
     * 将数据保存至 problemData
     * @param problemData 被保存数据的 problem Data
     */
    public void toProblemData(ProblemData problemData) {
        problemData.setJudgeCodeType(judgeCodeType);
        problemData.setJudgeCode(judgeCode);
    }

}
