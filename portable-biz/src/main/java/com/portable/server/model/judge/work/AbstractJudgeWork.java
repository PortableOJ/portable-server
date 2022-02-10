package com.portable.server.model.judge.work;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portable.server.model.judge.entity.JudgeContainer;
import com.portable.server.type.JudgeWorkType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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
    @JsonIgnore
    private JudgeContainer judgeContainer;

    /**
     * 当前正在测试的 ID
     */
    @Setter
    private Integer curTestId;

    /**
     * 总共需要进行的 test 数量
     */
    @Setter
    private Integer maxTest;

    public Integer nextTest() {
        return curTestId++;
    }

    public Boolean testOver() {
        return Objects.equals(curTestId, maxTest);
    }

    public AbstractJudgeWork(JudgeWorkType judgeWorkType) {
        this.judgeWorkType = judgeWorkType;
    }

    @Override
    public int compareTo(AbstractJudgeWork o) {
        return judgeWorkType.getWeight().compareTo(o.judgeWorkType.getWeight());
    }
}
