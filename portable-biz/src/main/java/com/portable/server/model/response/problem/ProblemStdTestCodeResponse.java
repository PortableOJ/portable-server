package com.portable.server.model.response.problem;

import com.portable.server.model.problem.ProblemData;
import lombok.Data;

import java.util.List;

/**
 * @author shiroha
 */
@Data
public class ProblemStdTestCodeResponse {

    /**
     * 标准代码，必定为通过
     */
    private ProblemData.StdCode stdCode;

    /**
     * 测试代码（并不一定是通过，可能是故意错误的，但是一定有一份是通过的）
     */
    private List<ProblemData.StdCode> testCodeList;

    private ProblemStdTestCodeResponse(ProblemData problemData) {
        this.stdCode = problemData.getStdCode();
        this.testCodeList = problemData.getTestCodeList();
    }

    public static ProblemStdTestCodeResponse of(ProblemData problemData) {
        return new ProblemStdTestCodeResponse(problemData);
    }
}
