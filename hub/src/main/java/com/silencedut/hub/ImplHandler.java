package com.silencedut.hub;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by SilenceDut on 2018/1/3 .
 */

class ImplHandler implements InvocationHandler {

    private static final String TAG = "ImplHandler";
    private Class mIHub;
    Object mImplProxy;

    ImplHandler(Class iHub ) {
        this.mIHub = iHub;
        this.mImplProxy =  Proxy.newProxyInstance(mIHub.getClassLoader(), new Class[]{mIHub}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Log.e(TAG,"invoke impl of "+ mIHub.getName()+" fail , impl not exit !");

        /*
          in debug model throw crash
         */

        if( method.getReturnType() != Void.TYPE && BuildConfig.DEBUG) {
            throw new RuntimeException("can't get return value , no impl of "+ mIHub.getName());
        }

        if(NumberUtil.isPrimitiveType(method.getReturnType())) {
            return -1;
        } else if(method.getReturnType() == char.class){
            return '0';
        }else if(method.getReturnType() == boolean.class){
            return false;
        }

        return null;
    }

}
