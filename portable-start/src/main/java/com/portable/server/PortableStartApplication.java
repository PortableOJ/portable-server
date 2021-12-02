package com.portable.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
//@EnableTransactionManagement
@ServletComponentScan("com.portable.server.filter")
@MapperScan(basePackages = {"com.portable.server.mapper"})
public class PortableStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortableStartApplication.class, args);
    }

}
