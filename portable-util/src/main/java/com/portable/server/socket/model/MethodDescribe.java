package com.portable.server.socket.model;

import com.portable.server.socket.type.EpollDataType;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author shiroha
 */
@Data
public class MethodDescribe {

    private Object bean;

    private Method method;

    private Map<String, ParamType> params;

    @Data
    public static class ParamType {

        /**
         * 参数名
         */
        private String name;

        /**
         * 参数类型
         */
        private Class<?> type;

        /**
         * 参数位于第几位
         */
        private Integer position;

        /**
         * 参数所使用的描述类型
         */
        private EpollDataType dataType;

        public ParamType() {
            this.dataType = EpollDataType.DEFAULT;
        }
    }
}
