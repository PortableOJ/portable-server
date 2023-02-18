package com.portable.server;

import com.portable.server.banner.PortableBanner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author shiroha
 */
@SpringBootApplication
@ServletComponentScan("com.portable.server.filter")
@MapperScan(basePackages = {"com.portable.server.mapper"})
public class PortableStartApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(PortableStartApplication.class);
        // 设置自定义 Banner
        springApplication.setBanner(new PortableBanner());
        // 启动 Spring Boot
        springApplication.run(args);
    }

}
