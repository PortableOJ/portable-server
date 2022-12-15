package com.portable.server.model.task;

import com.portable.server.type.TaskType;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shiroha
 */
@Getter
@Setter
public class ProblemTask extends AbstractTask {

    /**
     * 对应的题目 ID
     */
    private Long problemId;

    public ProblemTask() {
        super(TaskType.TEST);
    }

    @Override
    public Long getBizId() {
        return problemId;
    }
}
