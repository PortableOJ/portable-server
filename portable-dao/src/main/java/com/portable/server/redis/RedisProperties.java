package com.portable.server.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shiroha
 */
@Data
@Configuration
@ConfigurationProperties(RedisProperties.PREFIX)
public class RedisProperties {

    /**
     * 匹配前缀
     */
    public static final String PREFIX = "redis";

    private Boolean useCluster;

    private String host;

    private Integer port;

    private Integer database;

    private String password;

    private String clientName;

    private Integer maxActive;

    private Integer maxWait;

    private Integer maxIdle;

    private Integer minIdle;

    private Integer timeout;

    private Integer soTimeout;
}
