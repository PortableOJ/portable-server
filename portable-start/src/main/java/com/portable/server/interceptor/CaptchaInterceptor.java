package com.portable.server.interceptor;

import com.portable.server.annotation.CheckCaptcha;
import com.portable.server.exception.PortableException;
import com.portable.server.util.RequestSessionConstant;
import com.portable.server.util.UserContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author shiroha
 */
@Slf4j
public class CaptchaInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        CheckCaptcha methodRequirement = handlerMethod.getMethodAnnotation(CheckCaptcha.class);
        HttpSession httpSession = request.getSession();
        String captchaAnswer = (String) httpSession.getAttribute(RequestSessionConstant.CAPTCHA);
        // 当获取过之后，就应该移除掉，即使它没有
        httpSession.removeAttribute(RequestSessionConstant.CAPTCHA);

        if (methodRequirement == null) {
            return true;
        }
        UserContext userContext = UserContext.ctx();
        Long lastRequest = userContext.getUserCaptchaMap().get(methodRequirement.name());
        boolean timeIn = lastRequest != null && (System.currentTimeMillis() - lastRequest) < methodRequirement.value();
        if (timeIn || methodRequirement.value() < 0) {
            String captchaValue = request.getHeader(RequestSessionConstant.CAPTCHA);
            if (captchaAnswer == null || captchaValue == null) {
                throw PortableException.of("W-00-001");
            }
            if (!captchaAnswer.equalsIgnoreCase(captchaValue)) {
                throw PortableException.of("A-00-002");
            }
        }
        return true;
    }
}
