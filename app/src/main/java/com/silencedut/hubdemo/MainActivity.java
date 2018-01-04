package com.silencedut.hubdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.silencedut.hub.Hub;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hub.putImpl(new TestImpl());
        Hub.getImpl(ITestApi.class).test();
        Hub.getImpl(NoImplApi.class).noImpl();

        if(Hub.implExist(NoImplApi.class)) {
            Hub.getImpl(NoImplApi.class).noReturnImpl();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hub.getInstance().removeImpl(ITestApi.class);
    }
}
