package com.silencedut.hub.utils;

import com.silencedut.hub.IHub;

/**
 * @author SilenceDut
 * @date 2019/4/24
 */
public interface ErrorUseHandler {
    void errorUseHub(String msg,String throwableStack);
}
