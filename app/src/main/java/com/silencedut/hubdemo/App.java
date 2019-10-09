package com.silencedut.hubdemo;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
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
        long start = System.currentTimeMillis();
        ARouter.init(this);
        Log.d("App","cost:"+(System.currentTimeMillis()-start));
    }

    public static Application getInsatnce() {
        return sInstance;
    }
}
