package com.silencedut.hubdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.silencedut.hub.Hub;
import com.silencedut.hub_annotation.HubActivity;

import java.util.List;
import java.util.Map;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */

@HubActivity(activityApi = IActivityTest.class,methodName = "activitySecond")
public class SecondActivity extends AppCompatActivity {
    List<Map<String,Integer>> listMap;
    int value;
    TextView mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Hub.inject(this);

        mContent = findViewById(R.id.content);
        mContent.setText("second Activity \n process :second \n a size "+listMap.size()+" , b = "+value);

        Log.d("SecondActivity","a = " +listMap+ "b ="+listMap);
    }
}
