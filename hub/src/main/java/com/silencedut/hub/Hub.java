package com.silencedut.hub;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a library which can avoid check null when want to invoke a implementation by interface
 *
 * Created by SilenceDut on 2018/1/3 .
 */

public class Hub {
    private static final String TAG = "Hub";

    private Map<Class<?>,IHub> mRealImpls = new ConcurrentHashMap<>();

    private static volatile Hub sInstance;

    private Hub() {

    }

    public static Hub getInstance() {
        if(sInstance ==null) {
            synchronized (Hub.class) {
                if(sInstance ==null) {
                    sInstance = new Hub();
                }
            }
        }
        return sInstance;
    }


    /**
     * 一个接口只能对应一个实现，之前的实现会被替换掉
     * @param impl 实现IHub接口的对象
     */
    public synchronized void putImpl(IHub impl) {
        if (impl == null) {
            return;
        }

        Class<?>[] interfaces = impl.getClass().getInterfaces();
        for(Class interfacePer : interfaces) {
            if(IHub.class.isAssignableFrom(interfacePer)) {
                mRealImpls.put(interfacePer,impl);
            }
        }
    }

    public synchronized void removeImpl(Class<? extends IHub> impl) {
        if (impl == null) {
            return;
        }
        mRealImpls.remove(impl);
    }

    public synchronized  <T extends IHub> T getImpl(Class<T> iHub) {

        if (!iHub.isInterface()) {
            Log.e(TAG, String.format("interfaceType must be a interface , %s is not a interface", iHub.getName()));
        }

        IHub realImpl = mRealImpls.get(iHub);

        if (realImpl == null) {
            ImplHandler implHandler = new ImplHandler(iHub);
            realImpl = (IHub) implHandler.mImplProxy;
        }

        return (T) realImpl;
    }

    public synchronized <T extends IHub> boolean implExist(Class<T> iHub) {
        return mRealImpls.containsKey(iHub);
    }
}
