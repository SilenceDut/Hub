package com.silencedut.hub.navigation;

import android.app.Activity;

import com.silencedut.hub.IHubActivity;

import java.lang.ref.WeakReference;

/**
 * @author SilenceDut
 * @date 2018/8/4
 */
public class Expand<T extends IHubActivity> {
    private T mIHubApi;
    public int requestCode;
    public WeakReference<Activity> activityWeakReference;


    public Expand(T iHubAPi) {
        this.mIHubApi = iHubAPi;
        this.activityWeakReference = new WeakReference<>(null);
    }


    public Expand<T> withResult(Activity startActivity, int resultCode) {
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
