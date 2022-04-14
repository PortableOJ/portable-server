package com.portable.server.model.fs;

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
