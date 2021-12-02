package com.portable.server.support;

import com.portable.server.exception.PortableException;
import com.portable.server.type.LanguageType;

import java.io.InputStream;

public interface FileSupport {

    /// region problem

    void createProblem(Long problemId) throws PortableException;

    InputStream getTestInput(Long problemId, String testName) throws PortableException;

    InputStream getTestOutput(Long problemId, String testName) throws PortableException;

    void saveTestInput(Long problemId, String testName, InputStream inputStream) throws PortableException;

    void saveTestOutput(Long problemId, String testName, InputStream inputStream) throws PortableException;

    void removeTest(Long problemId, String testName) throws PortableException;

    /// endregion
}
