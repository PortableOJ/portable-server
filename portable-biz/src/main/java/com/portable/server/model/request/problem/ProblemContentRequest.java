package com.portable.server.model.request.problem;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.problem.ProblemData;
import lombok.Data;

import java.util.List;

/**
 * @author shiroha
 */
@Data
public class ProblemContentRequest {

    /**
     * 问题的 ID
     */
    private Long id;

    /**
     * 标题
     */
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
