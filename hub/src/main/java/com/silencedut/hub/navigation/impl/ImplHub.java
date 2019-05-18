package com.silencedut.hub.navigation.impl;


import com.silencedut.hub.Hub;
import com.silencedut.hub.IHub;
import com.silencedut.hub.utils.ErrorUseHandleException;
import com.silencedut.hub_annotation.IFindImplClz;

import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;


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

    /**
     * try park 100ms
     */
    private static final long THREAD_PARK_NANOS = 50 * 1000000;

    private static final int INIT_STATE = -1;
    private static final int CREATED = -2;

    private static Map<Class<? extends IHub>, IHub> sRealImpls = new ConcurrentHashMap<>();
    private static Map<Class, AtomicLong> sImplCtls = new ConcurrentHashMap<>();
    private static Map<Thread, Long> sThreadWaiter = new ConcurrentHashMap<>();

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
            Hub.sHubConfig.getIHubLog()
                    .error(TAG, String.format("interfaceType must be a interface , %s is not a interface",
                            iHub.getName()), new IllegalArgumentException("interfaceType must be a interface"));
        }

        while (sRealImpls.get(iHub) == null) {
            AtomicLong ctl = getCtls(iHub);
            long currentThreadId = Thread.currentThread().getId();
            if (ctl.compareAndSet(INIT_STATE, currentThreadId)) {
                IFindImplClz iFindImplClzHelper;
                try {
                    iFindImplClzHelper = findImplHelper(iHub);
                    IHub realImpl = (IHub) iFindImplClzHelper.getInstance();
                    realImpl.onCreate();
                    for (Class apiClass : iFindImplClzHelper.getApis()) {
                        putImpl(apiClass, realImpl);
                    }
                    ctl.set(CREATED);
                    releaseWaiter(iHub);
                } catch (Exception e) {
                    ctl.compareAndSet(currentThreadId, INIT_STATE);
                    Hub.sHubConfig.getIHubLog().error(TAG,
                            iHub + " initialization error " + "，Thread：" + Thread.currentThread(), e);
                    return exceptionHandler(iHub);
                }

            } else if (ctl.get() == currentThreadId) {
                Hub.sHubConfig.errorUseHandler.errorUseHub(
                        "getImpl api:" + iHub + " error,recursion onCreate() on same thread,check api impl " +
                                "init 、 constructor 、onCreate() to avoid circular reference,",
                        ErrorUseHandleException.traceToString(2, Thread.currentThread().getStackTrace()));
                break;
            } else if (ctl.get() != CREATED) {
                if (checkDeadLock(iHub, ctl.get())) {
                    return exceptionHandler(iHub);
                } else {
                    addAndParkWaiter(iHub, ctl);
                }
            }

            if (Thread.currentThread().isInterrupted()) {
                Hub.sHubConfig.getIHubLog().info(TAG,
                        iHub + " thread  interrupted ,return proxy for the moment" + "，Thread：" +
                                Thread.currentThread());
                return exceptionHandler(iHub);
            }
        }
        return (T) sRealImpls.get(iHub);
    }


    private static <T extends IHub> T exceptionHandler(Class<T> iHub) {
        if (sRealImpls.get(iHub) == null) {

            return (T) new ImplHandler(iHub).mImplProxy;
        } else {
            return (T) sRealImpls.get(iHub);
        }
    }

    /**
     * 检查是否存在不同线程里onCreate循环调用的问题.根据策略让一个线程退出或者让App抛出崩溃,破怀# 等待不释放的条件 #避免类似死锁
     *
     * @param iHub           当前线程尝试获取的接口
     * @param currentWaitFor 当前线程等待currentWaitFor线程的初始化
     * @return 是否形成不同线程间的循环初始化
     */

    private static boolean checkDeadLock(Class iHub, long currentWaitFor) {

        Map<Long, Long> checkDeadMap = new HashMap<>(8);
        for (Map.Entry<Thread, Long> threadWait : sThreadWaiter.entrySet()) {
            checkDeadMap.put(threadWait.getKey().getId(), threadWait.getValue());
        }
        Long current = Thread.currentThread().getId();
        Long temp = checkDeadMap.get(current);
        while (temp != null && !temp.equals(current)) {
            temp = checkDeadMap.get(temp);
        }
        boolean deadLock = temp!=null;
        if(deadLock) {
            Hub.sHubConfig.getIHubLog().info(TAG, current + " leave " + currentWaitFor + "," + iHub);
            //一般是在不同线程初始化互相引用导致
            Hub.sHubConfig.errorUseHandler.errorUseHub(
                    "getImpl api:" + iHub + " error,recursion on multi thread,check api impl " +
                            "init 、 constructor 、onCreate() to avoid circular reference"
                    , ErrorUseHandleException.traceToString(2, Thread.currentThread().getStackTrace()));
        }
        return deadLock;
    }

    /**
     * 如果其他线程在执行iHub的onCreate方法，等待执行完，通过LockSupport.parkNanos避免cpu的无谓消耗和一些未考虑的情况下导致的无法中止的park
     *
     * @param iHub           想要获取的接口
     * @param currentWaitFor 当前真正初始化该接口的对象id
     */
    private static void addAndParkWaiter(Class iHub, AtomicLong currentWaitFor) {
        Thread current = Thread.currentThread();
        if (sThreadWaiter.get(current) == null && currentWaitFor.get() != CREATED) {
            sThreadWaiter.put(current, currentWaitFor.get());
            Hub.sHubConfig.getIHubLog().info(TAG,
                    "park thread :" + current + ",waiting for threadId on " + currentWaitFor + " to create impl of " +
                            iHub);
            LockSupport.parkNanos(iHub, THREAD_PARK_NANOS);
        }
    }

    private static  void releaseWaiter(Class iHub) {
        Hub.sHubConfig.getIHubLog().info(TAG, "releaseWaiter :" + iHub);
        Thread current = Thread.currentThread();
        for (Map.Entry<Thread, Long> lockPair : sThreadWaiter.entrySet()) {

            if (lockPair.getValue() == current.getId()) {
                Hub.sHubConfig.getIHubLog().info(TAG,
                        "releaseWaiter :" + lockPair+",block:"+ LockSupport.getBlocker(lockPair.getKey()));
                if (LockSupport.getBlocker(lockPair.getKey()) == iHub) {
                    Hub.sHubConfig.getIHubLog().info(TAG, "unPark thread :" + lockPair.getKey());
                    LockSupport.unpark(lockPair.getKey());
                }
                sThreadWaiter.remove(lockPair.getKey());
            }
        }
    }

    private static AtomicLong getCtls(Class iHub) {

        AtomicLong ctl = sImplCtls.get(iHub);
        if (ctl == null) {
            synchronized (monitor(iHub)) {
                if ((ctl = sImplCtls.get(iHub)) == null) {
                    ctl = new AtomicLong(INIT_STATE);
                    sImplCtls.put(iHub, ctl);
                }
            }
        }
        return ctl;
    }

    private static HubMonitor monitor(Class iHub) {
        int monitorIndex = hash(iHub.hashCode()) & (HubMonitor.values().length - 1);
        return HubMonitor.values()[monitorIndex];
    }

    private static IFindImplClz findImplHelper(Class iHub) throws Exception {
        String apiCanonicalName = iHub.getCanonicalName();

        String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));

        String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR) + 1);

        String implCanonicalName =
                packageName + Hub.PACKAGER_SEPARATOR + apiName + Hub.CLASS_NAME_SEPARATOR + IMPL_HELPER_SUFFIX;
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
}
