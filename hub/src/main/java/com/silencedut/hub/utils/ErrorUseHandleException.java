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
}
