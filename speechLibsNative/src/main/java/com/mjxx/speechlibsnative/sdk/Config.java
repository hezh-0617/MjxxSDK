package com.mjxx.speechlibsnative.sdk;

import java.io.Serializable;

public class Config implements Serializable {
    private boolean isShowLog;
    private String asrServerUrl;
    private int asrPid = 888;
    private String ttsServerUrl;
    private String webServerUrl;

    public boolean isShowLog() {
        return isShowLog;
    }

    public void setShowLog(boolean showLog) {
        isShowLog = showLog;
    }

    public String getAsrServerUrl() {
        return asrServerUrl;
    }

    public void setAsrServerUrl(String asrServerUrl) {
        this.asrServerUrl = asrServerUrl;
    }

    public String getTtsServerUrl() {
        return ttsServerUrl;
    }

    public void setTtsServerUrl(String ttsServerUrl) {
        this.ttsServerUrl = ttsServerUrl;
    }

    public String getWebServerUrl() {
        return webServerUrl;
    }

    public void setWebServerUrl(String webServerUrl) {
        this.webServerUrl = webServerUrl;
    }

    public int getAsrPid() {
        return asrPid;
    }

    public void setAsrPid(int asrPid) {
        this.asrPid = asrPid;
    }
}
