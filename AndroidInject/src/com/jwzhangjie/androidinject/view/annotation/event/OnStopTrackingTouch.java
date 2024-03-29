package com.jwzhangjie.androidinject.view.annotation.event;

import android.widget.SeekBar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(
        listenerType = SeekBar.OnSeekBarChangeListener.class,
        listenerSetter = "setOnSeekBarChangeListener",
        methodName = "onStopTrackingTouch")
public @interface OnStopTrackingTouch {
    int[] value();

    int[] parentId() default 0;
}
