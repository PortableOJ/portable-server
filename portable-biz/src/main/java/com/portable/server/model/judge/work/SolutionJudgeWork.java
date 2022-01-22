package com.portable.server.model.judge.work;

import com.portable.server.type.JudgeWorkType;
import com.portable.server.type.SolutionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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
     * 对应的题目 ID
     */
    private Long problemId;

    public SolutionJudgeWork(SolutionType solutionType) {
        super(JudgeWorkType.toJudgeWorkType(solutionType));
    }
}
