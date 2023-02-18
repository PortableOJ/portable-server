package com.portable.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.portable.server.helper.redis.ServiceVerifyCode;
import com.portable.server.model.judge.JudgeContainer;
import com.portable.server.service.impl.JudgeMaintenanceServiceImpl;
import com.portable.server.support.impl.JudgeSupportImpl;
import com.portable.server.test.MockedValueMaker;
import com.portable.server.type.SolutionStatusType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JudgeMaintenanceServiceImplTest {

    @InjectMocks
    private JudgeMaintenanceServiceImpl judgeService;

    @Mock
    private JudgeSupportImpl judgeSupport;

    private static final String MOCKED_CODE = MockedValueMaker.mString();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetServerCode() {
        ServiceVerifyCode serviceVerifyCode = ServiceVerifyCode.builder()
                .code(MOCKED_CODE)
                .temporary(false)
                .build();

        Mockito.when(judgeSupport.queryServerCode()).thenReturn(serviceVerifyCode);

        ServiceVerifyCode retVal = judgeService.getServiceCode();

        Assertions.assertEquals(MOCKED_CODE, retVal.getCode());
    }

    @Test
    void testGetTheServerCodeFirstTime() {
        ServiceVerifyCode serviceVerifyCode = ServiceVerifyCode.builder()
                .code(MOCKED_CODE)
                .temporary(false)
                .build();

        Mockito.when(judgeSupport.queryServerCode()).thenReturn(serviceVerifyCode);

        String retVal = judgeService.getTheServiceCodeFirstTime();

        String retVal2 = judgeService.getTheServiceCodeFirstTime();

        Assertions.assertEquals(MOCKED_CODE, retVal);
        Assertions.assertNull(retVal2);
    }

    @Test
    void getJudgeContainerList() {
        List<JudgeContainer> judgeContainerList = new ArrayList<JudgeContainer>() {{
            add(JudgeContainer.builder().build());
        }};
        Mockito.when(judgeSupport.getJudgeContainerList()).thenReturn(judgeContainerList);

        List<JudgeContainer> retVal = judgeService.getJudgeContainerList();

        Assertions.assertEquals(1, retVal.size());
    }

    @Test
    void testUpdateJudgeContainer() {
        UpdateJudgeContainer updateJudgeContainer = UpdateJudgeContainer.builder()
                .build();

        judgeService.updateJudgeContainer(updateJudgeContainer);

        Mockito.verify(judgeSupport).updateJudgeContainer(updateJudgeContainer);
    }

    @Test
    void testKillJudge() {
        Long id = null;

        judgeService.killJudge(id);

        Mockito.verify(judgeSupport).killJudgeTask(id, SolutionStatusType.SYSTEM_ERROR, null, null);
    }

    @Test
    void testKillTest() {
        Long id = null;

        judgeService.killTest(id);

        Mockito.verify(judgeSupport).killTestTask(id, false);
    }

    @Test
    void testStopJudge() {
        String judgeCode = "";

        judgeService.stopJudge(judgeCode);

        Mockito.verify(judgeSupport).killJudge(judgeCode);
    }
}