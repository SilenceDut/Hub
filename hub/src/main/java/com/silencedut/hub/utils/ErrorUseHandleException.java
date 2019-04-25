package com.silencedut.hub.utils;

/**
 * @author SilenceDut
 * @date 2019/4/24
 */
public class ErrorUseHandleException extends RuntimeException{

    public ErrorUseHandleException(String msg){
        super(msg);
    }

    public ErrorUseHandleException(Throwable throwable) {
        super(throwable);
    }

    public ErrorUseHandleException(String msg,String throwableStack){
        super(msg,new Throwable(throwableStack));
    }

    public static String traceToString(int startIndex,Object[] stackArray) {
        if (stackArray == null) {
            return "null";
        }

        int iMax = stackArray.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = startIndex; ; i++) {
            b.append(String.valueOf(stackArray[i]));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append("\n");
        }
    }
}
