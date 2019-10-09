package com.silencedut.hubdemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.silencedut.hub.Hub;
import com.silencedut.hub_annotation.HubInject;

/**
 * @author SilenceDut
 * @date 2019/4/21
 */
@HubInject(api = ITestApi1.class)
public class ITestApi1Impl implements ITestApi1 {
    //ITestApi mApi1 = Hub.getImpl(ITestApi.class);
    @Override
    public void test(Context context) {
        //mApi1.test(context);

    }

    @Override
    public void onCreate() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
