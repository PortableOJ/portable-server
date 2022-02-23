package com.portable.server.kit;

import com.portable.server.exception.PortableException;

import java.io.InputStream;

/**
 * @author shiroha
 */
public interface FileKit {

    /**
     * 创建一个目录，如果不存在的话
     * @param dir 目录
     * @throws PortableException 创建失败则抛出错误
     */
    void createDirIfNotExist(String dir) throws PortableException;

    /**
     * 创建或者覆盖文件
     * @param filePath 文件路径
     * @param inputStream 输入流
     * @throws PortableException 写入失败则抛出错误
     */
    void saveFileOrOverwrite(String filePath, InputStream inputStream) throws PortableException;

    /**
     * 创建或者覆盖文件
     * @param filePath 文件路径
     * @param inputStream 输入内容
     * @throws PortableException 写入失败则抛出错误
     */
    void saveFileOrOverwrite(String filePath, byte[] inputStream) throws PortableException;

    /**
     * 增加内容至文件
     * @param filePath 文件路径
     * @param inputStream 输入内容
     * @throws PortableException 写入失败或文件不存在则抛出错误
     */
    void appendFile(String filePath, byte[] inputStream) throws PortableException;

    /**
     * 如果文件存在则删除
     * @param filePath 文件路径
     * @throws PortableException 删除失败则抛出错误
     */
    void deleteFileIfExist(String filePath) throws PortableException;

    /**
     * 获取文件内容
     * @param filePath 文件路径
     * @return 文件流
     * @throws PortableException 打开文件流失败则抛出错误
     */
    InputStream getFile(String filePath) throws PortableException;
}
