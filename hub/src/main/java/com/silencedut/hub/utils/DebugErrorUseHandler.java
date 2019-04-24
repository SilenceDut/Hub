package com.silencedut.hub.utils;

/**
 * @author SilenceDut
 * @date 2019/4/24
 */
public class DebugErrorUseHandler extends ReleaseErrorUseHandler {
    @Override
    public void errorUseHub(String msg,String throwableStack) {
        super.errorUseHub(msg,throwableStack);
        throw new ErrorUseHandleException(msg);
    }
}
