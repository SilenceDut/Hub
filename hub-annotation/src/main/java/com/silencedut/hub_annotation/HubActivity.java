package com.silencedut.hub_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HubActivity {
    Class activityApi();
    String methodName();
}
