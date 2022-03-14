package com.portable.server.manager;

import com.portable.server.exception.PortableException;
import com.portable.server.model.fs.FileData;
import com.portable.server.type.FileStoreType;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author shiroha
 */
@Component
public interface GridFsManager {

    /**
     * 上传图片
     * @param inputStream 图片的文件流
     * @param name 文件名
     * @param contentType 文件类型
     * @return 图片的 id
     * @throws PortableException 文件类型不匹配则抛出
     */
    String uploadImage(InputStream inputStream, String name, String contentType) throws PortableException;

    /**
     * 上传并移除图片
     * @param removeId 上一次的头像 id
     * @param inputStream 头像文件流
     * @param name 文件名
     * @param contentType 文件类型
     * @return 头像的 id
     * @throws PortableException 文件类型不匹配则抛出
     */
    String uploadAvatar(String removeId, InputStream inputStream, String name, String contentType) throws PortableException;

    /**
     * 获取文件
     * @param id 文件的 id
     * @param type 文件的类型
     * @return 文件信息
     * @throws PortableException 文件类型不匹配则抛出
     */
    FileData get(String id, FileStoreType type) throws PortableException;
}
