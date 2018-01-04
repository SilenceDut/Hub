package com.silencedut.hub;

/**
 * Created by SilenceDut on 2018/1/4 .
 */

public class NumberUtil {

    static boolean isPrimitiveType( Class<?> type) {
        return type == byte.class
                || type == int.class
                || type == short.class
                || type == float.class
                || type == double.class
                || type == long.class;
    }
}
