package com.portable.server.kit;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.portable.server.exception.PortableException;

/**
 * @author shiroha
 */
public interface FileKit {

    /**
     * 创建一个目录，如果不存在的话
     *
     * @param dir 目录
     * @throws PortableException 创建失败则抛出错误
     */
    void createDirIfNotExist(String dir);

    /**
     * 创建或者覆盖文件
     *
     * @param filePath    文件路径
     * @param inputStream 输入流
     * @throws PortableException 写入失败则抛出错误
     */
    void saveFileOrOverwrite(String filePath, InputStream inputStream);

    /**
     * 创建或者覆盖文件，记得关闭
     *
     * @param filePath 文件路径
     * @return 写入流
     * @throws PortableException 写入失败则抛出错误
     */
    OutputStream saveFileOrOverwrite(String filePath);

    /**
     * 创建或者覆盖文件
     *
     * @param filePath    文件路径
     * @param inputStream 输入内容
     * @throws PortableException 写入失败则抛出错误
     */
    void saveFileOrOverwrite(String filePath, byte[] inputStream);

    /**
     * 增加内容至文件
     *
     * @param filePath    文件路径
     * @param inputStream 输入内容
     * @throws PortableException 写入失败或文件不存在则抛出错误
     */
    void appendFile(String filePath, byte[] inputStream);

    /**
     * 如果文件存在则删除
     *
     * @param filePath 文件路径
     * @throws PortableException 删除失败则抛出错误
     */
    void deleteFileIfExist(String filePath);

    /**
     * 获取文件内容
     *
     * @param filePath 文件路径
     * @return 文件流
     * @throws PortableException 打开文件流失败则抛出错误
     */
    InputStream getFileInput(String filePath);

    /**
     * 获取目录下所有文件
     *
     * @param filePath 目录
     * @return 文件列表
     * @throws PortableException 若不是目录则抛出
     */
    List<File> getDirectoryFile(String filePath);

    /**
     * 移动文件
     *
     * @param from 来自
     * @param to   改为
     * @return 是否成功
     */
    Boolean moveFile(String from, String to);
}
