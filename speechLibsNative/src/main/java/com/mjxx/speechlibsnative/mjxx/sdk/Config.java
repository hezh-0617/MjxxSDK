package com.mjxx.speechlibsnative.mjxx.sdk;

import java.io.Serializable;

public class Config implements Serializable {
    private boolean isShowLog;
    private String asrServerUrl;
    private String ttsServerUrl;

    private String sn; //授权序列号，离线语音合成需要

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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
