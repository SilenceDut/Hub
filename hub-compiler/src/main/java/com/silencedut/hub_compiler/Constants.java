package com.silencedut.hub_compiler;

/**
 * Created by SilenceDut on 2017/1/18 .
 */

class Constants {

    // Java type
    private static final String LANG = "java.lang";
    static final String BYTE = LANG + ".Byte";
    static final String SHORT = LANG + ".Short";
    static final String INTEGER = LANG + ".Integer";
    static final String LONG = LANG + ".Long";
    static final String FLOAT = LANG + ".Float";
    static final String DOUBEL = LANG + ".Double";
    static final String BOOLEAN = LANG + ".Boolean";
    static final String STRING = LANG + ".String";

    //method name
    final static  String METHOD_GETAPIS = "getApis";
    final static  String METHOD_GETAPIField = "sameImplClass";
    final static  String Filed_ParamName = "paramNames";
    final static  String METHOD_GETActivityField = "targetActivity";
    final static  String METHOD_INJECT = "inject";

    //file name feature
    final static  String ACTIVITY_HELPER_SUFFIX = "HubActivityHelper";
    final static  String IMPL_HELPER_SUFFIX = "ImplHelper";
    final static  String CLASS_NAME_SEPARATOR = "_";

}