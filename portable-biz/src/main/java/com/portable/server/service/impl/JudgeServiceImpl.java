package com.portable.server.service.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.model.ServiceVerifyCode;
import com.portable.server.model.judge.entity.JudgeContainer;
import com.portable.server.model.judge.entity.UpdateJudgeContainer;
import com.portable.server.service.JudgeService;
import com.portable.server.support.JudgeSupport;
import com.portable.server.type.SolutionStatusType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shiroha
 */
@Component
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private JudgeSupport judgeSupport;

    @Override
    public ServiceVerifyCode getServerCode() {
        return judgeSupport.getServiceCode();
    }

    @Override
    public List<JudgeContainer> getJudgeContainerList() {
        return judgeSupport.getJudgeContainerList();
    }

    @Override
    public void updateJudgeContainer(UpdateJudgeContainer updateJudgeContainer) throws PortableException {
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
