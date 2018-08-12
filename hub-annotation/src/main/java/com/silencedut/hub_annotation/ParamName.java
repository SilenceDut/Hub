package com.silencedut.hub_annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author SilenceDut
 * @date 2018/8/10
 * mark as a parameter ， and if value is default use original parameter name
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD,PARAMETER})
public @interface ParamName {
}
