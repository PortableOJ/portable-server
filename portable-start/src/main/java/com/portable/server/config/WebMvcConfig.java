package com.portable.server.config;

import com.portable.server.interceptor.NeedLoginInterceptor;
import com.portable.server.interceptor.PermissionInterceptor;
import com.portable.server.util.UserContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author shiroha
 */
@Configuration
@EnableOpenApi
@EnableScheduling
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

    /**
     * 用于获取 redis 的模版注入，不需要使用此 bean
     * @return ignore
     */
    @Bean
    public UserContext userContext() {
        return new UserContext();
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("Portable")
                        .description("Portable 项目接口文档")
                        .contact(new Contact("Shiroha", "https://github.com/hukeqing/", "keqing.hu@icloud.com"))
                        .version("1.0")
                        .build())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.portable.server.controller"))
                .paths(s -> !s.matches("^/api/error/.+$"))
                .build();
    }
}
