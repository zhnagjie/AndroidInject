package com.jwzhangjie.androidinject.view.annotation.event;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(
        listenerType = View.OnClickListener.class,
        listenerSetter = "setOnClickListener",
        methodName = "onClick")
public @interface OnClick {
    int[] value();

    int[] parentId() default 0;
}
