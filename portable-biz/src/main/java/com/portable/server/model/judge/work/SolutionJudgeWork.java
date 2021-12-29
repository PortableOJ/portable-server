package com.portable.server.model.judge.work;

import com.portable.server.type.JudgeWorkType;
import com.portable.server.type.SolutionType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shiroha
 */
@Setter
@Getter
public class SolutionJudgeWork extends AbstractJudgeWork {

    /**
     * 对应的提交 ID
     */
    private Long solutionId;

    /**
     * 当前正在测试的 ID
     */
    private Long curTestId;

    /**
     * 当前任务是否被终止
     */
    private Boolean killed;

    public SolutionJudgeWork(JudgeWorkType judgeWorkType) {
        super(judgeWorkType);
    }

    public SolutionJudgeWork(SolutionType solutionType) {
        super(JudgeWorkType.toJudgeWorkType(solutionType));
    }
}
