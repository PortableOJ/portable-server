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
    void testPreHandleWithNoAnnotation() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(httpSession.getAttribute(RequestSessionConstant.CAPTCHA)).thenReturn(null);
        Mockito.when(request.getSession()).thenReturn(httpSession);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        HandlerMethod handler = Mockito.mock(HandlerMethod.class);
        Mockito.when(handler.getMethodAnnotation(CheckCaptcha.class)).thenReturn(null);

        UserContext userContext = new UserContext();
        userContext.setUserCaptchaMap(new HashMap<>(1));
        userContext.getUserCaptchaMap().put("default", System.currentTimeMillis());
        Mockito.when(UserContext.ctx()).thenReturn(userContext);

        boolean retVal = captchaInterceptor.preHandle(request, response, handler);

        Assertions.assertTrue(retVal);
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

    @Test
    void testPreHandleWithNoCaptchaValue() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(httpSession.getAttribute(RequestSessionConstant.CAPTCHA)).thenReturn("abc");
        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.when(request.getHeader(RequestSessionConstant.CAPTCHA)).thenReturn(null);

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

    @Test
    void testPreHandleWithNotEqual() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(httpSession.getAttribute(RequestSessionConstant.CAPTCHA)).thenReturn("abc");
        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.when(request.getHeader(RequestSessionConstant.CAPTCHA)).thenReturn("edf");

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
            Assertions.assertEquals("A-00-002", e.getCode());
        }
    }

    @Test
    void testPreHandleWithSuccess() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(httpSession.getAttribute(RequestSessionConstant.CAPTCHA)).thenReturn("abc");
        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.when(request.getHeader(RequestSessionConstant.CAPTCHA)).thenReturn("ABC");

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        HandlerMethod handler = Mockito.mock(HandlerMethod.class);
        CheckCaptcha methodRequirement = Mockito.mock(CheckCaptcha.class);
        Mockito.when(methodRequirement.name()).thenReturn("default");
        Mockito.when(methodRequirement.value()).thenReturn(-1L);
        Mockito.when(handler.getMethodAnnotation(CheckCaptcha.class)).thenReturn(methodRequirement);

        UserContext userContext = new UserContext();
        userContext.setUserCaptchaMap(new HashMap<>(1));
        userContext.getUserCaptchaMap().put("default", 0L);
        Mockito.when(UserContext.ctx()).thenReturn(userContext);

        boolean retVal = captchaInterceptor.preHandle(request, response, handler);

        Assertions.assertTrue(retVal);

        Assertions.assertTrue(System.currentTimeMillis() - userContext.getUserCaptchaMap().get("default") < 1000);
    }

    @Test
    void testPreHandleWithNotInTime() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(httpSession.getAttribute(RequestSessionConstant.CAPTCHA)).thenReturn("abc");
        Mockito.when(request.getSession()).thenReturn(httpSession);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        HandlerMethod handler = Mockito.mock(HandlerMethod.class);
        CheckCaptcha methodRequirement = Mockito.mock(CheckCaptcha.class);
        Mockito.when(methodRequirement.name()).thenReturn("default");
        Mockito.when(methodRequirement.value()).thenReturn(100L);
        Mockito.when(handler.getMethodAnnotation(CheckCaptcha.class)).thenReturn(methodRequirement);

        UserContext userContext = new UserContext();
        userContext.setUserCaptchaMap(new HashMap<>(1));
        userContext.getUserCaptchaMap().put("default", 0L);
        Mockito.when(UserContext.ctx()).thenReturn(userContext);

        boolean retVal = captchaInterceptor.preHandle(request, response, handler);

        Assertions.assertTrue(retVal);

        Assertions.assertTrue(System.currentTimeMillis() - userContext.getUserCaptchaMap().get("default") < 1000);

        Mockito.verify(request, Mockito.never()).getHeader(RequestSessionConstant.CAPTCHA);
    }

    @Test
    void testPreHandleWithInTime() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(httpSession.getAttribute(RequestSessionConstant.CAPTCHA)).thenReturn("abc");
        Mockito.when(request.getHeader(RequestSessionConstant.CAPTCHA)).thenReturn("ABC");
        Mockito.when(request.getSession()).thenReturn(httpSession);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        HandlerMethod handler = Mockito.mock(HandlerMethod.class);
        CheckCaptcha methodRequirement = Mockito.mock(CheckCaptcha.class);
        Mockito.when(methodRequirement.name()).thenReturn("default");
        Mockito.when(methodRequirement.value()).thenReturn(10000L);
        Mockito.when(handler.getMethodAnnotation(CheckCaptcha.class)).thenReturn(methodRequirement);

        UserContext userContext = new UserContext();
        userContext.setUserCaptchaMap(new HashMap<>(1));
        userContext.getUserCaptchaMap().put("default", System.currentTimeMillis() - 1000L);
        Mockito.when(UserContext.ctx()).thenReturn(userContext);

        boolean retVal = captchaInterceptor.preHandle(request, response, handler);

        Assertions.assertTrue(retVal);

        Assertions.assertTrue(System.currentTimeMillis() - userContext.getUserCaptchaMap().get("default") < 1000);
    }
}