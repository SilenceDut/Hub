package com.silencedut.hubdemo;

import android.content.Context;
import android.widget.Toast;

import com.silencedut.hub_annotation.HubInject;


/**
 * Created by SilenceDut on 2018/1/3 .
 */

@HubInject(api = {IMultiApi.class})
class TestImpl implements   IMultiApi{

//    @Override
//    public void test(Context context) {
//        Toast.makeText(context,"TestImpl 的 test方法被调用",Toast.LENGTH_LONG).show();
//
//    }


    @Override
    public void onCreate() {

    }

    @Override
    public void showMulti(Context context) {
        Toast.makeText(context,"TestImpl 的 showMulti方法被调用",Toast.LENGTH_LONG).show();
    }
}
