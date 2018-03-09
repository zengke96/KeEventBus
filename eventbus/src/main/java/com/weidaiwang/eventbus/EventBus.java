package com.weidaiwang.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zk on 2018/3/9.
 */

public class EventBus {

    private static final EventBus instance = new EventBus();
    private Map<Object, List<SubscribeMethods>> cacheMap = new HashMap<>();
    private Handler handler;
    private ExecutorService executorService;

    public static EventBus getInstance() {
        return instance;
    }

    private EventBus() {
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    public void register(Object object) {
        List<SubscribeMethods> subscribeMethods = cacheMap.get(object);
        if (subscribeMethods == null) {
            subscribeMethods = getSubscribeMethods(object);
            cacheMap.put(object, subscribeMethods);
        }
    }

    private List<SubscribeMethods> getSubscribeMethods(Object object) {
        List<SubscribeMethods> list = new ArrayList<>();
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getMethods();
        while (clazz != null) {

            String name = clazz.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.")) {
                break;
            }
            //遍历所有的方法
            for (Method method : methods) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe == null) {
                    continue;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("eventbus 不能有多个参数的方法");
                }
                ThreadMode threadMode = subscribe.threadMode();
                SubscribeMethods subscribeMethods = new SubscribeMethods(method, threadMode, parameterTypes[0]);
                list.add(subscribeMethods);
            }


            //不断去寻找父类需要接收消息的方法
            clazz = clazz.getSuperclass();

        }
        return list;
    }

    public void unregister(Object object) {
        cacheMap.remove(object);
    }

    public void post(final Object message) {
        Set<Object> keySet = cacheMap.keySet();
        Iterator<Object> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            final Object activity = iterator.next();
            List<SubscribeMethods> list = cacheMap.get(activity);
            for (final SubscribeMethods method : list) {
                //判断发送的类型和接收的类型是否一致
                if (method.getEventType().isAssignableFrom(message.getClass())) {
                    switch (method.getThreadMode()) {
                        case PostThread:
                            invoke(method, activity, message);
                            break;
                        case MainThread:
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(method, activity, message);
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(method, activity, message);
                                    }
                                });
                            }
                            break;
                        case BackgroundThread:
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(method, activity, message);
                                    }
                                });
                            } else {
                                invoke(method, activity, message);
                            }
                            break;
                        default:

                            break;
                    }
                }
            }
        }
    }

    private void invoke(SubscribeMethods subscribeMethod, Object activity, Object message) {
        Method method = subscribeMethod.getMethod();
        try {
            method.invoke(activity, message);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
