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

    private static Boolean firstServerCode;

    static {
        firstServerCode = true;
    }

    @Resource
    private JudgeSupport judgeSupport;

    @Override
    public ServiceVerifyCode getServiceCode() {
        return judgeSupport.getServiceCode();
    }

    @Override
    public String getTheServiceCodeFirstTime() {
        if (firstServerCode) {
            firstServerCode = false;
            return judgeSupport.getServiceCode().getCode();
        }
        return null;
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
    public void killJudge(Long solutionId) throws PortableException {
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
