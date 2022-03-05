package com.portable.server.model.fs;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

/**
 * @author shiroha
 */
@Data
@Builder
public class FileData {

    /**
     * 文件流
     */
    private InputStream inputStream;

    /**
     * 文件类型
     */
    private String contentType;
}
