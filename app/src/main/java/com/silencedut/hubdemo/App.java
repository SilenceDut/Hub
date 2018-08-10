package com.silencedut.hubdemo;

import android.app.Application;

import com.silencedut.hub.Hub;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Hub.init(this);
    }
}
