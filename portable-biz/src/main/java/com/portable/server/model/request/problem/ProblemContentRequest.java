package com.portable.server.model.request.problem;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemContentRequest {

    /**
     * 问题的 ID
     */
    private Long id;

    /**
     * 标题
     */
    @NotNull(message = "A-04-018")
    @Size(max = 60, message = "A-04-018")
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 输入格式
     */
    private String input;

    /**
     * 输出格式
     */
    private String output;

    /**
     * 样例
     */
    @Valid
    @NotNull(message = "A-04-019")
    @Size(min = 1, max = 5, message = "A-04-019")
    private List<ProblemData.Example> example;

    public void toProblem(Problem problem) {
        problem.setId(this.id);
        problem.setTitle(this.title);
    }

    public void toProblemData(ProblemData problemData) {
        problemData.setDescription(this.description);
        problemData.setInput(this.input);
        problemData.setOutput(this.output);
        problemData.setExample(this.example);
    }
}
