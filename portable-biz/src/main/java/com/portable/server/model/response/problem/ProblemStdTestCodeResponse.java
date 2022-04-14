package com.portable.server.model.response.problem;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.LanguageType;
import com.portable.server.type.SolutionStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Data
public class ProblemStdTestCodeResponse {

    /**
     * 标准代码，必定为通过
     */
    private StdCode stdCode;

    /**
     * 测试代码（并不一定是通过，可能是故意错误的，但是一定有一份是通过的）
     */
    private List<StdCode> testCodeList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StdCode {

        /**
         * 文件名
         */
        private String name;

        /**
         * 期望结果
         */
        private SolutionStatusType expectResultType;

        /**
         * 所使用的语言
         */
        private LanguageType languageType;

        /**
         * 最终的测试 ID
         */
        private Long solutionId;

        private SolutionStatusType solutionStatusType;

        public static StdCode of(ProblemData.StdCode code) {
            return StdCode.builder()
                    .name(code.getName())
                    .expectResultType(code.getExpectResultType())
                    .languageType(code.getLanguageType())
                    .solutionId(code.getSolutionId())
                    .build();
        }
    }

    private ProblemStdTestCodeResponse(ProblemData problemData) {
        this.stdCode = StdCode.of(problemData.getStdCode());
        this.testCodeList = problemData.getTestCodeList().stream()
                .map(StdCode::of)
                .collect(Collectors.toList());
    }

    public static ProblemStdTestCodeResponse of(ProblemData problemData) {
        return new ProblemStdTestCodeResponse(problemData);
    }
}
