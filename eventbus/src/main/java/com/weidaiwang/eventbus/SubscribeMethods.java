package com.weidaiwang.eventbus;

import java.lang.reflect.Method;

/**
 * Created by zk on 2018/3/9.
 */

public class SubscribeMethods {

    private Method method;

    private ThreadMode threadMode;

    private Class<?> eventType;

    public SubscribeMethods(Method method, ThreadMode threadMode, Class<?> parameterType) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = parameterType;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public Method getMethod() {
        return method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }
}
