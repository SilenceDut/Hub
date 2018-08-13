package com.silencedut.hub.navigation.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.silencedut.hub.navigation.HubJsonHelper;
import com.silencedut.hub_annotation.IFindActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */
public class ActivityHandler implements InvocationHandler {

    private static final String TAG = "ActivityHandler";
    private Class mIHubPointer;
    public Object mActivityProxy;
    private Expand mExpand;

    ActivityHandler(Class iHubPointer) {
        this.mIHubPointer = iHubPointer;
        this.mActivityProxy =  Proxy.newProxyInstance(mIHubPointer.getClassLoader(), new Class[]{mIHubPointer}, this);

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        boolean find =false;
        try {

            IFindActivity iFindActivityClzHelper = ActivityHub.generateFindActivity(mIHubPointer,method.getName());

            Intent intent = new Intent(ActivityHub.sApplication,iFindActivityClzHelper.targetActivity());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            buildBundle(intent,method,args,iFindActivityClzHelper);

            if(mExpand!=null && mExpand.activityWeakReference.get()!=null) {
                ((Activity)(mExpand.activityWeakReference.get())).startActivityForResult(intent,mExpand.requestCode);
                mExpand.activityWeakReference.clear();
                mExpand = null;
            }else {
                ActivityHub.sApplication.startActivity(intent);
            }

            find = true;
        } catch (Exception e) {

            Log.e(TAG,"invoke error of "+ mIHubPointer.getName()+":"+method.getName()+" fail ",e);
        }

        return find;
    }

    private void buildBundle(Intent intent,Method method, Object[] args,IFindActivity iFindActivityClzHelper) {
        List<String> paramNames = iFindActivityClzHelper.paramNames();
        for(int index =0; index < args.length;index++) {
            Type type = method.getGenericParameterTypes()[index];
            if(type == Integer.TYPE || type == int.class) {
                intent.putExtra(paramNames.get(index), (Integer) args[index]);
            }else if(type == Byte.TYPE || type == byte.class) {
                intent.putExtra(paramNames.get(index), (Byte) args[index]);
            }else if(type == Short.TYPE || type == short.class) {
                intent.putExtra(paramNames.get(index), (Short) args[index]);
            } else if(type == Long.TYPE || type == long.class) {
                intent.putExtra(paramNames.get(index), (Long) args[index]);
            } else if(type == Float.TYPE || type == float.class) {
                intent.putExtra(paramNames.get(index), (Float) args[index]);
            } else if(type == Double.TYPE || type == double.class) {
                intent.putExtra(paramNames.get(index), (Double) args[index]);
            } else if(type == Boolean.TYPE || type == boolean.class) {
                intent.putExtra(paramNames.get(index), (Boolean) args[index]);
            }else if(type == String.class ) {
                intent.putExtra(paramNames.get(index), (String) args[index]);
            }else {
                intent.putExtra(paramNames.get(index), HubJsonHelper.toJson(args[index]));
            }
        }

    }


    public void setExpand(Expand expand) {
       this.mExpand = expand;
    }

}
