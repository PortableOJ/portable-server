package com.portable.server.aop;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.type.PermissionType;
import com.portable.server.util.UserContext;

import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Aspect
@Component
public class PermissionMethodAspect {

    @Around("@annotation(com.portable.server.annotation.PermissionRequirement) && @annotation(permissionRequirement)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, PermissionRequirement permissionRequirement) throws Throwable {
        String needPermission = Arrays.stream(permissionRequirement.value())
                .filter(permissionType -> !UserContext.ctx().getPermissionTypeSet().contains(permissionType))
                .map(PermissionType::getText)
                .collect(Collectors.joining(", "));
        if (!Strings.isBlank(needPermission)) {
            throw PortableErrors.of("A-02-002", needPermission);
        }
        return proceedingJoinPoint.proceed();
    }
}
