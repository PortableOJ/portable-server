package com.portable.server.support;

import com.portable.server.exception.PortableException;
import com.portable.server.type.LanguageType;

import java.io.InputStream;

/**
 * @author shiroha
 */
public interface FileSupport {

    /// region problem

    /**
     * 创建问题的目录
     * @param problemId 问题 ID
     * @throws PortableException 出现问题则抛出错误
     */
    void createProblem(Long problemId) throws PortableException;

    /**
     * 获取测试输入的文件流
     * @param problemId 题目 ID
     * @param testName 测试数据名称
     * @return 测试数据输入流
     * @throws PortableException 出现问题则抛出错误
     */
    InputStream getTestInput(Long problemId, String testName) throws PortableException;

    /**
     * 获取测试输出的文件流
     * @param problemId 题目 ID
     * @param testName 测试数据名称
     * @return 测试数据输出流
     * @throws PortableException 出现问题则抛出错误
     */
    InputStream getTestOutput(Long problemId, String testName) throws PortableException;

    /**
     * 保存测试输入文件流
     * @param problemId 题目 ID
     * @param testName 测试数据名称
     * @param inputStream 输入流
     * @throws PortableException 出现问题则抛出错误
     */
    void saveTestInput(Long problemId, String testName, InputStream inputStream) throws PortableException;

    /**
     * 保存测试输出文件流
     * @param problemId 题目 ID
     * @param testName 测试数据名称
     * @param inputStream 输入流
     * @throws PortableException 出现问题则抛出错误
     */
    void saveTestOutput(Long problemId, String testName, InputStream inputStream) throws PortableException;

    /**
     * 创建一个新的测试输出文件
     * @param problemId 题目 ID
     * @param testName 测试数据名称
     * @param value 开头的字符串
     * @throws PortableException 出现问题则抛出错误
     */
    void createTestOutput(Long problemId, String testName, String value) throws PortableException;

    /**
     * 创建一个新的测试输出文件
     * @param problemId 题目 ID
     * @param testName 测试数据名称
     * @param value 开头的字符串
     * @throws PortableException 出现问题则抛出错误
     */
    void appendTestOutput(Long problemId, String testName, String value) throws PortableException;

    /**
     * 删除测试文件，包括输入输出
     * @param problemId 题目 ID
     * @param testName 测试数据名称
     * @throws PortableException 出现问题则抛出错误
     */
    void removeTest(Long problemId, String testName) throws PortableException;

    /// endregion
}
