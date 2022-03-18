package com.portable.server.model.request.problem;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

/**
 * @author shiroha
 */
@Data
@Builder
public class ProblemTestRequest {

    /**
     * 问题的 ID
     */
    private Long id;

    /**
     * 测试文件流
     */
    private InputStream inputStream;

    /**
     * 测试名称
     */
    private String name;
}
