package com.silencedut.hub.navigation.activity;

import android.app.Application;

import com.silencedut.hub.Hub;
import com.silencedut.hub.IHubActivity;
import com.silencedut.hub_annotation.HubActivity;
import com.silencedut.hub_annotation.IFindActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */
public class ActivityHub {
    private static final String ACTIVITY_HELPER_SUFFIX = "Helper";
    private static Map<String,ActivityHandler> sActivityProxy = new ConcurrentHashMap<>();
    static Map<String,IFindActivity> sActivityHelperPath = new ConcurrentHashMap<>();
    static volatile Application sApplication;

    public static void init(Application application) {
        sApplication = application;
    }

    public static synchronized void inject(Object target){
        HubActivity hubActivity = target.getClass().getAnnotation(HubActivity.class);
        IFindActivity iFindActivity = generateFindActivity(hubActivity.activityApi(),hubActivity.methodName());
        if(iFindActivity!=null) {
            iFindActivity.inject(target);
        }
    }

    public static synchronized <T extends IHubActivity> Expand<T> buildExpandActivity(Class<T> iHubPointer){
        ActivityHandler activityHandler = getActivityHandler(iHubPointer);

        Expand<T> expand = new Expand((T) activityHandler.mActivityProxy);
        activityHandler.setExpand(expand);

        ActivityHub.sActivityProxy.put(iHubPointer.getCanonicalName(),activityHandler);
        return expand;
    }

    public static synchronized <T extends IHubActivity> T buildActivity(Class<T> iHubPointer) {
        ActivityHandler activityHandler = getActivityHandler(iHubPointer);

        ActivityHub.sActivityProxy.put(iHubPointer.getCanonicalName(),activityHandler);
        return (T) activityHandler.mActivityProxy;
    }

    private static synchronized <T extends IHubActivity> ActivityHandler getActivityHandler(Class<T> iHubPointer) {
        ActivityHandler activityHandler = sActivityProxy.get(iHubPointer.getCanonicalName());
        if(activityHandler == null) {
            activityHandler = new ActivityHandler(iHubPointer);
        }
        return activityHandler;
    }

    static IFindActivity generateFindActivity(Class hubActivity,String methodName) {
        try {
            String apiCanonicalName = hubActivity.getCanonicalName();

            String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));

            String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR) + 1, apiCanonicalName.length());

            String activityFindHelperClassName = packageName + Hub.PACKAGER_SEPARATOR + apiName + Hub.CLASS_NAME_SEPARATOR + methodName + Hub.CLASS_NAME_SEPARATOR+ACTIVITY_HELPER_SUFFIX;

            IFindActivity iFindActivityClzHelper = ActivityHub.sActivityHelperPath.get(activityFindHelperClassName);

            if(iFindActivityClzHelper==null) {
                iFindActivityClzHelper = (IFindActivity) Class.forName(activityFindHelperClassName).newInstance();
                ActivityHub.sActivityHelperPath.put(activityFindHelperClassName,iFindActivityClzHelper);
            }

            return iFindActivityClzHelper;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
