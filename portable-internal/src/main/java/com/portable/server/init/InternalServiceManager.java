package com.portable.server.init;

import java.util.Stack;

import com.portable.server.internal.impl.CaptchaCounterAsyncInternalServiceImpl;
import com.portable.server.internal.impl.base.BaseCounterAsyncInternalService;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

/**
 * @author shiroha
 */
@SuppressWarnings("UnstableApiUsage")
public class InternalServiceManager {

    private EventBus eventBus;

    private ServiceManager serviceManager;

    private Stack<Service> serviceStack;

    public void startInit() {
        eventBus = new EventBus();

        serviceManager = new ServiceManager(Lists.newArrayList(
                new CaptchaCounterAsyncInternalServiceImpl().registerWithListen(eventBus,
                        new BaseCounterAsyncInternalService.CounterCallbackSubscribe())
        ));

        serviceManager.startAsync();
    }

    public void awaitInit() {
        serviceManager.awaitHealthy();
    }

    public void startClose() {
        serviceManager.stopAsync();
    }

    public void awaitClose() {
        serviceManager.awaitStopped();
    }
}
