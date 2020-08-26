package com.laotang.quickdevcore.integration.manifest;

import android.content.pm.ActivityInfo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface Orientation {
    int value() default ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
}
