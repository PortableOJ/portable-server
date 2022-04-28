package com.portable.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shiroha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CheckCaptcha {

    /**
     * 间隔多少毫秒就需要检查
     */
    long value() default -1;

    /**
     * 相同的名字将会被同时记录
     */
    String name() default "default";
}
