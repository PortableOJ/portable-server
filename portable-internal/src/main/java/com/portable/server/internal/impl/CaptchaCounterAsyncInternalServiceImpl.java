package com.portable.server.internal.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.portable.server.exception.PortableErrors;
import com.portable.server.exception.PortableRuntimeException;
import com.portable.server.internal.CaptchaInternalService;
import com.portable.server.internal.impl.base.BaseCounterAsyncInternalService;
import com.portable.server.struct.PartitionHelper;
import com.portable.server.time.Interval;
import com.portable.server.util.ImageUtils;
import com.portable.server.util.StreamUtils;

/**
 * @author shiroha
 */
public class CaptchaCounterAsyncInternalServiceImpl extends BaseCounterAsyncInternalService implements CaptchaInternalService {

    private static final String CAPTCHA_TMP_NAME = "__TMP";

    @Resource(name = "captchaPartitionHelper")
    private PartitionHelper captchaPartitionHelper;

    private static final Integer MAX_CAPTCHA_SIZE = 100;

    private static final Integer INIT_CAPTCHA_SIZE = 10;

    private static final Integer UPDATE_COUNT = 10;

    private static final Long minInterval = 600000L;

    private static final Long maxInterval = 6000000L;

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

    public CaptchaCounterAsyncInternalServiceImpl() {
        super(UPDATE_COUNT, Interval.ofMillisecond(minInterval), Interval.ofMillisecond(maxInterval));
    }

    @Override
    protected void startUp() throws Exception {
        List<File> captchaHistoryList = captchaPartitionHelper.getDirectoryFile();
        cacheCaptcha = captchaHistoryList.stream()
                .map(File::getName)
                .collect(Collectors.toList());
        curPoint = 0;
        maxPoint = cacheCaptcha.size();
        updateCaptcha = cacheCaptcha.size() % MAX_CAPTCHA_SIZE;
        for (int i = cacheCaptcha.size(); i < INIT_CAPTCHA_SIZE; i++) {
            reduceImage();
        }
    }

    @Override
    public String getCaptcha(OutputStream outputStream) {
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
            InputStream inputStream = captchaPartitionHelper.getFileInput(name);
            StreamUtils.copy(inputStream, outputStream);
        } catch (PortableRuntimeException ignore) {
            throw PortableErrors.of("S-08-001");
        }

        this.count();
        return name;
    }

    @Override
    protected void trigger() {
        reduceImage();
    }

    private void reduceImage() {
        OutputStream outputStream = captchaPartitionHelper.saveFileOrOverwrite(CAPTCHA_TMP_NAME);
        String name = ImageUtils.createCaptcha(outputStream);
        if (!captchaPartitionHelper.moveFile(CAPTCHA_TMP_NAME, name)) {
            // 失败说明有两张相同验证码值的图片存在，那么就让他们相同吧
            captchaPartitionHelper.deleteFileIfExist(CAPTCHA_TMP_NAME);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw PortableErrors.of("S-01-003");
        }
        if (!Objects.equals(maxPoint, MAX_CAPTCHA_SIZE)) {
            cacheCaptcha.add(name);
            maxPoint++;
        } else {
            String lastName = cacheCaptcha.get(updateCaptcha);
            cacheCaptcha.set(updateCaptcha, name);
            captchaPartitionHelper.deleteFileIfExist(lastName);
            updateCaptcha++;
            if (Objects.equals(updateCaptcha, MAX_CAPTCHA_SIZE)) {
                updateCaptcha = 0;
            }
        }
    }
}
