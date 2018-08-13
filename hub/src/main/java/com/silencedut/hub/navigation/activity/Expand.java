package com.silencedut.hub.navigation.activity;

import android.app.Activity;

import com.silencedut.hub.IHubActivity;

import java.lang.ref.WeakReference;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */
public class Expand<T extends IHubActivity> {
    private T mIHubApi;
    int requestCode;
    WeakReference<Activity> activityWeakReference;


    public Expand(T iHubAPi) {
        this.mIHubApi = iHubAPi;
        this.activityWeakReference = new WeakReference<>(null);
    }


    public Expand<T> withResult(Activity startActivity, int requestCode) {
        if(this.activityWeakReference.get() == null && startActivity == null ) {
            return this;
        }
        this.activityWeakReference = new WeakReference<>(startActivity);
        this.requestCode  = requestCode;
        return this;
    }


    public T build() {
        return mIHubApi;
    }
}
