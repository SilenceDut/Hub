package com.silencedut.hubdemo;

import com.silencedut.hub.IHubPointer;

/**
 * Created by SilenceDut on 2018/1/3 .
 */

public interface ITestApi extends IHubPointer {

    void activitySecond(String key1,int key2);
    void c();
}
