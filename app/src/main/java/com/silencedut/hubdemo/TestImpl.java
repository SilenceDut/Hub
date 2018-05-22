package com.silencedut.hubdemo;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by SilenceDut on 2018/1/3 .
 */

class TestImpl implements ITestApi , IMultiApi{

    @Override
    public void test(Context context) {
        Toast.makeText(context,"TestImpl 的 test方法被调用",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void showMulti(Context context) {
        Toast.makeText(context,"TestImpl 的 showMulti方法被调用",Toast.LENGTH_LONG).show();
    }
}
