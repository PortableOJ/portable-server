package com.portable.server.model.response.problem;

import com.portable.server.model.problem.Problem;
import com.portable.server.model.solution.Solution;
import com.portable.server.type.ProblemListStatusType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class ProblemListResponse {

    /**
     * 题目的 ID
     */
    private Long id;

    /**
     * 题目的标题
     */
    private String title;

    /**
     * 题目的状态
     */
    private ProblemStatusType status;

    /**
     * 题目的访问权限
     */
    private ProblemAccessType accessType;

    /**
     * 历史提交数量
     */
    private Integer submissionCount;

    /**
     * 历史通过的数量
     */
    private Integer acceptCount;

    /**
     * 当前题目的通过状态
     */
    private ProblemListStatusType problemListStatusType;

    private ProblemListResponse(Problem problem, ProblemListStatusType problemListStatusType) {
        this.id = problem.getId();
        this.title = problem.getTitle();
        this.status = problem.getStatusType();
        this.accessType = problem.getAccessType();
        this.submissionCount = problem.getSubmissionCount();
        this.acceptCount = problem.getAcceptCount();
        this.problemListStatusType = problemListStatusType;
    }


    public static ProblemListResponse of(Problem problem, Solution solution) {
        return new ProblemListResponse(problem,
                solution == null
                        ? ProblemListStatusType.NEVER_SUBMIT
                        : ProblemListStatusType.of(solution.getStatus()));
    }
}
