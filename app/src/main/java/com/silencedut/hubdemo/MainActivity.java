package com.silencedut.hubdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.silencedut.hub.Hub;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hub.getInstance().putImpl(new TestImpl());
        Hub.getInstance().getImpl(ITestApi.class).test();
        Hub.getInstance().getImpl(NoImplApi.class).noImpl();

        if(Hub.getInstance().implExist(NoImplApi.class)) {
            Hub.getInstance().getImpl(NoImplApi.class).noReturnImpl();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hub.getInstance().removeImpl(ITestApi.class);
    }
}
