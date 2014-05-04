package com.jwzhangjie.androidinject.view;

import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import com.jwzhangjie.androidinject.utils.LogUtils;
import com.jwzhangjie.androidinject.utils.core.DoubleKeyValueMap;
import com.jwzhangjie.androidinject.view.annotation.event.EventBase;

public class EventListenerManager {

    private EventListenerManager() {
    }

    /**
     * k1: viewInjectInfo
     * k2: interface Type
     * value: listener
     */
    private final static DoubleKeyValueMap<ViewInjectInfo, Class<?>, Object> listenerCache =
            new DoubleKeyValueMap<ViewInjectInfo, Class<?>, Object>();

    public static void addEventMethod(
            ViewFinder finder,
            ViewInjectInfo info,
            Annotation eventAnnotation,
            Object handler,
            Method method) {
        try {
            View view = finder.findViewByInfo(info);
            if (view != null) {
                EventBase eventBase = eventAnnotation.annotationType().getAnnotation(EventBase.class);
                Class<?> listenerType = eventBase.listenerType();
                String listenerSetter = eventBase.listenerSetter();
                String methodName = eventBase.methodName();

                boolean addNewMethod = false;
                Object listener = listenerCache.get(info, listenerType);
                DynamicHandler dynamicHandler = null;
                if (listener != null) {
                    dynamicHandler = (DynamicHandler) Proxy.getInvocationHandler(listener);
                    addNewMethod = handler.equals(dynamicHandler.getHandler());
                    if (addNewMethod) {
                        dynamicHandler.addMethod(methodName, method);
                    }
                }
                if (!addNewMethod) {
                    dynamicHandler = new DynamicHandler(handler);
                    dynamicHandler.addMethod(methodName, method);
                    listener = Proxy.newProxyInstance(
                            listenerType.getClassLoader(),
                            new Class<?>[]{listenerType},
                            dynamicHandler);

                    listenerCache.put(info, listenerType, listener);
                }

                Method setEventListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                setEventListenerMethod.invoke(view, listener);
            }
        } catch (Throwable e) {
            LogUtils.e(e.getMessage(), e);
        }
    }

    public static class DynamicHandler implements InvocationHandler {
        private WeakReference<Object> handlerRef;
        private final HashMap<String, Method> methodMap = new HashMap<String, Method>(1);

        public DynamicHandler(Object handler) {
            this.handlerRef = new WeakReference<Object>(handler);
        }

        public void addMethod(String name, Method method) {
            methodMap.put(name, method);
        }

        public Object getHandler() {
            return handlerRef.get();
        }

        public void setHandler(Object handler) {
            this.handlerRef = new WeakReference<Object>(handler);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object handler = handlerRef.get();
            if (handler != null) {
                String methodName = method.getName();
                method = methodMap.get(methodName);
                if (method != null) {
                    return method.invoke(handler, args);
                }
            }
            return null;
        }
    }
}
