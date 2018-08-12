package com.silencedut.hub_annotation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * @author SilenceDut
 */
public class TypeHelper<T> {
    Type tClass = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public Type getType() {
        return tClass;
    }
}