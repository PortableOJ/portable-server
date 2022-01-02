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
     * 对应的题目 ID
     */
    private Long problemId;

    /**
     * 当前正在测试的 ID
     */
    private Integer curTestId;

    /**
     * 总共需要进行的 test 数量
     */
    private Integer maxTest;

    /**
     * 当前任务是否被终止
     */
    private Boolean killed;

    public Boolean nextTest() {
        curTestId++;
        return curTestId.equals(maxTest);
    }

    public SolutionJudgeWork(SolutionType solutionType) {
        super(JudgeWorkType.toJudgeWorkType(solutionType));
    }
}
