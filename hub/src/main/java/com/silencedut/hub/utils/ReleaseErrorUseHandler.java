package com.silencedut.hub.utils;

import com.silencedut.hub.Hub;
import com.silencedut.hub.IHub;

/**
 * @author SilenceDut
 * @date 2019/4/24
 */
public class ReleaseErrorUseHandler implements ErrorUseHandler {
    @Override
    public void errorUseHub(String msg,String throwableStack) {
        Hub.sHubConfig.getIHubLog().error("ReleaseErrorUseHandler",msg,new Throwable(throwableStack));
    }

}
