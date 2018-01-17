package com.silencedut.hubdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.silencedut.hub.Hub;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hub.getImpl(ITestApi.class);

        Log.d(TAG,Hub.implExist(ITestApi.class)+";"+Hub.implExist(NoImplApi.class));

        if(Hub.implExist(NoImplApi.class)) {
            Hub.getImpl(NoImplApi.class).noReturnImpl();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hub.removeImpl(ITestApi.class);
    }
}
