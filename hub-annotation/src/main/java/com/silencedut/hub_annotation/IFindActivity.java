package com.silencedut.hub_annotation;

import java.util.List;

/**
 * @author SilenceDut
 * @date 2018/8/8
 */
public interface IFindActivity {
    List<String> paramNames();
    void inject(Object target);
    Class<?> targetActivity();
}
