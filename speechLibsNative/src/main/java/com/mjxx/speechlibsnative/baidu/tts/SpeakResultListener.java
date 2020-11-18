package com.mjxx.speechlibsnative.baidu.tts;

public interface SpeakResultListener {
    void onFinish(String speakWhat);
    void onErr(String msg);
}
