package com.silencedut.hubdemo;

import com.silencedut.hub.IHubActivity;

import java.util.List;
import java.util.Map;

/**
 * @author SilenceDut
 * @date 2018/8/12
 */
public interface IActivityTest extends IHubActivity{
    boolean activitySecond(List<Map<String,Integer>> listMap, int value);
    void c();
}
