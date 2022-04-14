package com.portable.server.model.request.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
