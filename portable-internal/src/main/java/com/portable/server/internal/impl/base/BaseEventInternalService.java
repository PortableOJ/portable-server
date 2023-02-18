package com.portable.server.internal.impl.base;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;

/**
 * @author shiroha
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseEventInternalService extends AbstractIdleService {

    /**
     * 是否当前类已经在监听了
     */
    private Boolean onListen;

    /**
     * 消息总线
     */
    private EventBus eventBus;

    public BaseEventInternalService register(EventBus eventBus) {
        this.eventBus = eventBus;
        this.onListen = false;
        return this;
    }

    public BaseEventInternalService registerWithListen(EventBus eventBus) {
        BaseEventInternalService service = this.register(eventBus);
        this.listen();
        return service;
    }

    public BaseEventInternalService registerWithListen(EventBus eventBus, Object... objects) {
        BaseEventInternalService service = this.register(eventBus);
        for (Object o : objects) {
            this.listen(o);
        }
        return service;
    }

    protected synchronized void listen() {
        if (this.onListen) {
            return;
        }
        this.onListen = true;
        eventBus.register(this);
    }

    protected void listen(Object object) {
        eventBus.register(object);
    }

    protected EventBus getEventBus() {
        return eventBus;
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(this);
    }
}
