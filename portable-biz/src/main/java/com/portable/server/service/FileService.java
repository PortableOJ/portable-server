package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.type.FileStoreType;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author shiroha
 */
public interface FileService {

    /**
     * 上传头像
     * @param inputStream 头像文件流
     * @param name 文件名
     * @param contentType 文件类型
     * @return 头像的 id
     * @throws PortableException 类型不匹配则抛出
     */
    String uploadAvatar(InputStream inputStream, String name, String contentType) throws PortableException;

    /**
     * 上传图片
     * @param inputStream 图片的文件流
     * @param name 文件名
     * @param contentType 文件类型
     * @return 图片的 id
     * @throws PortableException 类型不匹配则抛出
     */
    String uploadImage(InputStream inputStream, String name, String contentType) throws PortableException;

    /**
     * 获取文件
     * @param id 文件的 id
     * @param type 文件类型
     * @param outputStream 输出流
     * @return 文件类型
     * @throws PortableException 类型不匹配则抛出
     */
    String get(String id, FileStoreType type, OutputStream outputStream) throws PortableException;
}
