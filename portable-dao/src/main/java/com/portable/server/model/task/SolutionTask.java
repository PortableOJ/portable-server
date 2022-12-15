package com.portable.server.model.task;

import com.portable.server.type.SolutionType;
import com.portable.server.type.TaskType;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shiroha
 */
@Setter
@Getter
public class SolutionTask extends AbstractTask {

    /**
     * 对应的提交 ID
     */
    private Long solutionId;

    /**
     * 对应的题目 ID
     */
    private Long problemId;

    public SolutionTask(SolutionType solutionType) {
        super(TaskType.toJudgeWorkType(solutionType));
    }

    @Override
    public Long getBizId() {
        return solutionId;
    }
}
