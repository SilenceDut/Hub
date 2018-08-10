package com.silencedut.hub;

import android.app.Application;

import com.silencedut.hub.navigation.activity.ActivityHub;
import com.silencedut.hub.navigation.activity.Params;
import com.silencedut.hub.navigation.impl.ImplHub;

/**
 * a library which can avoid check null when want to invoke a implementation by interface
 *
 *
 * @author SilenceDut
 * @date 2018/1/3
 */

public class Hub {

    public static final String PACKAGER_SEPARATOR = ".";
    public static final String CLASS_METHOD_SEPARATOR = "$";
    private Hub() {
    }

    public static void init(Application application) {
        ActivityHub.init(application);
    }

    public static  <T extends IHub> T getImpl(Class<T> iHub) {
        return ImplHub.getImpl(iHub);
    }

    public static  <T extends IHubPointer> Params<T> getActivity(Class<T> iHubPointer) {
        return ActivityHub.buildActivity(iHubPointer);
    }

    public static  <T extends IHub> boolean implExist(Class<T> iHub) {
        return ImplHub.implExist(iHub);
    }

    public static synchronized void removeImpl(Class<? extends IHub> iHub) {
         ImplHub.removeImpl(iHub);
    }

}
