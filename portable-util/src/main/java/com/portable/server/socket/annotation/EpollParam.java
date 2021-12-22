package com.portable.server.socket.annotation;

import com.portable.server.socket.type.EpollDataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shiroha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface EpollParam {
    EpollDataType value() default EpollDataType.SIMPLE;
}
