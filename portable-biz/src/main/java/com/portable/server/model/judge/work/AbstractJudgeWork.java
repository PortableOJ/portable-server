package com.portable.server.model.judge.work;

import com.portable.server.type.JudgeWorkType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shiroha
 */
@Getter
public abstract class AbstractJudgeWork implements Comparable<AbstractJudgeWork> {

    /**
     * 所属的任务类别
     */
    private final JudgeWorkType judgeWorkType;

    /**
     * 被分配到的 judge 容器
     */
    @Setter
    private String judgeContainer;

    public AbstractJudgeWork(JudgeWorkType judgeWorkType) {
        this.judgeWorkType = judgeWorkType;
    }

    @Override
    public int compareTo(AbstractJudgeWork o) {
        return judgeWorkType.getWeight().compareTo(o.judgeWorkType.getWeight());
    }
}
