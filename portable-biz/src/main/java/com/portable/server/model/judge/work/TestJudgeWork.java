package com.portable.server.model.judge.work;

import com.portable.server.type.JudgeWorkType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shiroha
 */
@Getter
@Setter
public class TestJudgeWork extends AbstractJudgeWork {

    /**
     * 对应的题目 ID
     */
    private Long problemId;

    /**
     * 当前正在测试的 ID
     */
    private Long curTestId;

    public TestJudgeWork() {
        super(JudgeWorkType.TEST);
    }
}
