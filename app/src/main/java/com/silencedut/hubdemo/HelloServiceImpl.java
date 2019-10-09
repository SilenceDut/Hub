package com.silencedut.hubdemo;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;

// 实现接口
@Route(path = "/yourservicegroupname/hello3", name = "测试服务")
public class HelloServiceImpl implements ARouterSever1 {


    @Override
    public void init(Context context) {
        try {
            Log.d("ARouterSever","HelloServiceImp1");
            Thread.sleep(5000);
            Log.d("ARouterSever","HelloServiceImp1 after");
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
}