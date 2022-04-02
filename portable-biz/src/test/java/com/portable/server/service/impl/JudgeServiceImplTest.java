package com.portable.server.service.impl;

import com.portable.server.model.ServiceVerifyCode;
import com.portable.server.support.impl.JudgeSupportImpl;
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
class JudgeServiceImplTest {

    @InjectMocks
    private JudgeServiceImpl judgeService;

    @Mock
    private JudgeSupportImpl judgeSupport;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getServerCode() {
        ServiceVerifyCode serviceVerifyCode = ServiceVerifyCode.builder()
                .code("CODE")
                .temporary(false)
                .build();

        Mockito.when(judgeSupport.getServiceCode()).thenReturn(serviceVerifyCode);

        ServiceVerifyCode retVal = judgeService.getServiceCode();

        Assertions.assertEquals("CODE", retVal.getCode());
    }

    @Test
    void getTheServerCodeFirstTime() {
        ServiceVerifyCode serviceVerifyCode = ServiceVerifyCode.builder()
                .code("CODE")
                .temporary(false)
                .build();

        Mockito.when(judgeSupport.getServiceCode()).thenReturn(serviceVerifyCode);

        String retVal = judgeService.getTheServiceCodeFirstTime();

        String retVal2 = judgeService.getTheServiceCodeFirstTime();

        Assertions.assertEquals("CODE", retVal);
        Assertions.assertNull(retVal2);
    }

    @Test
    void getJudgeContainerList() {
    }

    @Test
    void updateJudgeContainer() {
    }

    @Test
    void killJudge() {
    }

    @Test
    void killTest() {
    }

    @Test
    void stopJudge() {
    }
}