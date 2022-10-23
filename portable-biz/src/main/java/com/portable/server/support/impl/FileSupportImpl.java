package com.portable.server.support.impl;

import java.io.File;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.portable.server.helper.FileHelper;
import com.portable.server.support.FileSupport;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class FileSupportImpl implements FileSupport {

    private static final String PROBLEM_DIR_NAME = "problem";

    @Resource
    private FileHelper fileHelper;

    @PostConstruct
    public void init() {
        fileHelper.createDirIfNotExist(PROBLEM_DIR_NAME);
    }

    @Override
    public void createProblem(Long problemId) {
        String newProblemDir = PROBLEM_DIR_NAME + File.separator + problemId;
        fileHelper.createDirIfNotExist(newProblemDir);
    }

    @Override
    public InputStream getTestInput(Long problemId, String testName) {
        return fileHelper.getFileInput(getProblemInput(problemId, testName));
    }

    @Override
    public InputStream getTestOutput(Long problemId, String testName) {
        return fileHelper.getFileInput(getProblemOutput(problemId, testName));
    }

    @Override
    public void saveTestInput(Long problemId, String testName, InputStream inputStream) {
        fileHelper.saveFileOrOverwrite(getProblemInput(problemId, testName), inputStream);
    }

    @Override
    public void saveTestOutput(Long problemId, String testName, InputStream inputStream) {
        fileHelper.saveFileOrOverwrite(getProblemOutput(problemId, testName), inputStream);
    }

    @Override
    public void createTestOutput(Long problemId, String testName, byte[] value) {
        fileHelper.saveFileOrOverwrite(getProblemOutput(problemId, testName), value);
    }

    @Override
    public void appendTestOutput(Long problemId, String testName, byte[] value) {
        fileHelper.appendFile(getProblemOutput(problemId, testName), value);
    }

    @Override
    public void removeTest(Long problemId, String testName) {
        fileHelper.deleteFileIfExist(getProblemInput(problemId, testName));
        fileHelper.deleteFileIfExist(getProblemOutput(problemId, testName));
    }

    private String getProblemInput(Long problemId, String testName) {
        return String.format("%s%s%d%s%s.in", PROBLEM_DIR_NAME, File.separator, problemId, File.separator, testName);
    }

    private String getProblemOutput(Long problemId, String testName) {
        return String.format("%s%s%d%s%s.out", PROBLEM_DIR_NAME, File.separator, problemId, File.separator, testName);
    }
}
