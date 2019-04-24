package com.silencedut.hub.provider;

import android.util.Log;

import com.silencedut.hub.utils.DebugErrorUseHandler;
import com.silencedut.hub.utils.ErrorUseHandler;
import com.silencedut.hub.utils.ReleaseErrorUseHandler;

/**
 * @author SilenceDut
 * @date 2019/4/24
 * App config for hub
 */
public class HubConfig {
    boolean isDebug;
    IHubLog iHubLog;
    public ErrorUseHandler errorUseHandler = RELEASE_ERROR_USE_HANDLER;

    private HubConfig(){

    }

    public static HubConfig create() {
        return new HubConfig();
    }

    public IHubLog getIHubLog() {
        if(iHubLog == null) {
            iHubLog = new IHubLog() {
                @Override
                public void info(String tag,String info) {
                    Log.i(tag,info);
                }

                @Override
                public void error(String tag, String msg,  Throwable... throwables) {
                    Log.e(tag,msg);
                }
            };
        }
        return iHubLog;
    }

    public HubConfig setDebug(boolean debug) {
        isDebug = debug;
        if(isDebug) {
            errorUseHandler = DEBUG_ERROR_USE_HANDLER;
        }else {
            errorUseHandler = RELEASE_ERROR_USE_HANDLER;
        }
        return this;
    }

    public HubConfig setIHubLog(IHubLog iHubLog) {
        this.iHubLog = iHubLog;
        return this;
    }

    private static final DebugErrorUseHandler DEBUG_ERROR_USE_HANDLER = new DebugErrorUseHandler();
    private static final ReleaseErrorUseHandler RELEASE_ERROR_USE_HANDLER = new DebugErrorUseHandler();


}
