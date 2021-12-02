package com.portable.server.interceptor;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.AccountManager;
import com.portable.server.manager.NormalUserManager;
import com.portable.server.model.user.User;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.util.ExceptionConstant;
import com.portable.server.util.RequestSessionConstant;
import com.portable.server.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author shiroha
 */
@Slf4j
public class NeedLoginInterceptor implements HandlerInterceptor {

    @Resource
    private AccountManager accountManager;

    @Resource
    private NormalUserManager normalUserManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            // 没有对应的方法 -> 404
            request.getRequestDispatcher(ExceptionConstant.NOT_FOUND).forward(request,response);
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        NeedLogin classRequirement = handlerMethod.getMethod().getDeclaringClass().getAnnotation(NeedLogin.class);
        NeedLogin methodRequirement = handlerMethod.getMethodAnnotation(NeedLogin.class);
        if (checkLogin(classRequirement, request) && checkLogin(methodRequirement, request)) {
            return true;
        }
        throw PortableException.of("A-02-001");
    }

    /**
     * 通过了返回 true，没有通过返回 false
     * @param needLogin 是否需要登录的注解
     * @param request 请求
     * @return 是否通过
     */
    private Boolean checkLogin(NeedLogin needLogin, HttpServletRequest request) throws PortableException {
        if (needLogin == null || !needLogin.value()) {
            return true;
        }
        HttpSession httpSession = request.getSession();
        Object idObject = httpSession.getAttribute(RequestSessionConstant.USER_ID);
        if (!(idObject instanceof Long)) {
            return false;
        }
        Long id = (Long) idObject;
        UserContext.set(id);

        if (Objects.isNull(UserContext.ctx().getId())) {
            User user = accountManager.getAccountById(id);
            UserContext.set(user);
            NormalUserData userData = normalUserManager.getUserDataById(UserContext.ctx().getDataId());
            UserContext.set(userData);
        }

        return true;
    }
}
