package com.portable.server.model.task;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.portable.server.model.judge.JudgeContainer;
import com.portable.server.type.TaskType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shiroha
 */
@Getter
public abstract class AbstractTask implements Comparable<AbstractTask> {

    /**
     * 静态的工作 id，用于每次创建任务的时候 + 1
     */
    private static final AtomicLong LOCAL_WORK_SEQUENCE_ID;

    /**
     * 权重
     */
    private final Long weight;

    /**
     * 所属的任务类别
     */
    private final TaskType taskType;

    /**
     * 被分配到的 judge 容器
     */
    @Setter
    @JsonIgnore
    private JudgeContainer judgeContainer;

    static {
        LOCAL_WORK_SEQUENCE_ID = new AtomicLong(0);
    }

    /**
     * 获取当前 task 的业务 id
     *
     * @return 业务 id
     */
    public abstract Long getBizId();

    public AbstractTask(TaskType taskType) {
        this.taskType = taskType;
        long curWorkId = LOCAL_WORK_SEQUENCE_ID.getAndIncrement();

        this.weight = curWorkId + taskType.getWeightGrade();
    }

    @Override
    public int compareTo(AbstractTask o) {
        return Objects.compare(weight, o.getWeight(), Long::compareTo);
    }
}
