package com.portable.server.interceptor;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.exception.PortableException;
import com.portable.server.util.ExceptionConstant;
import com.portable.server.util.RequestSessionConstant;
import com.portable.server.util.UserContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author shiroha
 */
@Slf4j
public class NeedLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            // 没有对应的方法 -> 404
            request.getRequestDispatcher(ExceptionConstant.NOT_FOUND).forward(request, response);
            return false;
        }
        HttpSession httpSession = request.getSession();
        Object idObject = httpSession.getAttribute(RequestSessionConstant.USER_ID);
        Boolean isNormal = null;
        if (idObject instanceof Long) {
            // 已经有登录的 id 了，尝试还原数据
            Long id = (Long) idObject;
            if (UserContext.restore(id)) {
                isNormal = UserContext.ctx().getType().getIsNormal();
            } else {
                UserContext.set(UserContext.getNullUser());
            }
        } else {
            UserContext.set(UserContext.getNullUser());
        }

        // 已经登录且为标准用户
        if (Boolean.TRUE.equals(isNormal)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NeedLogin methodRequirement = handlerMethod.getMethodAnnotation(NeedLogin.class);
        if (methodRequirement == null) {
            // 不需要登录
            return true;
        } else if (isNormal == null) {
            throw PortableException.of("A-02-001");
        } else if (methodRequirement.normal()) {
            // 不可能是标准用户时
            throw PortableException.of("A-01-011");
        }
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        UserContext.remove();
    }
}
