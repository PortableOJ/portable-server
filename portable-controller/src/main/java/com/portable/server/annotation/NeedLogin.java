package com.portable.server.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shiroha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NeedLogin {

    /**
     * 是否是必须登录
     */
    boolean value() default true;

    /**
     * 强制要求标准用户
     */
    boolean normal() default false;
}
