package com.portable.server.service.impl;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.portable.server.exception.PortableErrors;
import com.portable.server.internal.CaptchaInternalService;
import com.portable.server.service.CommonService;
import com.portable.server.util.DateTimeUtils;

import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class CommonServiceImpl implements CommonService {

    private static final Integer LONG_BYTE_SIZE = 8;

    private static final String TYPE_PACKAGE_PATH = "com.portable.server.type.";

    /**
     * 当前启动的版本号
     */
    private String version;

    private Map<String, Map<String, Map<String, Object>>> enumDescMap;

    @Resource
    private CaptchaInternalService captchaInternalService;

    @PostConstruct
    public void init() {
        byte[] timeByte = new byte[LONG_BYTE_SIZE];
        long time = DateTimeUtils.formatLong(new Date());
        for (int i = 0; i < LONG_BYTE_SIZE; i++) {
            timeByte[i] = (byte) (time & 0xff);
            time >>= 4;
        }
        version = Base64.getEncoder().encodeToString(timeByte);

        enumDescMap = new HashMap<>(10);
    }

    @Override
    public String getVersionName() {
        return version;
    }

    @Override
    public Map<String, Map<String, Object>> getEnumDesc(String name) {
        if (enumDescMap.containsKey(name)) {
            return enumDescMap.get(name);
        }
        try {
            Class<?> clazz = Class.forName(TYPE_PACKAGE_PATH + name);
            if (clazz.isEnum()) {
                Method values = clazz.getMethod("values");
                Object invokeValue = values.invoke(null);
                if (invokeValue instanceof Object[]) {
                    Object[] enums = (Object[]) invokeValue;
                    Field[] fields = clazz.getDeclaredFields();
                    Map<String, Map<String, Object>> result = new HashMap<>(enums.length);

                    for (Field field : fields) {
                        field.setAccessible(true);
                    }

                    for (Object e : enums) {
                        Map<String, Object> enumValue = new HashMap<>(fields.length);
                        for (Field field : fields) {
                            if (field.isEnumConstant() || field.isSynthetic()) {
                                continue;
                            }
                            Object o = field.get(e);
                            enumValue.put(field.getName(), o);
                        }
                        result.put(e.toString(), enumValue);
                    }

                    for (Field field : fields) {
                        field.setAccessible(false);
                    }

                    enumDescMap.put(name, result);

                    return result;
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignore) {
        }
        throw PortableErrors.of("A-06-001", name);
    }

    @Override
    public String getCaptcha(OutputStream outputStream) {
        return captchaInternalService.getCaptcha(outputStream);
    }
}
