package com.portable.server.interceptor;

import com.portable.server.annotation.CheckCaptcha;
import com.portable.server.exception.PortableException;
import com.portable.server.util.RequestSessionConstant;
import com.portable.server.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
class CaptchaInterceptorTest {

    @InjectMocks
    private CaptchaInterceptor captchaInterceptor;

    private static MockedStatic<UserContext> userContextMockedStatic;

    @BeforeEach
    void setUp() {
        userContextMockedStatic = Mockito.mockStatic(UserContext.class);
    }

    @AfterEach
    void tearDown() {
        userContextMockedStatic.close();
    }

    @Test
    void testPreHandleWithNoCaptchaSession() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(httpSession.getAttribute(RequestSessionConstant.CAPTCHA)).thenReturn(null);
        Mockito.when(request.getSession()).thenReturn(httpSession);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        HandlerMethod handler = Mockito.mock(HandlerMethod.class);
        CheckCaptcha methodRequirement = Mockito.mock(CheckCaptcha.class);
        Mockito.when(methodRequirement.name()).thenReturn("default");
        Mockito.when(methodRequirement.value()).thenReturn(-1L);
        Mockito.when(handler.getMethodAnnotation(CheckCaptcha.class)).thenReturn(methodRequirement);

        UserContext userContext = new UserContext();
        userContext.setUserCaptchaMap(new HashMap<>(1));
        userContext.getUserCaptchaMap().put("default", System.currentTimeMillis());
        Mockito.when(UserContext.ctx()).thenReturn(userContext);

        try {
            captchaInterceptor.preHandle(request, response, handler);
            Assertions.fail();
        } catch (PortableException e) {
            Assertions.assertEquals("W-00-001", e.getCode());
        }
    }
}