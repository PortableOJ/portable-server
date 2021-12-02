package com.portable.server.config;

import com.portable.server.interceptor.NeedLoginInterceptor;
import com.portable.server.interceptor.PermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author shiroha
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(needLoginInterceptor()).addPathPatterns("/api/**");
        registry.addInterceptor(permissionInterceptor()).addPathPatterns("/api/**");
    }

    @Bean
    public NeedLoginInterceptor needLoginInterceptor() {
        return new NeedLoginInterceptor();
    }

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }
}
