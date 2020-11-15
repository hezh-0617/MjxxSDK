package com.baidu.tts;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.baidu.tts.control.InitConfig;
import com.baidu.tts.control.MySyntherizer;
import com.baidu.tts.listener.UiMessageListener;
import com.baidu.tts.util.Auth;
import com.baidu.tts.util.AutoCheck;
import com.baidu.tts.util.IOfflineResourceConst;
import com.baidu.tts.util.OfflineResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by topTech on 2020/4/20.
 * des:
 */
public class TTSHelper {

    private static final String TAG = "TTSLOG";


    private MySyntherizer synthesizer;
    private String appId;
    private String appKey;
    private String secretKey;
    private String sn; // 纯离线合成SDK授权码；离在线合成SDK免费，没有此参数

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； TtsMode.OFFLINE 纯离线合成，需要纯离线SDK
    private TtsMode ttsMode = IOfflineResourceConst.DEFAULT_SDK_TTS_MODE;
    private boolean isOnlineSDK = TtsMode.ONLINE.equals(IOfflineResourceConst.DEFAULT_SDK_TTS_MODE);

    private String offlineVoice = OfflineResource.VOICE_FEMALE;

    private Context context;

    private int speed = 8;  //语速

    private SpeechSynthesizerListener listener;


    public TTSHelper(Context context, String sn, @NonNull SpeechSynthesizerListener listener) {
        this.context = context;
        this.sn = sn;
        this.listener = listener;
        initSpeechSynthesizer();
    }

//    public TTSHelper(Context context,int speed) {
//        this.context = context;
//        this.speed = speed;
//        initSpeechSynthesizer();
//    }

    private void initSpeechSynthesizer() {

        try {
//            Auth.getInstance(context);
            Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).metaData;
            appId = metaData.getString("com.baidu.speech.APP_ID");
            appKey = metaData.getString("com.baidu.speech.API_KEY");
            secretKey = metaData.getString("com.baidu.speech.SECRET_KEY");
//            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        LoggerProxy.printable(true);

//        appId = Auth.getInstance(context).getAppId()
//        appKey = Auth.getInstance(context).getAppKey();
//        secretKey = Auth.getInstance(context).getSecretKey();
//        sn = Auth.getInstance(context).getSn(); // 离线合成SDK必须有此参数；在线合成SDK免费，没有此参数


//        SpeechSynthesizerListener listener = new UiMessageListener();
        InitConfig config = getInitConfig(listener);
        synthesizer = new MySyntherizer(context, config); // 此处可以改为MySyntherizer 了解调用过程

    }

    private InitConfig getInitConfig(SpeechSynthesizerListener listener) {
        Map<String, String> params = getParams();
        // 添加你自己的参数
        InitConfig initConfig;
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        if (sn == null) {
            initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        } else {
            initConfig = new InitConfig(appId, appKey, secretKey, sn, ttsMode, params, listener);
        }
        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用
        AutoCheck.getInstance(context).check(initConfig, null);
        return initConfig;
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return 合成参数Map
     */
    private Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>, 其它发音人见文档
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, OfflineResource.VOICE_FEMALE);
        // 设置合成的音量，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "15");
        // 设置合成的语速，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, String.valueOf(speed));
        // 设置合成的语调，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");
        if (!isOnlineSDK) {
            // 免费的在线SDK版本没有此参数。

            /*
            params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
            // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
            // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
            // params.put(SpeechSynthesizer.PARAM_MIX_MODE_TIMEOUT, SpeechSynthesizer.PARAM_MIX_TIMEOUT_TWO_SECOND);
            // 离在线模式，强制在线优先。在线请求后超时2秒后，转为离线合成。
            */
            // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成


//
//            OfflineResource offlineResource = createOfflineResource(offlineVoice);
//            // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
//            if (offlineResource != null) {
//                params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
//                params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename());
//            }
        }
        return params;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onDestroy() {
        synthesizer.release();
    }

    private OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(context, voiceType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return offlineResource;
    }


    /**
     * 在线合成sdk，这个方法不会被调用。
     * <p>
     * 切换离线发音。注意需要添加额外的判断：引擎在合成时该方法不能调用
     */
    private void loadModel(String mode) {
        offlineVoice = mode;
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        int result = synthesizer.loadModel(offlineResource.getModelFilename(), offlineResource.getTextFilename());
    }


    public void speak(String speak, int speed) {

        if (speed != this.speed && speed > 0 && speed < 16) {
            this.speed = speed;
            synthesizer.release();
//            SpeechSynthesizerListener listener = new UiMessageListener();
            InitConfig config = getInitConfig(listener);
            synthesizer = new MySyntherizer(context, config);
        }

        int result = synthesizer.speak(speak);
        Log.d(TAG, "speak result: " + result);
    }

    public void speak(String speak) {
        speak(speak, 6);
    }


}
