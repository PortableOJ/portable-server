package com.portable.server.support.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.FileManager;
import com.portable.server.support.FileSupport;
import com.portable.server.type.LanguageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author shiroha
 */
@Component
public class FileSupportImpl implements FileSupport {

    @Value("${portable.home}")
    private String homeDir;

    private static final String PROBLEM_DIR = "problem";

    @Resource
    private FileManager fileManager;

    @PostConstruct
    private void init() throws PortableException {
        fileManager.createDirIfNotExist(homeDir);
        fileManager.createDirIfNotExist(homeDir + PROBLEM_DIR);
    }

    @Override
    public void createProblem(Long problemId) throws PortableException {

    }

    @Override
    public InputStream getTestInput(Long problemId, String testName) throws PortableException {
        return null;
    }

    @Override
    public InputStream getTestOutput(Long problemId, String testName) throws PortableException {
        return null;
    }

    @Override
    public void saveTestInput(Long problemId, String testName, InputStream inputStream) throws PortableException {

    }

    @Override
    public void saveTestOutput(Long problemId, String testName, InputStream inputStream) throws PortableException {

    }

    @Override
    public void removeTest(Long problemId, String testName) throws PortableException {

    }
}
