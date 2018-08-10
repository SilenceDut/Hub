package com.silencedut.hubdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.silencedut.hub_annotation.HubActivity;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */

@HubActivity(activityApi = ITestApi.class,methodName = "activitySecond")
public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
