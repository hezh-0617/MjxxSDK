package com.mjxx.sdk;

import java.io.Serializable;

public class Config implements Serializable {
    private boolean isShowLog;
    private String serverUrl;

    private String sn; //授权序列号，离线语音合成需要

    public boolean isShowLog() {
        return isShowLog;
    }

    public void setShowLog(boolean showLog) {
        isShowLog = showLog;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
