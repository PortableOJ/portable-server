package com.portable.server.interceptor;

import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.type.PermissionType;
import com.portable.server.util.ExceptionConstant;
import com.portable.server.util.UserContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            // 没有对应的方法 -> 404
            request.getRequestDispatcher(ExceptionConstant.NOT_FOUND).forward(request, response);
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        PermissionRequirement permissionRequirement = handlerMethod.getMethodAnnotation(PermissionRequirement.class);
        if (permissionRequirement == null) {
            return true;
        }
        String needPermission = Arrays.stream(permissionRequirement.value())
                .map(permissionType -> UserContext.ctx().getPermissionTypeSet().contains(permissionType)
                        ? null
                        : permissionType)
                .filter(Objects::nonNull)
                .map(PermissionType::getText)
                .collect(Collectors.joining(", "));
        if (!Strings.isBlank(needPermission)) {
            throw PortableException.of("A-02-002", needPermission);
        }
        return true;
    }
}
