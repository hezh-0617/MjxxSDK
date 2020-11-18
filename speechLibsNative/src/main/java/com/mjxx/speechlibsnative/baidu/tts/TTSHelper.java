package com.mjxx.speechlibsnative.baidu.tts;

import android.text.TextUtils;

import com.mjxx.speechlibsnative.mjxx.sdk.Config;
import com.mjxx.speechlibsnative.mjxx.utils.DeviceUtil;
import com.mjxx.speechlibsnative.mjxx.utils.LogUtil;

import java.net.URLEncoder;

public class TTSHelper {

    private static final String TAG = "TTSHelper";
    private Config config;
    private SpeakResultListener listener;

    public TTSHelper(Config config, SpeakResultListener listener) {
        this.config = config;
        this.listener = listener;
    }

    public void speak(String result) {
        speak(result, 5);
    }

    public void speak(final String result, int speed) {

        if (TextUtils.isEmpty(result)) {
            stopAll();
            return;
        }

        String ttsServerUrl = config.getTtsServerUrl();
        if (!ttsServerUrl.endsWith("/")) {
            ttsServerUrl += "/";
        }
        try {

            MediaPlayerManager.getInstance().setMediaPlayerManagerInterface(new MediaPlayerManagerInterface() {
                @Override
                public void onComplete() {
                    stopAll();
                    listener.onFinish(result);
                }
            });
            String urlBuilder = ttsServerUrl + "text2audio?tex=" + URLEncoder.encode(result, "utf-8") +
                    "&lan=zh&pdt=993&ctp=1&per=5117&cuid=" + URLEncoder.encode(DeviceUtil.getNewMac(), "utf-8") +
                    "&spd=" + speed +
                    "&pit=5&vol=10&";

            LogUtil.d(TAG, "urlBuilder: " + urlBuilder);

            MediaPlayerManager.getInstance().mPlay(urlBuilder);
        } catch (Exception e) {
            e.printStackTrace();
            stopAll();
            listener.onErr(e.getMessage());
        }
    }


    public void stopAll() {
        MediaPlayerManager.getInstance().mDestory();
    }


}
