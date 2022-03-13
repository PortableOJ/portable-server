package com.portable.server.support.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.kit.FileKit;
import com.portable.server.support.CaptchaSupport;
import com.portable.server.util.ImageUtils;
import com.portable.server.util.StreamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Component
public class CaptchaSupportImpl implements CaptchaSupport {

    private static final String CAPTCHA_PATH = "captcha";

    private static final String CAPTCHA_TMP_NAME = "__TMP";

    @Resource
    private FileKit fileKit;

    @Value("${portable.captcha.size}")
    private Integer maxCaptchaSize;

    @Value("${portable.captcha.init.size}")
    private Integer initCaptchaSize;

    /**
     * 缓存的验证码
     */
    private List<String> cacheCaptcha;

    /**
     * 当前使用到了第几张验证码
     */
    private Integer curPoint;

    /**
     * 最大验证码位置
     */
    private Integer maxPoint;

    /**
     * 下一次更新到验证码位置
     */
    private Integer updateCaptcha;

    @PostConstruct
    public void init() throws PortableException {
        fileKit.createDirIfNotExist(CAPTCHA_PATH);
        List<File> captchaHistoryList = fileKit.getDirectoryFile(CAPTCHA_PATH);
        cacheCaptcha = captchaHistoryList.stream()
                .map(File::getName)
                .collect(Collectors.toList());
        curPoint = 0;
        maxPoint = cacheCaptcha.size();
        updateCaptcha = cacheCaptcha.size() % maxCaptchaSize;
        for (int i = cacheCaptcha.size(); i < initCaptchaSize; i++) {
            reduceImage();
        }
    }

    @Override
    public String getCaptcha(OutputStream outputStream) throws PortableException {
        Integer pos;
        synchronized (this) {
            pos = curPoint;
            curPoint++;
            if (Objects.equals(curPoint, maxPoint)) {
                curPoint = 0;
            }
        }
        if (Objects.equals(pos, updateCaptcha)) {
            // 这张图片可能将会被更新，存在隐患，直接选下一张
            pos++;
            if (Objects.equals(pos, maxPoint)) {
                pos = 0;
            }
        }
        String name = cacheCaptcha.get(pos);
        try {
            InputStream inputStream = fileKit.getFileInput(getCaptchaPath(name));
            StreamUtils.copy(inputStream, outputStream);
        } catch (PortableException ignore) {
            throw PortableException.of("S-08-001");
        }
        return name;
    }

    @Scheduled(fixedDelayString = "${portable.captcha.update}")
    public void reduceImage() throws PortableException {
        OutputStream outputStream = fileKit.saveFileOrOverwrite(getCaptchaPath(CAPTCHA_TMP_NAME));
        String name = ImageUtils.createCaptcha(outputStream);
        if (!fileKit.moveFile(getCaptchaPath(CAPTCHA_TMP_NAME), getCaptchaPath(name))) {
            // 失败说明有两张相同验证码值的图片存在，那么就让他们相同吧
            fileKit.deleteFileIfExist(getCaptchaPath(CAPTCHA_TMP_NAME));
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw PortableException.of("S-01-003");
        }
        if (!Objects.equals(maxPoint, maxCaptchaSize)) {
            cacheCaptcha.add(name);
            maxPoint++;
        } else {
            String lastName = cacheCaptcha.get(updateCaptcha);
            cacheCaptcha.set(updateCaptcha, name);
            fileKit.deleteFileIfExist(getCaptchaPath(lastName));
            updateCaptcha++;
            if (Objects.equals(updateCaptcha, maxCaptchaSize)) {
                updateCaptcha = 0;
            }
        }
    }

    private String getCaptchaPath(String name) {
        return String.format("%s%s%s", CAPTCHA_PATH, File.separator, name);
    }
}
