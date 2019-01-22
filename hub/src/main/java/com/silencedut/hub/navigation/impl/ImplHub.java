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
    private static final String TAG = "ImplHub";
    private static final String IMPL_HELPER_SUFFIX = "ImplHelper";
    private static Map<Class,IHub> sRealImpls = new ConcurrentHashMap<>();

    /**
     * 一个接口只能对应一个实现，之前的实现会被替换掉
     * @param impl 实现IHub接口的对象
     */
    private static  void putImpl(Class api , IHub impl) {
        if (impl == null) {
            return;
        }

        sRealImpls.put(api,impl);
    }


    public static <T extends IHub> T getImpl( Class<T> iHub) {

        if (!iHub.isInterface()) {
            Log.e(TAG, String.format("interfaceType must be a interface , %s is not a interface", iHub.getName()));
        }

        IHub realImpl = sRealImpls.get(iHub);

        if (realImpl == null) {
            try {
                synchronized (iHub.getDeclaredClasses()) {

                    if (sRealImpls.get(iHub) == null) {
                        String apiCanonicalName = iHub.getCanonicalName();

                        String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));

                        String apiName =  apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR)+1, apiCanonicalName.length());

                        String implCanonicalName = packageName + Hub.PACKAGER_SEPARATOR + apiName + Hub.CLASS_NAME_SEPARATOR+IMPL_HELPER_SUFFIX;

                        IFindImplClz iFindImplClzHelper = (IFindImplClz) Class.forName(implCanonicalName).newInstance();

                        /*
                        暂时先只支持无参的构造函数
                         */

                        realImpl = (IHub) iFindImplClzHelper.newImplInstance();

                        for(String apiClassName : iFindImplClzHelper.getApis()) {
                            putImpl(Class.forName(apiClassName),realImpl);
                        }
                        realImpl.onCreate();
                    }
                }


            }catch (Exception e) {

                ImplHandler implHandler = new ImplHandler(iHub);
                if(realImpl == null) {
                    realImpl = (IHub) implHandler.mImplProxy;
                    Log.e(TAG,"find impl "+iHub.getSimpleName()+" error "+", using proxy", e);
                }else {
                    Log.e(TAG,"impl %s"+iHub.getSimpleName()+" exit but onCreate error , using impl",e);
                }
            }
        }

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
