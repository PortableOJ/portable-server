package com.portable.server.model.request.problem;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.JudgeCodeType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Data
@Builder
public class ProblemJudgeRequest {

    /**
     * 题目 ID
     */
    @NotNull(message = "A-04-001")
    @Min(value = 1, message = "A-04-001")
    private Long id;

    /**
     * judge 模式
     */
    @NotNull(message = "A-04-026")
    private JudgeCodeType judgeCodeType;

    /**
     * DIY judge code
     */
    private String judgeCode;

    /**
     * 将数据保存至 problemData
     *
     * @param problemData 被保存数据的 problem Data
     */
    public void toProblemData(ProblemData problemData) {
        problemData.setJudgeCodeType(judgeCodeType);
        problemData.setJudgeCode(judgeCode);
    }

}
