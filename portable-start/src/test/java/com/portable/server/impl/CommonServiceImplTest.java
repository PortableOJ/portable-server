package com.portable.server.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.portable.server.exception.PortableException;
import com.portable.server.service.impl.CommonServiceImpl;
import com.portable.server.support.impl.CaptchaSupportImpl;
import com.portable.server.test.MockedValueMaker;
import com.portable.server.util.StreamUtils;

import com.Ostermiller.util.CircularByteBuffer;
import com.alibaba.fastjson.JSONObject;
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
class CommonServiceImplTest {

    @InjectMocks
    private CommonServiceImpl commonService;

    @Mock
    private CaptchaSupportImpl captchaSupport;

    @BeforeEach
    void setUp() {
        commonService.init();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetVersionName() {
        commonService.init();
        Assertions.assertNotNull(commonService.getVersionName());
    }

    @Test
    void testGetEnumDescWithFail() {
        try {
            commonService.getEnumDesc(MockedValueMaker.mString());
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("A-06-001", e.getCode());
        }
    }

    @Test
    void testGetEnumDescWithSucceess() {
        Map<String, JSONObject> retVal = commonService.getEnumDesc("AccountType");
        Assertions.assertTrue(retVal.containsKey("NORMAL"));
    }

    @Test
    void getCaptcha() throws IOException {
        String MOCKED_CAPTCHA_VALUE = MockedValueMaker.mString();
        String MOCKED_CAPTCHA_BUFFER = MockedValueMaker.mString();
        Mockito.when(captchaSupport.getCaptcha(Mockito.any())).thenAnswer(invocationOnMock -> {
            OutputStream outputStream = invocationOnMock.getArgument(0);
            outputStream.write(MOCKED_CAPTCHA_BUFFER.getBytes());
            return MOCKED_CAPTCHA_VALUE;
        });

        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        String retVal = commonService.getCaptcha(circularByteBuffer.getOutputStream());
        circularByteBuffer.getOutputStream().close();

        Assertions.assertEquals(MOCKED_CAPTCHA_VALUE, retVal);
        Assertions.assertEquals(MOCKED_CAPTCHA_BUFFER, StreamUtils.read(circularByteBuffer.getInputStream()));
    }
}