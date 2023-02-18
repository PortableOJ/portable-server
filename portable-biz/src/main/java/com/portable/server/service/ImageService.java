package com.portable.server.service;

import java.io.InputStream;
import java.io.OutputStream;

import com.portable.server.type.FileStoreType;

/**
 * @author shiroha
 */
public interface ImageService {

    /**
     * 上传图片
     *
     * @param inputStream 图片的文件流
     * @param name        文件名
     * @param contentType 文件类型
     * @return 图片的 id
     */
    String uploadImage(InputStream inputStream, String name, String contentType);

    /**
     * 获取文件
     * @param id 文件的 id
     * @param type 文件类型
     * @param outputStream 输出流
     * @return 文件类型
     */
    String get(String id, FileStoreType type, OutputStream outputStream);
}
