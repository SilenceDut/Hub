package com.silencedut.hub.navigation.activity;

import android.content.Intent;
import android.util.Log;

import com.silencedut.hub.BuildConfig;
import com.silencedut.hub.Hub;
import com.silencedut.hub_annotation.IFindActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */
public class ActivityHandler implements InvocationHandler {

    private static final String TAG = "ActivityHandler";
    private Class mIHubPointer;
    public Object mActivityProxy;
    private Params mTParams;

    ActivityHandler(Class iHubPointer ) {
        this.mIHubPointer = iHubPointer;
        this.mActivityProxy =  Proxy.newProxyInstance(mIHubPointer.getClassLoader(), new Class[]{mIHubPointer}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        /*
          in debug model throw crash
         */

        if( method.getReturnType() != Void.TYPE && BuildConfig.DEBUG) {

            throw new RuntimeException("can't get return value , no impl of "+ method.getName()+"parameter "+method.getParameterTypes());
        }
        try {
            String apiCanonicalName = mIHubPointer.getCanonicalName();

            String activityKey = apiCanonicalName + Hub.CLASS_METHOD_SEPARATOR + method.getName();

            Class<?> targetActivity = ActivityHub.mActivityPath.get(activityKey);

            Log.d(TAG,"invoke method of " + method.getName()+",targetActivity = "+targetActivity);
            if(targetActivity==null) {

                String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR));

                String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Hub.PACKAGER_SEPARATOR) + 1, apiCanonicalName.length());

                String implCanonicalName = packageName + Hub.PACKAGER_SEPARATOR + apiName + "_" + method.getName() + "_ImplHelper";


                IFindActivity iFindActivityClzHelper = (IFindActivity) Class.forName(implCanonicalName).newInstance();

                targetActivity = iFindActivityClzHelper.targetActivity();

                ActivityHub.mActivityPath.put(activityKey,targetActivity);
            }

            Intent intent = new Intent(ActivityHub.sApplication,targetActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(mTParams.bundle);
            if(this.mTParams.activity!=null && this.mTParams.resultCode != 0) {
                this.mTParams.activity.startActivityForResult(intent,this.mTParams.resultCode);
            }else {
                ActivityHub.sApplication.startActivity(intent);
            }

            this.mTParams = null;
        } catch (Exception e) {

            Log.e(TAG,"invoke error of "+ mIHubPointer.getName()+":"+method.getName()+" fail ",e);
        }


        return null;
    }

    public void setParams(Params params) {
        this.mTParams = params;
    }

}
