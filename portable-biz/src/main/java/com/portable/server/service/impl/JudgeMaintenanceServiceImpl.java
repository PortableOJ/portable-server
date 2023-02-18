package com.portable.server.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.portable.server.model.judge.JudgeContainer;
import com.portable.server.model.judge.ServerCode;
import com.portable.server.model.judge.UpdateJudgeContainer;
import com.portable.server.service.JudgeMaintenanceService;
import com.portable.server.support.JudgeSupport;
import com.portable.server.type.SolutionStatusType;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class JudgeMaintenanceServiceImpl implements JudgeMaintenanceService {

    @Resource
    private JudgeSupport judgeSupport;

    @Override
    public ServerCode getServiceCode() {
        return judgeSupport.queryServerCode();
    }

    @Override
    public List<JudgeContainer> getJudgeContainerList() {
        return judgeSupport.getJudgeContainerList();
    }

    @Override
    public void updateJudgeContainer(UpdateJudgeContainer updateJudgeContainer) {
        judgeSupport.updateJudgeContainer(updateJudgeContainer);
    }

    @Override
    public void killJudge(Long solutionId) {
        judgeSupport.killJudgeTask(solutionId, SolutionStatusType.SYSTEM_ERROR, null, null);
    }

    @Override
    public void killTest(Long problemId) {
        judgeSupport.killTestTask(problemId, false);
    }

    @Override
    public void stopJudge(String judgeCode) {
        judgeSupport.killJudge(judgeCode);
    }
}
