package com.silencedut.hub.navigation.impl;

import android.util.Log;

import com.silencedut.hub.Hub;
import com.silencedut.hub.IHub;
import com.silencedut.hub_annotation.IFindImplClz;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public class ImplHub {

    private enum HubMonitor {
        /**
         * hub class对象公用锁，避免直接锁class可能造成的死锁。length 必须为 2的n次方，用位运算代替求余数
         */
        ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN
    }


    private static final String TAG = "ImplHub";
    private static final String IMPL_HELPER_SUFFIX = "ImplHelper";
    private static Map<Class,IHub> sRealImpls = new ConcurrentHashMap<>();

    /**
     * 一个接口只能对应一个实现，之前的实现会被替换掉
     * @param impl 实现IHub接口的对象
     */
    private static void putImpl(Class api , IHub impl) {
        if (impl == null) {
            return;
        }

        sRealImpls.put(api,impl);

    }


    public static <T extends IHub> T getImpl(Class<T> iHub) {

        if (!iHub.isInterface()) {
            Log.e(TAG, String.format("interfaceType must be a interface , %s is not a interface", iHub.getName()));
        }

        int monitorIndex = iHub.hashCode() & (HubMonitor.values().length -1);
        IHub realImpl;
        long monitorStartTime = System.currentTimeMillis();
        Log.i(TAG, String.format("getImpl before monitor %s , monitor %d ", iHub.getName(),monitorIndex));
        synchronized (HubMonitor.values()[monitorIndex]) {
            try {
                long startTime = System.currentTimeMillis();
                Log.i(TAG, String.format("newImpl  %s ", iHub.getName()));
                realImpl = sRealImpls.get(iHub);
                if (realImpl == null) {

                    String apiCanonicalName = iHub.getCanonicalName();

                    String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));

                    String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR) + 1, apiCanonicalName.length());

                    String implCanonicalName = packageName + Hub.PACKAGER_SEPARATOR + apiName + Hub.CLASS_NAME_SEPARATOR + IMPL_HELPER_SUFFIX;

                    IFindImplClz iFindImplClzHelper = (IFindImplClz) Class.forName(implCanonicalName).newInstance();

                    realImpl = (IHub) iFindImplClzHelper.newImplInstance();

                    for (String apiClassName : iFindImplClzHelper.getApis()) {
                        putImpl(Class.forName(apiClassName), realImpl);
                    }

                    realImpl.onCreate();
                    Log.i(TAG, String.format("newImpl %s, cost time  %s ", iHub.getName(),System.currentTimeMillis() - startTime));
                }
            } catch (Throwable throwable) {

                ImplHandler implHandler = new ImplHandler(iHub);
                realImpl = sRealImpls.get(iHub);
                if (realImpl == null) {
                    realImpl = (IHub) implHandler.mImplProxy;
                    sRealImpls.put (iHub,realImpl);
                    Log.e(TAG, "find impl " + iHub.getSimpleName() + " error " + ", using proxy", throwable);
                } else {
                    Log.e(TAG, "impl %s" + iHub.getSimpleName() + " exit but onCreate error , using impl", throwable);
                }
            }
        }
        Log.i(TAG, String.format("getImpl %s, result cost  %s ", iHub.getName(),System.currentTimeMillis() - monitorStartTime));
        return (T) realImpl;
    }

    public static  <T extends IHub> boolean implExist(Class<T> iHub) {
        boolean implExist = sRealImpls.containsKey(iHub);

        if(implExist) {
            return true;
        }

        try {
            String apiCanonicalName = iHub.getCanonicalName();

            String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));
            String apiName =  apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR)+1, apiCanonicalName.length());

            String implCanonicalName = packageName+Hub.PACKAGER_SEPARATOR+apiName+"_ImplHelper";
            IFindImplClz iFindImplClzHelper = (IFindImplClz) Class.forName(implCanonicalName).newInstance();

            return iFindImplClzHelper!=null;

        }catch (Exception e) {
            return false;
        }
    }

    public static  void removeImpl(Class<? extends IHub> api) {
        if (api == null) {
            return;
        }
        sRealImpls.remove(api);
    }
}
