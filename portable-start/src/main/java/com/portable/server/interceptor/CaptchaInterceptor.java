package com.portable.server.interceptor;

import com.portable.server.annotation.CheckCaptcha;
import com.portable.server.exception.PortableException;
import com.portable.server.util.RequestSessionConstant;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author shiroha
 */
@Slf4j
public class CaptchaInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        CheckCaptcha methodRequirement = handlerMethod.getMethodAnnotation(CheckCaptcha.class);
        if (methodRequirement == null) {
            return true;
        }
        HttpSession httpSession = request.getSession();
        String captchaAnswer = (String) httpSession.getAttribute(RequestSessionConstant.CAPTCHA);
        String captchaValue = request.getHeader(RequestSessionConstant.CAPTCHA);
        if (captchaAnswer == null || captchaValue == null) {
            throw PortableException.of("A-00-002");
        }
        captchaAnswer = captchaAnswer.toLowerCase();
        captchaValue = captchaValue.toLowerCase();
        httpSession.removeAttribute(RequestSessionConstant.CAPTCHA);
        if (Objects.equals(captchaAnswer, captchaValue)) {
            return true;
        }
        throw PortableException.of("A-00-002");
    }
}
