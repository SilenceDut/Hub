package com.silencedut.hubdemo;

import com.silencedut.hub_annotation.HubInject;


/**
 * Created by SilenceDut on 2018/1/3 .
 */
@HubInject(api = ITestApi.class)
class TestImpl implements ITestApi {

    @Override
    public void test() {

    }
}
