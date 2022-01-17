package com.portable.server.socket;

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
import java.util.*;

/**
 * @author shiroha
 */
@Slf4j
@Component
public class EpollManager {

    private Map<String, MethodDescribe> registerMethod;
    private List<MethodDescribe> closeMethod;
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
        registerMethod = new HashMap<>(0);
        closeMethod = new ArrayList<>();
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

            methodDescribe.setBean(target);
            methodDescribe.setMethod(method);

            if (epollMethod.close()) {
                closeMethod.add(methodDescribe);
                continue;
            } else {
                registerMethod.put(epollMethod.value().isEmpty() ? method.getName() : epollMethod.value(), methodDescribe);
            }

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
        Map<String, List<Byte>> paramMap = new HashMap<>(methodDescribe.getParams().size());
        byte[] buffer = data.getBytes(StandardCharsets.UTF_8);
        int pos = 0;
        while (pos < buffer.length) {
            keyBuilder.setLength(0);
            pos = readKey(buffer, pos, keyBuilder);
            String key = keyBuilder.toString();
            if (!methodDescribe.getParams().containsKey(key)) {
                log.error("Unknown key: {}", key);
            }
            MethodDescribe.ParamType paramType = methodDescribe.getParams().get(key);
            if (paramType == null) {
                log.error("Illegal key key: {}", key);
                continue;
            }
            switch (paramType.getDataType()) {
                case SIMPLE:
                    pos = readSimpleValue(buffer, pos, key, paramMap);
                    break;
                case COMPLEX:
                    pos = readComplexValue(buffer, pos, key, paramMap);
                    break;
                case DEFAULT:
                default:
                    log.error("Illegal key type, key: {}, type: {}", key, paramType.getDataType());
                    break;
            }
        }
        Object[] params = new Object[methodDescribe.getParams().size()];
        methodDescribe.getParams().forEach((key, paramType) -> {
            if (paramMap.containsKey(key)) {
                List<Byte> byteList = paramMap.get(key);
                if (byte[].class.equals(paramType.getType())) {
                    byte[] param = new byte[byteList.size()];
                    for (int i = 0, byteListSize = byteList.size(); i < byteListSize; i++) {
                        param[i] = byteList.get(i);
                    }
                    params[paramType.getPosition()] = param;
                } else if (Integer.class.equals(paramType.getType())) {
                    int res = 0;
                    for (Byte aByte : byteList) {
                        res *= 10;
                        res += aByte - '0';
                    }
                    params[paramType.getPosition()] = res;
                } else if (String.class.equals(paramType.getType())) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Byte aByte : byteList) {
                        stringBuilder.append((char) aByte.byteValue());
                    }
                    params[paramType.getPosition()] = stringBuilder.toString();
                } else if (paramType.getType().isEnum()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Byte aByte : byteList) {
                        stringBuilder.append((char) aByte.byteValue());
                    }
                    try {
                        Method valueOf = paramType.getType().getMethod("valueOf", String.class);
                        params[paramType.getPosition()] = valueOf.invoke(null, stringBuilder.toString());
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
            if (methodDescribe.getMethod().getReturnType().equals(Void.TYPE)) {
                methodDescribe.getMethod().invoke(methodDescribe.getBean(), params);
                return "";
            } else {
                return methodDescribe.getMethod().invoke(methodDescribe.getBean(), params);
            }
        } catch (Exception e) {
            log.error("Fail invoke, method: {}, data: {}, exception: {}", method, data, e.getMessage());
        } finally {
            ADDRESS_THREAD_LOCAL.remove();
        }
        return null;
    }

    public void close(String address) {
        ADDRESS_THREAD_LOCAL.set(address);
        for (MethodDescribe methodDescribe : closeMethod) {
            try {
                methodDescribe.getMethod().invoke(methodDescribe.getBean());
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ADDRESS_THREAD_LOCAL.remove();
    }

    private Integer readKey(byte[] buffer, Integer pos, StringBuilder key) {
        while (buffer[pos] != Constant.SPACE_BYTE) {
            key.append((char) buffer[pos++]);
        }
        return ++pos;
    }

    private Integer readSimpleValue(byte[] buffer, Integer pos, String key, Map<String, List<Byte>> paramMap) {
        List<Byte> value = new ArrayList<>();
        while (pos < buffer.length && buffer[pos] != Constant.RETURN_BYTE) {
            value.add(buffer[pos++]);
        }
        paramMap.put(key, value);
        return ++pos;
    }

    private Integer readComplexValue(byte[] buffer, Integer pos, String key, Map<String, List<Byte>> paramMap) {
        int len = 0;
        while (buffer[pos] != Constant.RETURN_BYTE) {
            len *= 10;
            len += buffer[pos++] - '0';
        }
        List<Byte> value = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            value.add(buffer[pos++]);
        }
        paramMap.put(key, value);
        return ++pos;
    }
}
