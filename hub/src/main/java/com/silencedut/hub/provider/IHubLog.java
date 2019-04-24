package com.silencedut.hub.provider;

/**
 * @author SilenceDut
 * @date 2019/3/26
 */
public interface IHubLog {
    void info(String tag,String info);
    void error(String tag, String msg,  Throwable... objs);

}
