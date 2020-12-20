package com.mjxx.speechlibsnative.sdk;

import java.io.Serializable;

public class Config implements Serializable {
    private boolean isShowLog;
    private String asrServerUrl;
    private int asrPid = 888;
    private boolean asrLongRecordEnable = true;
    private boolean asrSaveRecord;
    private String ttsServerUrl;
    private String webServerUrl;

    private boolean isWriteLog;

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

    public boolean isWriteLog() {
        return isWriteLog;
    }

    public void setWriteLog(boolean writeLog) {
        isWriteLog = writeLog;
    }

    public boolean isAsrLongRecordEnable() {
        return asrLongRecordEnable;
    }

    public void setAsrLongRecordEnable(boolean asrLongRecordEnable) {
        this.asrLongRecordEnable = asrLongRecordEnable;
    }

    public boolean isAsrSaveRecord() {
        return asrSaveRecord;
    }

    public void setAsrSaveRecord(boolean asrSaveRecord) {
        this.asrSaveRecord = asrSaveRecord;
    }
}
