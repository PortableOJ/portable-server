package com.portable.server.socket;

import com.portable.server.exception.PortableException;
import com.portable.server.socket.annotation.EpollMethod;
import com.portable.server.socket.annotation.EpollParam;
import com.portable.server.socket.model.MethodDescribe;
import com.portable.server.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shiroha
 */
@Slf4j
@Component
public class EpollManager {

    private Map<String, MethodDescribe> registerMethod;
    private static final Set<Class<?>> CLASS_SET;
    private static final ThreadLocal<String> ADDRESS_THREAD_LOCAL;

    private static Thread runner;

    @Resource
    private ApplicationContext applicationContext;

    static {
        CLASS_SET = new HashSet<>();
        ADDRESS_THREAD_LOCAL = new ThreadLocal<>();
    }

    public static String getAddress() {
        return ADDRESS_THREAD_LOCAL.get();
    }

    @PostConstruct
    public void init() {
        registerMethod = new HashMap<>(1);
        if (runner == null) {
            //noinspection AlibabaAvoidManuallyCreateThread
            runner = new Thread(() -> {
                try {
                    log.info("create socket");
                    EpollUtil.initEpollSocket(9090, this);
                } catch (Exception e) {
                    log.error("Socket start fail, {}", e.getMessage());
                }
            });
            runner.start();
        }
        clearClass();
    }

    public static void setUp(Class<?> clazz) {
        CLASS_SET.add(clazz);
    }

    public void clearClass() {
        if (CLASS_SET.isEmpty()) {
            return;
        }
        for (Class<?> clazz : CLASS_SET) {
            scan(clazz);
        }
        CLASS_SET.clear();
    }

    public void scan(Class<?> clazz) {
        Object target = applicationContext.getBean(clazz);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            EpollMethod epollMethod = method.getAnnotation(EpollMethod.class);
            if (epollMethod == null) {
                continue;
            }
            MethodDescribe methodDescribe = new MethodDescribe();

            registerMethod.put(method.getName(), methodDescribe);

            methodDescribe.setBean(target);
            methodDescribe.setMethod(method);

            Map<String, MethodDescribe.ParamType> paramsTypeList = new HashMap<>(method.getParameterCount());
            methodDescribe.setParams(paramsTypeList);

            Parameter[] parameters = method.getParameters();
            for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                Parameter parameter = parameters[i];
                EpollParam epollParam = parameter.getAnnotation(EpollParam.class);
                MethodDescribe.ParamType paramType = new MethodDescribe.ParamType();

                paramsTypeList.put(parameter.getName(), paramType);
                paramType.setName(parameter.getName());
                paramType.setType(parameter.getType());
                paramType.setPosition(i);
                if (epollParam != null) {
                    paramType.setDataType(epollParam.value());
                }
            }
        }
    }

    public Object call(String address, String method, String data) {
        clearClass();
        MethodDescribe methodDescribe = registerMethod.get(method);
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        Map<String, String> paramMap = new HashMap<>(methodDescribe.getParams().size());
        byte[] buffer = data.getBytes(StandardCharsets.UTF_8);
        int pos = 0;
        while (pos < buffer.length) {
            keyBuilder.setLength(0);
            valueBuilder.setLength(0);
            pos = readKey(buffer, pos, keyBuilder);
            String key = keyBuilder.toString();
            if (!methodDescribe.getParams().containsKey(key)) {
                log.error("Unknown key: {}", key);
            }
            MethodDescribe.ParamType paramType = methodDescribe.getParams().get(key);
            switch (paramType.getDataType()) {
                case SIMPLE:
                    pos = readSimpleValue(buffer, pos, valueBuilder);
                    break;
                case COMPLEX:
                    pos = readComplexValue(buffer, pos, valueBuilder);
                    break;
                case DEFAULT:
                default:
                    log.error("Illegal key type, key: {}, type: {}", key, paramType.getDataType());
                    break;
            }
            paramMap.put(key, valueBuilder.toString());
        }
        Object[] params = new Object[methodDescribe.getParams().size()];
        methodDescribe.getParams().forEach((key, paramType) -> {
            if (paramMap.containsKey(key)) {
                if (Integer.class.equals(paramType.getType())) {
                    params[paramType.getPosition()] = Integer.valueOf(paramMap.get(key));
                } else if (String.class.equals(paramType.getType())) {
                    params[paramType.getPosition()] = paramMap.get(key);
                } else if (paramType.getType().isEnum()) {
                    try {
                        Method valueOf = paramType.getType().getMethod("valueOf", String.class);
                        params[paramType.getPosition()] = valueOf.invoke(null, paramMap.get(key));
                    } catch (NoSuchMethodException e) {
                        log.error("Error Enum: {}", paramType.getType());
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        log.error("Error invoke valueOf: {}", paramType.getType());
                    }
                } else {
                    log.error("Unsupported type: {}", paramType.getType());
                    params[paramType.getPosition()] = null;
                }
            } else {
                log.error("Empty param: {}, type: {}", paramType.getName(), paramType.getType());
                params[paramType.getPosition()] = null;
            }
        });
        try {
            ADDRESS_THREAD_LOCAL.set(address);
            return methodDescribe.getMethod().invoke(methodDescribe.getBean(), params);
        } catch (Exception e) {
            log.error("Fail invoke, method: {}, data: {}, exception: {}", method, data, e.getMessage());
        } finally {
            ADDRESS_THREAD_LOCAL.remove();
        }
        return null;
    }

    private Integer readKey(byte[] buffer, Integer pos, StringBuilder key) {
        while (buffer[pos] != Constant.SPACE_BYTE) {
            key.append((char) buffer[pos++]);
        }
        return ++pos;
    }

    private Integer readSimpleValue(byte[] buffer, Integer pos, StringBuilder value) {
        while (buffer[pos] != Constant.RETURN_BYTE) {
            value.append((char) buffer[pos++]);
        }
        return ++pos;
    }

    private Integer readComplexValue(byte[] buffer, Integer pos, StringBuilder value) {
        int len = 0;
        while (buffer[pos] != Constant.RETURN_BYTE) {
            len *= 10;
            len += buffer[pos++] - '0';
        }
        for (int i = 0; i < len; i++) {
            value.append((char) buffer[pos++]);
        }
        return ++pos;
    }
}
