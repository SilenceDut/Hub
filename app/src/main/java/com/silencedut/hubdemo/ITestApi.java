package com.silencedut.hubdemo;

import com.silencedut.hub.IHubActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by SilenceDut on 2018/1/3 .
 */

public interface ITestApi extends IHubActivity {

    void activitySecond(List<Map<String,Integer>> a, int b);
    void c();
}
