package com.silencedut.hubdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.silencedut.hub.Hub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//
                List<Map<String,Integer>> mapList =  new ArrayList<>();

                Map a = new HashMap<String, Integer>();
                a.put("abc",10);
                Map b = new HashMap<String, Integer>();
                b.put("abc",3);
                b.put("278",5);
                mapList.add(a);
                mapList.add(b);
                Hub.getActivity(IActivityTest.class).activitySecond(mapList,9);

               // Hub.getActivityWithExpand(IActivityTest.class).withResult(MainActivity.this,10).build().activitySecond(mapList,9);

//                Hub.getActivity(ITestApi.class).activitySecond("Hello",10);
//                Hub.getActivityWithExpand(ITestApi.class).build().activitySecond("Hello",10);
//                Hub.getActivityWithExpand(ITestApi.class).withResult(MainActivity.this,5).build().activitySecond("Hello",10);
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
