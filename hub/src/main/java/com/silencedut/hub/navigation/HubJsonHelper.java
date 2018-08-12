package com.silencedut.hub.navigation;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

/**
 * @author SilenceDut
 * @date 2018/8/11
 */
public class HubJsonHelper {


    public static String toJson(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T fromJson(String string, Type type) {
        try {
            return JSON.parseObject(string,type);
        } catch (Exception e) {
            return null;
        }
    }
}
