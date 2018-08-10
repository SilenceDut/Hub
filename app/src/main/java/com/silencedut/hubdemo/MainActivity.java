package com.silencedut.hubdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.silencedut.hub.Hub;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.method_invoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hub.getImpl(IMultiApi.class).showMulti(MainActivity.this);
            }
        });

        findViewById(R.id.activity_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hub.getActivity(ITestApi.class).withResult(MainActivity.this,1).build().activitySecond("Hello",10);
            }
        });

        //Log.d(TAG,Hub.implExist(ITestApi.class)+";"+Hub.implExist(NoImplApi.class));

        if(Hub.implExist(NoImplApi.class)) {
            Hub.getImpl(NoImplApi.class).noReturnImpl();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
