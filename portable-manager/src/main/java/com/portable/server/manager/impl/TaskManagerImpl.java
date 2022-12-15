package com.portable.server.manager.impl;

import java.util.Optional;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.TaskManager;
import com.portable.server.model.task.AbstractTask;
import com.portable.server.persistent.PriorityQueueHelper;
import com.portable.server.type.TaskType;

/**
 * @author shiroha
 */
public class TaskManagerImpl implements TaskManager {

    @Resource(name = "taskPriorityQueueHelper")
    private PriorityQueueHelper<AbstractTask> taskPriorityQueueHelper;

    @Override
    public Optional<AbstractTask> popTask() {
        return Optional.ofNullable(taskPriorityQueueHelper.poll());
    }

    @Override
    public void pushTask(AbstractTask task) {
        taskPriorityQueueHelper.offer(task);
    }

    @Override
    public void removeTask(Long taskBizId, TaskType taskType) {
        throw PortableException.of("S-00-000");
    }
}
