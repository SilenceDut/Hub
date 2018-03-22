package com.silencedut.hub_annotation;

import java.util.Set;

/**
 * Created by SilenceDut on 2018/1/15 .
 */

public interface IFindImplClz {
     Object newImplInstance() ;
     Set<String> getApis();
}
