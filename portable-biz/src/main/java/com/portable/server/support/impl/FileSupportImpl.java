package com.portable.server.support.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.kit.FileKit;
import com.portable.server.support.FileSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;

/**
 * @author shiroha
 */
@Component
public class FileSupportImpl implements FileSupport {

    private static final String PROBLEM_DIR_NAME = "problem";

    @Resource
    private FileKit fileKit;

    @PostConstruct
    private void init() throws PortableException {
        fileKit.createDirIfNotExist(PROBLEM_DIR_NAME);
    }

    @Override
    public void createProblem(Long problemId) throws PortableException {
        String newProblemDir = PROBLEM_DIR_NAME + File.separator + problemId;
        fileKit.createDirIfNotExist(newProblemDir);
    }

    @Override
    public InputStream getTestInput(Long problemId, String testName) throws PortableException {
        return fileKit.getFileInput(getProblemInput(problemId, testName));
    }

    @Override
    public InputStream getTestOutput(Long problemId, String testName) throws PortableException {
        return fileKit.getFileInput(getProblemOutput(problemId, testName));
    }

    @Override
    public void saveTestInput(Long problemId, String testName, InputStream inputStream) throws PortableException {
        fileKit.saveFileOrOverwrite(getProblemInput(problemId, testName), inputStream);
    }

    @Override
    public void saveTestOutput(Long problemId, String testName, InputStream inputStream) throws PortableException {
        fileKit.saveFileOrOverwrite(getProblemOutput(problemId, testName), inputStream);
    }

    @Override
    public void createTestOutput(Long problemId, String testName, byte[] value) throws PortableException {
        fileKit.saveFileOrOverwrite(getProblemOutput(problemId, testName), value);
    }

    @Override
    public void appendTestOutput(Long problemId, String testName, byte[] value) throws PortableException {
        fileKit.appendFile(getProblemOutput(problemId, testName), value);
    }

    @Override
    public void removeTest(Long problemId, String testName) throws PortableException {
        fileKit.deleteFileIfExist(getProblemInput(problemId, testName));
        fileKit.deleteFileIfExist(getProblemOutput(problemId, testName));
    }

    private String getProblemInput(Long problemId, String testName) {
        return String.format("%s%s%d%s%s.in", PROBLEM_DIR_NAME, File.separator, problemId, File.separator, testName);
    }

    private String getProblemOutput(Long problemId, String testName) {
        return String.format("%s%s%d%s%s.out", PROBLEM_DIR_NAME, File.separator, problemId, File.separator, testName);
    }
}
