package com.silencedut.hubdemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.silencedut.hub.Hub;
import com.silencedut.hub_annotation.HubInject;


/**
 * Created by SilenceDut on 2018/1/3 .
 */

@HubInject(api = {IMultiApi.class,ITestApi.class})
class TestImpl implements  IMultiApi,ITestApi{
    //ITestApi1 mApi1 = Hub.getImpl(ITestApi1.class);

    @Override
    public void test(Context context) {
       Toast.makeText(context,"TestImpl 的 test方法被调用",Toast.LENGTH_LONG).show();

    }


    @Override
    public void onCreate() {
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("ImplHub","ImplHub onCreate");
        Hub.getImpl(ITestApi1.class);
        Log.d("ImplHub","ImplHub onCreate after");
    }

    @Override
    public void showMulti(Context context) {
        Toast.makeText(context,"TestImpl 的 showMulti方法被调用",Toast.LENGTH_LONG).show();
    }
}
