package com.silencedut.hub.navigation.activity;

import android.app.Application;

import com.silencedut.hub.IHubPointer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */
public class ActivityHub {

    private static Map<String,ActivityHandler> mActivityProxy = new ConcurrentHashMap<>();
    static Map<String,Class<?>> mActivityPath = new ConcurrentHashMap<>();
    static volatile Application sApplication;

    public static void init(Application application) {
        sApplication = application;
    }

    public static synchronized <T extends IHubPointer> Params<T> buildActivity(Class<T> iHubPointer){
        ActivityHandler activityHandler = mActivityProxy.get(iHubPointer.getClass().getCanonicalName());
        if(activityHandler == null) {
            activityHandler = new ActivityHandler(iHubPointer);
        }
        Params<T> params = new Params((T) activityHandler.mActivityProxy);
        activityHandler.setParams(params);

        ActivityHub.mActivityProxy.put(iHubPointer.getClass().getCanonicalName(),activityHandler);
        return params;
    }


}
