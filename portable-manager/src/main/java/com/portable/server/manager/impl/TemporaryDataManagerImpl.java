package com.portable.server.manager.impl;

import com.portable.server.manager.TemporaryDataManager;
import com.portable.server.model.ServiceVerifyCode;
import com.portable.server.util.Switch;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author shiroha
 */
@Component
public class TemporaryDataManagerImpl implements TemporaryDataManager {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 保存在 redis 中的服务器密钥的 key 值
     */
    private static final String SERVICE_CODE_KEY = "SERVICE_CODE";

    @Override
    public ServiceVerifyCode getServiceCode() {
        BoundValueOperations<String, String> boundValueOperations = stringRedisTemplate.boundValueOps(SERVICE_CODE_KEY);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(SERVICE_CODE_KEY))) {
            String code = boundValueOperations.get();
            Long expire = boundValueOperations.getExpire();
            Calendar calendar = Calendar.getInstance();
            if (expire != null) {
                calendar.add(Calendar.SECOND, expire.intValue());
            }
            return ServiceVerifyCode.builder()
                    .code(code)
                    .temporary(true)
                    .endTime(calendar.getTime())
                    .build();
        }
        String code = UUID.randomUUID().toString();
        boundValueOperations.set(code, Switch.serverCodeExpireTime, TimeUnit.SECONDS);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, Switch.serverCodeExpireTime.intValue());
        return ServiceVerifyCode.builder()
                .code(code)
                .temporary(true)
                .endTime(calendar.getTime())
                .build();
    }
}
