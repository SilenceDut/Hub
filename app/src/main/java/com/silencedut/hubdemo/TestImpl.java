package com.silencedut.hubdemo;

import android.util.Log;

/**
 * Created by SilenceDut on 2018/1/3 .
 */

public class TestImpl implements ITestApi {
    @Override
    public void test() {
        Log.d("TestImpl","test");
    }
}
