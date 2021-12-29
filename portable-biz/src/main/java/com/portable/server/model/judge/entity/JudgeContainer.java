package com.portable.server.model.judge.entity;

import com.portable.server.model.judge.work.SolutionJudgeWork;
import com.portable.server.model.judge.work.TestJudgeWork;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author shiroha
 */
@Data
public class JudgeContainer {

    /**
     * 此 Judge 的 judgeCode
     */
    String judgeCode;

    /**
     * 所有的 socket
     */
    Set<String> sockets;

    /**
     * 正在进行中的 judge 任务
     */
    Map<Long, SolutionJudgeWork> judgeWorkMap;

    /**
     * 正在进行中的 test 任务
     */
    Map<Long, TestJudgeWork> testWorkMap;
}
