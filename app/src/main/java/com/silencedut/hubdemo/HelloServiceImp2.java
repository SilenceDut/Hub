package com.silencedut.hubdemo;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;

// 实现接口
@Route(path = "/yourservicegroupname/hello1", name = "测试服务")
public class HelloServiceImp2 implements ARouterSever2{


    @Override
    public void init(Context context) {
        try {
            Log.d("ARouterSever","HelloServiceImp2");
            Thread.sleep(3000);
            Log.d("ARouterSever","HelloServiceImp2 sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}