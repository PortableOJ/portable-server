package com.portable.server.model.request.problem;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile fileData;

    /**
     * 测试名称
     */
    private String name;
}
