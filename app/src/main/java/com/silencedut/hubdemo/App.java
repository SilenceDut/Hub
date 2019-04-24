package com.silencedut.hubdemo;

import android.app.Application;

import com.silencedut.hub.Hub;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public class App extends Application {
    private static App sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Hub.init(this);
    }

    public static Application getInsatnce() {
        return sInstance;
    }
}
