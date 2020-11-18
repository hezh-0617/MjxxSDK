package com.mjxx.speechlibsnative.mjxx.sdk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.baidu.speech.asr.SpeechConstant;
import com.mjxx.speechlibsnative.R;
import com.mjxx.speechlibsnative.baidu.asr.recog.MyRecognizer;
import com.mjxx.speechlibsnative.baidu.asr.recog.RecogResult;
import com.mjxx.speechlibsnative.baidu.asr.recog.listener.IRecogListener;
import com.mjxx.speechlibsnative.baidu.tts.SpeakResultListener;
import com.mjxx.speechlibsnative.baidu.tts.TTSHelper;
import com.mjxx.speechlibsnative.mjxx.utils.LogUtil;
import com.mjxx.speechlibsnative.mjxx.webview.CustomerWebView;
import com.mjxx.speechlibsnative.mjxx.webview.WebViewCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class SpeechActivity extends AppCompatActivity {
    private CustomerWebView webView;
    private ProgressBar progressBar;
    private TextView tvLoading;

    private TTSHelper ttsHelper;
    private MyRecognizer recognizer;

    private HashMap<String, Object> asrSendParams = new HashMap<>();

    private Map<String, String> webCallbackFun = new HashMap<>();

    //    private String voidText;
    private Config config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_speech);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        tvLoading = findViewById(R.id.tvLoading);

        config = (Config) getIntent().getSerializableExtra("config");
        if (config == null) {
            config = new Config();
            config.setShowLog(false);
            LogUtil.init(false, false);
        }

//        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recognizer.start(asrSendParams);
//            }
//        });
//
//        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ttsHelper.speak("你还没告诉我你想查询什么，请说");
//            }
//        });

        if (initPermission()) {
            initWebView();
            iniSpeechSDK();
        }

    }

    private void iniSpeechSDK() {
        ttsHelper = new TTSHelper( config, new SpeakResultListener(){

            @Override
            public void onFinish(String speakWhat) {
                webView.doJSCallback(webCallbackFun.get(String.valueOf(JavaScriptInterface.API_TEXT_TO_VOICE)), speakWhat);
            }

            @Override
            public void onErr(String msg) {

            }
        });

        asrSendParams.put(SpeechConstant.PID, 1912);  //普通话
        asrSendParams.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        asrSendParams.put(SpeechConstant.APP_KEY, "com.baidu.cloud");

        if (URLUtil.isNetworkUrl(config.getAsrServerUrl())) {
            asrSendParams.put("url", config.getAsrServerUrl());
        }

        recognizer = new MyRecognizer(this, new IRecogListener() {
            @Override
            public void onAsrReady() {

            }

            @Override
            public void onAsrBegin() {

            }

            @Override
            public void onAsrEnd() {

            }

            @Override
            public void onAsrPartialResult(String[] results, RecogResult recogResult) {

            }

            @Override
            public void onAsrOnlineNluResult(String nluResult) {

            }

            @Override
            public void onAsrFinalResult(String[] results, RecogResult recogResult) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String result : results) {
                    stringBuilder.append(result);
                }
                LogUtil.d("MyRecognizer", "onAsrFinalResult=" + stringBuilder.toString());
                Map<String, String> res = new HashMap<>();
                res.put("voiceStr", stringBuilder.toString());
                webView.doJSCallback(webCallbackFun.get(String.valueOf(JavaScriptInterface.API_INIT_VOICE_2_TEXT)), res);
            }

            @Override
            public void onAsrFinish(RecogResult recogResult) {

            }

            @Override
            public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult) {

            }

            @Override
            public void onAsrLongFinish() {

            }

            @Override
            public void onAsrVolume(int volumePercent, int volume) {

            }

            @Override
            public void onAsrAudio(byte[] data, int offset, int length) {

            }

            @Override
            public void onAsrExit() {

            }

            @Override
            public void onOfflineLoaded() {

            }

            @Override
            public void onOfflineUnLoaded() {

            }
        });
    }

    private void initWebView() {

        String url = "http://47.106.235.8:8889/#/";

        webView.getWebview().getSettings().setUserAgentString(WebSettings.getDefaultUserAgent(this) + ";MJXX_SPEECH");
        final JavaScriptInterface mapClazz = new JavaScriptInterface();
        webView.addJavascriptInterface(mapClazz, "jsBridge");

        webView.setCurWebUrl(url).startCallback(new WebViewCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int curProgress) {
                progressBar.setProgress(curProgress);
            }

            @Override
            public void onError(int errorCode, String description, String failingUrl) {
                tvLoading.setText("内容加载失败");
            }

            @Override
            public void onPageFinished() {
                progressBar.setVisibility(View.GONE);
                tvLoading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvLoading.setVisibility(View.GONE);
                    }
                }, 2000);

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    final class JavaScriptInterface {

        final static int API_INIT_VOICE_2_TEXT = 5;
        final static int API_PAUSE_VOICE_TRANS = 10; //
        final static int API_RELEASE_VOICE_TRANS = 12; //

        final static int API_TEXT_TO_VOICE = 11; //
        final static int API_ON_BACK_PRESS = 13;

        @JavascriptInterface
        public void hxpApi(final int apiId, String parasJsonStr, final String callBack) {

//            LogUtil.d("jsApi", "js_api_id:" + apiId);
//            LogUtil.d("jsApi", "parasJasonStr:" + parasJsonStr);
//            LogUtil.d("jsApi", "callBack:" + callBack);

            switch (apiId) {
                case API_TEXT_TO_VOICE:
                    webCallbackFun.put(String.valueOf(apiId), callBack);
                    try {
                        JSONObject jsonObject = new JSONObject(parasJsonStr);
                        if (jsonObject.has("ctx")) {
                            final String ctx = jsonObject.getString("ctx");
                            final int speed;
                            if (jsonObject.has("speed")) {
                                speed = jsonObject.getInt("speed");
                            } else {
                                speed = 0;
                            }
                            SpeechActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (ttsHelper != null) {
                                        ttsHelper.speak(ctx, speed);
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case API_INIT_VOICE_2_TEXT:
                    webCallbackFun.put(String.valueOf(apiId), callBack);
                    recognizer.start(asrSendParams);

                    break;

                case API_PAUSE_VOICE_TRANS:
                    recognizer.stop();
                    break;

                case API_RELEASE_VOICE_TRANS:
                    recognizer.release();
                    break;

                case API_ON_BACK_PRESS:
                    onBackPressed();
                    break;
            }
        }

        @JavascriptInterface
        public void hxpApi(int apiId) {
            hxpApi(apiId, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.release();
        }
        if (ttsHelper != null) {
            ttsHelper.stopAll();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean granted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }

        if (!granted) {
            Toast.makeText(this, "获取权限失败", Toast.LENGTH_LONG).show();
            return;
        }
        initWebView();
        iniSpeechSDK();
    }

    private boolean initPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
            return false;
        } else {
            return true;
        }

    }
}
