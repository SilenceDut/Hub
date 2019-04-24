package com.silencedut.hub.navigation.impl;

import android.os.Looper;
import android.util.Log;

import com.silencedut.hub.Hub;
import com.silencedut.hub.IHub;
import com.silencedut.hub.utils.ErrorUseHandleException;
import com.silencedut.hub_annotation.IFindImplClz;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
            Hub.sHubConfig.getIHubLog().error(TAG, String.format("interfaceType must be a interface , %s is not a interface",
                    iHub.getName()), new IllegalArgumentException("interfaceType must be a interface"));
        }


            AtomicLong ctl = getCtls(iHub);

            IHub realImpl = sRealImpls.get(iHub);
        if (realImpl == null) {
            long currentThreadId = Thread.currentThread().getId();
            while (ctl.get() != CREATED) {
                if (ctl.compareAndSet(INIT_STATE, currentThreadId)) {
                    IFindImplClz iFindImplClzHelper ;
                    try {
                        iFindImplClzHelper = findImplHelper(iHub);
                    } catch (Exception e) {
                        ctl.compareAndSet(currentThreadId, INIT_STATE);
                        Hub.sHubConfig.getIHubLog().error(TAG,
                                iHub+" findImplHelper error "+"，Thread："+Thread.currentThread(),e);
                        return (T) new ImplHandler(iHub).mImplProxy;
                    }
                    realImpl = (IHub) iFindImplClzHelper.getInstance();
                    Hub.sHubConfig.getIHubLog().info(TAG,
                            iHub+" onCreate before "+"，Thread："+Thread.currentThread());
                    realImpl.onCreate();
                    for (Class apiClass : iFindImplClzHelper.getApis()) {
                        putImpl(apiClass, realImpl);
                    }
                    ctl.set(CREATED);
                    sThreadWaiter.remove(currentThreadId);
                    Hub.sHubConfig.getIHubLog().info(TAG,
                            iHub+"onCreate after "+" ，Thread："+Thread.currentThread());
                } else if (ctl.get() == currentThreadId) {
                    Hub.sHubConfig.errorUseHandler.errorUseHub(
                            "getImpl api:"+iHub+ " error,recursion onCreate() on same thread,check api impl " +
                                    "init 、 constructor 、onCreate() to avoid circular reference,",traceToString(2
                                    , Thread.currentThread().getStackTrace()));
                    break;
                } else if (ctl.get() != CREATED) {
                    //判断iHub的初始化线程是否在等待当前线程的初始化
                    if(sThreadWaiter.get(ctl.get())!=null && sThreadWaiter.get(ctl.get()) == currentThreadId) {

                        Hub.sHubConfig.getIHubLog().info(TAG, currentThreadId +" leave "+ctl.get() + ","+iHub);
                        //一般是在不同线程初始化互相引用导致

                        Hub.sHubConfig.errorUseHandler.errorUseHub(
                                "getImpl api:"+iHub+ " error,recursion on multi thread,check api impl " +
                                        "init 、 constructor 、onCreate() to avoid circular reference"
                                        ,traceToString(2, Thread.currentThread().getStackTrace()));
                        break;
                    }else {
                        //当前线程初始化iHub在等待ctl.get()的线程的初始化完成
                        sThreadWaiter.put(currentThreadId,ctl.get());
                        if(!mainThreadCheck()) {
                            Thread.yield();
                        }
                        Hub.sHubConfig.getIHubLog().info(TAG, currentThreadId +"wait for"+ctl.get() + ","+iHub);
                    }
                }
            }
        }
        return (T) realImpl;
    }


    private static boolean mainThreadCheck() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
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
            return findImplHelper(iHub) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static String traceToString(int startIndex,Object[] stackArray) {
        if (stackArray == null) {
            return "null";
        }

        int iMax = stackArray.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = startIndex; ; i++) {
            b.append(String.valueOf(stackArray[i]));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append("\n");
        }
    }
}
