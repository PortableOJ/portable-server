package com.portable.server.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shiroha
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CounterAsyncInternalEvent extends BaseEvent {

    /**
     * 需要触发的服务 ID
     */
    private Integer serviceId;

    public static CounterAsyncInternalEvent of(Integer serviceId) {
        return new CounterAsyncInternalEvent(serviceId);
    }

    private CounterAsyncInternalEvent(Integer serviceId) {
        this.serviceId = serviceId;
    }
}
