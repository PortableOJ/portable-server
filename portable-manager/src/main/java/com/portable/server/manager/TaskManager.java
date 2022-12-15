package com.portable.server.manager;

import java.util.Optional;

import com.portable.server.model.task.AbstractTask;
import com.portable.server.type.TaskType;

/**
 * @author shiroha
 */
public interface TaskManager {

    /**
     * 获取一个优先级最高的 task
     *
     * @return 目前优先级别最高的 task
     */
    Optional<AbstractTask> popTask();

    /**
     * 添加一个 task 到任务中
     *
     * @param task task 信息
     */
    void pushTask(AbstractTask task);

    /**
     * 尝试删除一个 task
     *
     * @param taskBizId 业务对象 id，比如题目的 id，提交的 id
     * @param taskType  任务的类型
     */
    void removeTask(Long taskBizId, TaskType taskType);
}
