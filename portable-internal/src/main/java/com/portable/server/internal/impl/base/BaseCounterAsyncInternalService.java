package com.portable.server.internal.impl.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.portable.server.model.event.CounterAsyncInternalEvent;
import com.portable.server.thread.MultiTreadJumpLock;
import com.portable.server.time.Interval;

import com.google.common.eventbus.Subscribe;

/**
 * 基于数量统计的后台触发式任务，每隔至少 x 秒至多 y 秒内发生 t 次请求则发送某个消息
 *
 * @author shiroha
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseCounterAsyncInternalService extends BaseEventInternalService {

    /**
     * 全局服务 ID，用于区别不同服务
     */
    private static final AtomicInteger COUNTER_ASYNC_NEXT_ID;

    /**
     * 服务记录，在触发任务的时候通过此 map 来找服务
     */
    private static final Map<Integer, BaseCounterAsyncInternalService> BASE_COUNTER_ASYNC_INTERNAL_SERVICE_MAP;

    /**
     * 当前服务的 ID
     */
    private final Integer serviceId;

    /**
     * 上一次触发的时间，毫秒
     */
    private final AtomicLong lastEventTime;

    /**
     * 距离上一次触发之后发发生了多少次请求
     */
    private final AtomicLong accumulateCount;

    /**
     * 并发锁
     */
    private final MultiTreadJumpLock multiTreadJumpLock;

    /**
     * 最小触发间隔，必须要距离上一次触发完成经过这么长时间之后，且满足请求次数，则会触发
     */
    private final Long minIntervalMillisecond;

    /**
     * 最大触发间隔，到达这个间隔则下一次请求必定触发
     */
    private final Long maxIntervalMillisecond;

    /**
     * 触发次数要求
     */
    private final Integer triggerCount;

    static {
        COUNTER_ASYNC_NEXT_ID = new AtomicInteger(0);
        BASE_COUNTER_ASYNC_INTERNAL_SERVICE_MAP = new HashMap<>();
    }

    protected BaseCounterAsyncInternalService(Integer triggerCount, Interval minInterval, Interval maxInterval) {
        // 处理静态
        this.serviceId = COUNTER_ASYNC_NEXT_ID.getAndIncrement();
        BASE_COUNTER_ASYNC_INTERNAL_SERVICE_MAP.put(this.serviceId, this);

        // 处理入参
        this.minIntervalMillisecond = minInterval.toMillisecond();
        this.maxIntervalMillisecond = maxInterval.toMillisecond();
        this.triggerCount = triggerCount;

        // 创建默认值
        this.multiTreadJumpLock = new MultiTreadJumpLock();
        this.lastEventTime = new AtomicLong(System.currentTimeMillis());
        this.accumulateCount = new AtomicLong(0);
    }

    /**
     * 事件发生触发此函数
     */
    protected abstract void trigger();

    /**
     * 触发一次记录
     */
    protected void count() {
        accumulateCount.incrementAndGet();

        if (!multiTreadJumpLock.tryLock()) {
            return;
        }

        Long curTime = System.currentTimeMillis();
        Long lastEventTimestamp = lastEventTime.get();
        long nowInterval = curTime - lastEventTimestamp;
        if (nowInterval < minIntervalMillisecond) {
            return;
        }

        if (nowInterval < maxIntervalMillisecond && accumulateCount.get() >= triggerCount
                || nowInterval > maxIntervalMillisecond) {
            this.getEventBus().post(CounterAsyncInternalEvent.of(serviceId));
        }
    }

    /**
     * 当满足条件的时候触发的回调，实际上是通过消息得到的
     */
    private void eventCallback() {
        this.trigger();

        this.accumulateCount.set(0L);
        this.lastEventTime.set(System.currentTimeMillis());
        this.multiTreadJumpLock.close();
    }

    public static class CounterCallbackSubscribe {

        @Subscribe
        private void subscribe(CounterAsyncInternalEvent event) {
            BaseCounterAsyncInternalService service = BASE_COUNTER_ASYNC_INTERNAL_SERVICE_MAP.get(event.getServiceId());
            Optional.ofNullable(service).ifPresent(BaseCounterAsyncInternalService::eventCallback);
        }
    }
}
