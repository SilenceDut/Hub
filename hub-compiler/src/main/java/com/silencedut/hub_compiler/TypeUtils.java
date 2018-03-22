package com.silencedut.hub_compiler;

import com.squareup.javapoet.ClassName;

/**
 * Created by SilenceDut on 2017/1/18 .
 */

class TypeUtils {
    static final ClassName SET = ClassName.get("java.util", "Set");
    static final ClassName HASHSETCLS = ClassName.get("java.util", "HashSet");
    static final ClassName STRINGCLS = ClassName.get("java.lang", "String");
    static final String METHOD_GETAPIS = "getApis";
    static final String METHOD_GETAPIField = "sameImplClass";

}