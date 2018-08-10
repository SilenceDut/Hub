package com.silencedut.hub.navigation.activity;

import android.app.Activity;
import android.os.Bundle;

import com.silencedut.hub.IHubPointer;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */
public class Params<T extends IHubPointer> {
    private T mIHubApi;
    int resultCode;
    Activity activity;
    Bundle bundle = new Bundle();

    public Params(T iHubAPi) {
        this.mIHubApi = iHubAPi;
    }

    public Params<T> withString(String key, String value) {
        bundle.putString(key,value);
        return this;
    }

    public Params<T> withBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public Params<T> withResult(Activity activity,int resultCode) {
        this.activity = activity;
        this.resultCode = resultCode;
        return this;
    }

    public T build() {
        return mIHubApi;
    }
}
