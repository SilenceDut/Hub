package com.silencedut.hubdemo;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;

// 实现接口
@Route(path = "/yourservicegroupname/hello2", name = "测试服务")
public class HelloServiceImp3 implements ARouterSever3{


    @Override
    public void init(Context context) {
        try {
            Log.d("ARouterSever","HelloServiceImp3");
            Thread.sleep(4000);
            Log.d("ARouterSever","HelloServiceImp3 after");
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
}