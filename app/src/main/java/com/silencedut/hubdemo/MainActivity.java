package com.silencedut.hubdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.silencedut.hub.Hub;
import com.silencedut.hub.provider.HubConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Thread mThread1;
    private Thread mThread2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hub.configHub(HubConfig.create().setDebug(true));

        findViewById(R.id.method_invoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Hub.getImpl(ITestApi.class);
                if(mThread1 == null) {

                    mThread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Hub.getImpl(ITestApi.class);
                        }
                    });
                    mThread1.start();


                    mThread2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Hub.getImpl(ITestApi1.class);
                        }
                    });
                    mThread2.start();
                }else {
                    Log.d("ImplHub", Arrays.toString(mThread1.getStackTrace()));
                    Log.d("ImplHub", Arrays.toString(mThread2.getStackTrace()));
                }

            }
        });

        findViewById(R.id.activity_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hub.getImpl(ITestApi.class).test(MainActivity.this);
                Hub.getImpl(ITestApi1.class).test(MainActivity.this);
//
//                 List<Map<String,Integer>> mapList =  new ArrayList<>();
//
//                 Map a = new HashMap<String, Integer>();
//                 a.put("abc",10);
//                 Map b = new HashMap<String, Integer>();
//                 b.put("abc",3);
//                 b.put("278",5);
//                 mapList.add(a);
//                 mapList.add(b);
//                 boolean found = Hub.getActivity(IActivityTest.class).activitySecond(mapList,9);
//                 Toast.makeText(MainActivity.this,"找到了对应的Activity ? "+ found,Toast.LENGTH_LONG).show();
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
