package com.portable.server.interceptor;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.UserManager;
import com.portable.server.manager.NormalUserManager;
import com.portable.server.model.user.User;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.util.ExceptionConstant;
import com.portable.server.util.RequestSessionConstant;
import com.portable.server.util.UserContext;
import lombok.NonNull;
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
    private UserManager userManager;

    @Resource
    private NormalUserManager normalUserManager;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            // 没有对应的方法 -> 404
            request.getRequestDispatcher(ExceptionConstant.NOT_FOUND).forward(request, response);
            return false;
        }
        HttpSession httpSession = request.getSession();
        Object idObject = httpSession.getAttribute(RequestSessionConstant.USER_ID);
        if (idObject instanceof Long) {
            // 已经登录了，就不需要关心是不是需要登录了
            Long id = (Long) idObject;
            UserContext.restore(id);

            if (Objects.isNull(UserContext.ctx().getId())) {
                User user = userManager.getAccountById(id);
                UserContext.set(user);
                NormalUserData userData = normalUserManager.getUserDataById(UserContext.ctx().getDataId());
                UserContext.set(userData);
            }
            return true;
        }

        // 检查方法或者类，是否必需要登录
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NeedLogin classRequirement = handlerMethod.getMethod().getDeclaringClass().getAnnotation(NeedLogin.class);
        NeedLogin methodRequirement = handlerMethod.getMethodAnnotation(NeedLogin.class);
        if (checkLogin(classRequirement) || checkLogin(methodRequirement)) {
            throw PortableException.of("A-02-001");
        }
        return true;
    }

    /**
     * 需要登录则返回 true
     *
     * @param needLogin 是否需要登录的注解
     * @return 是否需要登录
     */
    private Boolean checkLogin(NeedLogin needLogin) {
        return needLogin != null && needLogin.value();
    }
}
