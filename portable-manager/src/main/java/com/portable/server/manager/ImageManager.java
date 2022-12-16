package com.portable.server.manager;

import java.io.InputStream;

import com.portable.server.exception.PortableException;
import com.portable.server.model.fs.FileData;
import com.portable.server.type.FileStoreType;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public interface ImageManager {

    /**
     * 上传图片
     *
     * @param inputStream 图片的文件流
     * @param name        文件名
     * @param contentType 文件类型
     * @return 图片的 id
     * @throws PortableException 文件类型不匹配则抛出
     */
    @NotNull
    String uploadImage(InputStream inputStream, String name, String contentType);

    /**
     * 上传并移除图片
     *
     * @param removeId    上一次的图片 id
     * @param inputStream 图片文件流
     * @param name        文件名
     * @param contentType 文件类型
     * @return 新图片的 id
     * @throws PortableException 文件类型不匹配则抛出
     */
    @NotNull
    String replaceImage(String removeId, InputStream inputStream, String name, String contentType);

    /**
     * 获取文件
     *
     * @param id   文件的 id
     * @param type 文件的类型
     * @return 文件信息
     * @throws PortableException 文件类型不匹配则抛出
     */
    @NotNull
    FileData getImage(String id, FileStoreType type);
}
