package com.silencedut.hub.navigation.impl;

import android.util.Log;

import com.silencedut.hub.Hub;
import com.silencedut.hub.IHub;
import com.silencedut.hub_annotation.IFindImplClz;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author SilenceDut
 * @date 2018/8/9
 */
public class ImplHub {
    private enum HubMonitor {
        /**
         * hub class对象公用锁，避免直接锁class可能造成的死锁。length 必须为 2的n次方，用位运算代替求余数
         */
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN
    }

    private static final String TAG = "ImplHub";
    private static final String IMPL_HELPER_SUFFIX = "ImplHelper";



    private static final int INIT_STATE = -1;
    private static final int CREATED = -2;

    private static Map<Class, IHub> sRealImpls = new ConcurrentHashMap<>();
    private static Map<Class, AtomicLong> sImplCtls = new ConcurrentHashMap<>();
    private static Map<Long,Long> sThreadWaiter = new ConcurrentHashMap<>();
    /**
     * 一个接口只能对应一个实现，之前的实现会被替换掉
     *
     * @param impl 实现IHub接口的对象
     */
    private static void putImpl(Class api, IHub impl) {
        if (impl == null) {
            return;
        }

        sRealImpls.put(api, impl);
    }

    public static <T extends IHub> T getImpl(Class<T> iHub) {

        if (!iHub.isInterface()) {
            Hub.sIHubLog.error(TAG, String.format("interfaceType must be a interface , %s is not a interface",
                    iHub.getName()), new IllegalArgumentException("interfaceType must be a interface"));
        }

        IHub realImpl = null;

        try {
            AtomicLong ctl = getCtls(iHub);

            realImpl = sRealImpls.get(iHub);
            if (realImpl == null) {
                long currentThreadId = Thread.currentThread().getId();
                while (ctl.get() != CREATED) {
                    if (ctl.compareAndSet(INIT_STATE, currentThreadId)) {
                        IFindImplClz iFindImplClzHelper = findImplHelper(iHub);
                        realImpl = (IHub) iFindImplClzHelper.getInstance();
                        Hub.sIHubLog.info(TAG, "normal onCreate before "+iHub+".Thread"+Thread.currentThread());
                        realImpl.onCreate();

                        for (Class apiClass : iFindImplClzHelper.getApis()) {
                            putImpl(apiClass, realImpl);
                        }
                        ctl.set(CREATED);

                        sThreadWaiter.remove(currentThreadId);
                        Hub.sIHubLog.info(TAG, "normal onCreate after "+iHub+".Thread"+Thread.currentThread());
                    } else if (ctl.get() == currentThreadId) {
                        Hub.sIHubLog.info(TAG, "recursion onCreate "+iHub+".Thread"+Thread.currentThread());
                        break;
                    } else if (ctl.get() != CREATED) {
                        //判断iHub的初始化线程是否在等待当前线程的初始化
                        if(sThreadWaiter.get(ctl.get())!=null && sThreadWaiter.get(ctl.get()) == currentThreadId) {
                            Hub.sIHubLog.info(TAG, currentThreadId +" leave "+ctl.get() + ","+iHub);
                            break;
                        }else {
                            //当前线程初始化iHub在等待ctl.get()的线程的初始化完成
                            sThreadWaiter.put(currentThreadId,ctl.get());
                            Thread.yield();
                            Hub.sIHubLog.info(TAG, currentThreadId +"wait for"+ctl.get() + ","+iHub);
                        }
                        Hub.sIHubLog.info(TAG, "concurrent onCreate "+iHub);
                    }
                }
            }

        } catch (Throwable e) {

            ImplHandler implHandler = new ImplHandler(iHub);
            if (realImpl == null) {
                realImpl = (IHub) implHandler.mImplProxy;
                Log.e(TAG, "find impl " + iHub.getSimpleName() + " error " + ", using proxy", e);
            } else {
                Log.e(TAG, "impl %s" + iHub.getSimpleName() + " exit but onCreate error , using impl", e);
            }
        }

        return (T) realImpl;
    }

    private static AtomicLong getCtls(Class iHub) {
        int monitorIndex = hash(iHub.hashCode()) & (HubMonitor.values().length - 1);
        AtomicLong ctl = sImplCtls.get(iHub);
        if (ctl == null) {
            synchronized (HubMonitor.values()[monitorIndex]) {
                if ((ctl = sImplCtls.get(iHub)) == null) {
                    ctl = new AtomicLong(INIT_STATE);
                    sImplCtls.put(iHub, ctl);
                }
            }
        }
        return ctl;
    }

    private static IFindImplClz findImplHelper(Class iHub) throws Exception {
        String apiCanonicalName = iHub.getCanonicalName();

        String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));

        String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR) + 1);

        String implCanonicalName = packageName + Hub.PACKAGER_SEPARATOR + apiName + Hub.CLASS_NAME_SEPARATOR + IMPL_HELPER_SUFFIX;
        return (IFindImplClz) Class.forName(implCanonicalName).newInstance();
    }

    /**
     * copy from HashMap jdk8
     *
     * @param key
     * @return
     */
    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    public static <T extends IHub> boolean implExist(Class<T> iHub) {
        boolean implExist = sRealImpls.containsKey(iHub);

        if (implExist) {
            return true;
        }

        try {
            String apiCanonicalName = iHub.getCanonicalName();

            String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));
            String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR) + 1);

            String implCanonicalName = packageName + Hub.PACKAGER_SEPARATOR + apiName + "_ImplHelper";
            IFindImplClz iFindImplClzHelper = (IFindImplClz) Class.forName(implCanonicalName).newInstance();

            return iFindImplClzHelper != null;

        } catch (Exception e) {
            return false;
        }
    }

    public static void removeImpl(Class<? extends IHub> api) {
        if (api == null) {
            return;
        }
        sRealImpls.remove(api);
    }
}
