package com.mjxx.speechlibsnative.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.baidu.android.common.util.BuildConfig;
import com.baidu.speech.asr.SpeechConstant;
import com.mjxx.speech.R;
import com.mjxx.speechlibsnative.baidu.asr.recog.MyRecognizer;
import com.mjxx.speechlibsnative.baidu.asr.recog.RecogResult;
import com.mjxx.speechlibsnative.baidu.asr.recog.listener.IRecogListener;
import com.mjxx.speechlibsnative.baidu.tts.SpeakResultListener;
import com.mjxx.speechlibsnative.baidu.tts.TTSHelper;
import com.mjxx.speechlibsnative.utils.DeviceUtil;
import com.mjxx.speechlibsnative.utils.FileUtil;
import com.mjxx.speechlibsnative.utils.LogUtil;
import com.mjxx.speechlibsnative.webview.CustomerWebView;
import com.mjxx.speechlibsnative.webview.WebViewCallback;
//import com.mjxx.speechlibsnative.BuildConfig;
//import com.mjxx.speechlibsnative.R;
//import com.mjxx.speechlibsnative.baidu.asr.recog.MyRecognizer;
//import com.mjxx.speechlibsnative.baidu.asr.recog.RecogResult;
//import com.mjxx.speechlibsnative.baidu.asr.recog.listener.IRecogListener;
//import com.mjxx.speechlibsnative.baidu.tts.SpeakResultListener;
//import com.mjxx.speechlibsnative.baidu.tts.TTSHelper;
//import com.mjxx.speechlibsnative.utils.DeviceUtil;
//import com.mjxx.speechlibsnative.utils.FileUtil;
//import com.mjxx.speechlibsnative.utils.LogUtil;
//import com.mjxx.speechlibsnative.webview.CustomerWebView;
//import com.mjxx.speechlibsnative.webview.WebViewCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public final class SpeechFragment extends Fragment {

    private static final String TAG = SpeechFragment.class.getSimpleName();

    private CustomerWebView webView;
    private ProgressBar progressBar;
    private TextView tvLoading;
    private RelativeLayout rlNv;

    private static final String ASR_RECEIVE_MODE_PARTIAL = "Partial";
    private static final String ASR_RECEIVE_MODE_FINAL = "Final";

    private String asrReceiveMode = ASR_RECEIVE_MODE_FINAL;


    private TTSHelper ttsHelper;
    private MyRecognizer recognizer;
    private IRecogListener recogListener;

    private HashMap<String, Object> asrSendParams = new HashMap<>();

    private Map<String, String> webCallbackFun = new HashMap<>();

    private Config config;

    private static boolean canLogInWeb = true;

    public static SpeechFragment newInstance(Config config) {
        Bundle args = new Bundle();
        args.putSerializable("config", config);

        SpeechFragment fragment = new SpeechFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_speech, container, false);
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);
        tvLoading = view.findViewById(R.id.tvLoading);
        rlNv = view.findViewById(R.id.rlNv);

        Log.i(TAG, "onCreateView");
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new NullPointerException("Config 不能为空");
        }

        config = (Config) arguments.getSerializable("config");
        if (config == null) {
            throw new NullPointerException("Config 不能为空");
        }

        if (initPermission(getActivity())) {
            Log.i(TAG, "has Permission,init sdk!");
            initWebView();
            iniSpeechSDK();
            LogUtil.writeTraceFile("SpeechSDK", "init", config.toString());
        } else {
            Log.i(TAG, "do not get Permission");
        }

        view.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (onCloseCallListener != null) {
//                    onCloseCallListener.onCloseCall();
//                }
                onCloseCall();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }

    private void iniSpeechSDK() {
        ttsHelper = new TTSHelper(config, new SpeakResultListener() {

            @Override
            public void onFinish(String speakWhat) {
                webView.doJSCallback(webCallbackFun.get(String.valueOf(JavaScriptInterface.API_TEXT_TO_VOICE)), speakWhat);
            }

            @Override
            public void onErr(String msg) {
                webView.doJSCallback(webCallbackFun.get(String.valueOf(JavaScriptInterface.API_TEXT_TO_VOICE)), "error");
            }
        });

        asrSendParams.put(SpeechConstant.PID, config.getAsrPid());
        asrSendParams.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        asrSendParams.put(SpeechConstant.APP_KEY, "com.baidu.cloud");

        asrSendParams.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);
        if (config.isAsrSaveRecord()) {
            asrSendParams.put(SpeechConstant.OUT_FILE, FileUtil.getAsrCachePath());
        }
        if (config.isWriteLog()) {
            asrSendParams.put(SpeechConstant.LOG_LEVEL, 6);
        } else if (config.isShowLog()) {
            asrSendParams.put(SpeechConstant.LOG_LEVEL, 5);
        } else {
            asrSendParams.put(SpeechConstant.LOG_LEVEL, 0);
        }
        if (config.isAsrLongRecordEnable()) {
            asrSendParams.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        } else {
            asrSendParams.remove(SpeechConstant.VAD_ENDPOINT_TIMEOUT);
        }

        if (URLUtil.isNetworkUrl(config.getAsrServerUrl())) {
            asrSendParams.put("url", config.getAsrServerUrl());
        } else {
            throw new IllegalArgumentException("asrServerUrl 非法");
        }

        recogListener = new IRecogListener() {
            @Override
            public void onLogMessage(String logMessage) {
            }

            @Override
            public void onAsrReady() {
                LogUtil.d("MyRecognizer", "onAsrReady: ");
                setWebLog("onAsrReady");
            }

            @Override
            public void onAsrBegin() {
                LogUtil.d("MyRecognizer", "onAsrBegin: ");
                setWebLog("onAsrBegin");
            }

            @Override
            public void onAsrEnd() {
                LogUtil.d("MyRecognizer", "onAsrEnd: ");
                setWebLog("onAsrEnd");
            }

            @Override
            public void onAsrPartialResult(String[] results, RecogResult recogResult) {
                if (ASR_RECEIVE_MODE_PARTIAL.equals(asrReceiveMode)) {
                    onAsrResult(results);
                }
            }

            @Override
            public void onAsrOnlineNluResult(String nluResult) {
                LogUtil.d("MyRecognizer", "onAsrOnlineNluResult: ");
                setWebLog("onAsrOnlineNluResult");

            }

            @Override
            public void onAsrFinalResult(String[] results, RecogResult recogResult) {
                if (ASR_RECEIVE_MODE_FINAL.equals(asrReceiveMode)) {
                    onAsrResult(results);
                }
            }

            @Override
            public void onAsrFinish(RecogResult recogResult) {
                LogUtil.d("MyRecognizer", "onAsrFinish: ");
                setWebLog("onAsrFinish");
            }

            @Override
            public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult) {
                String msg = "onAsrFinishError: " + errorCode + ",subErrorCode:" + subErrorCode + ",descMessage:" + descMessage;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("errorCode", 1);
                    jsonObject.put("voiceStr", "");
                    webView.doJSCallback(webCallbackFun.get(String.valueOf(JavaScriptInterface.API_INIT_VOICE_2_TEXT)), jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.d("MyRecognizer", msg);
                setWebLog(msg);
            }

            @Override
            public void onAsrLongFinish() {
                LogUtil.d("MyRecognizer", "onAsrLongFinish: ");
                setWebLog("onAsrLongFinish");
            }

            @Override
            public void onAsrVolume(int volumePercent, int volume) {
                LogUtil.d("MyRecognizer", "onAsrVolume: ");
                setWebLog("onAsrVolume:" + volume);
            }

            @Override
            public void onAsrAudio(byte[] data, int offset, int length) {
                LogUtil.d("MyRecognizer", "onAsrAudio: ");
                if (canLogInWeb) {
                    setWebLog("正在录音：" + (data == null ? 0 : data.length));
                    canLogInWeb = false;
                    new Timer().schedule(new TimerTask() { // 1秒不重复显示
                        public void run() {
                            canLogInWeb = true;
                        }
                    }, 1000);
                }
            }

            @Override
            public void onAsrExit() {
                LogUtil.d("MyRecognizer", "onAsrExit: ");
                setWebLog("onAsrExit");
            }

            @Override
            public void onOfflineLoaded() {
                LogUtil.d("MyRecognizer", "onOfflineLoaded: ");
            }

            @Override
            public void onOfflineUnLoaded() {
                LogUtil.d("MyRecognizer", "onOfflineUnLoaded: ");
            }
        };
        recognizer = new MyRecognizer(getContext(), recogListener);
    }

    private void setWebLog(String logMsg) {
        webView.evaluateJavascript("window.appInfo['_0']['setLog'](\'" + logMsg + "<br/>\')", null);
    }

    private void onAsrResult(String[] results) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String result : results) {
            stringBuilder.append(result);
        }
        LogUtil.d("MyRecognizer", "onAsrFinalResult=" + stringBuilder.toString());
        setWebLog("onAsrFinalResult=" + stringBuilder.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("errorCode", 0);
            jsonObject.put("voiceStr", stringBuilder.toString());
            webView.doJSCallback(webCallbackFun.get(String.valueOf(JavaScriptInterface.API_INIT_VOICE_2_TEXT)), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean loadFail = false;

    private void initWebView() {

        String url = config.getWebServerUrl();

        if (!URLUtil.isNetworkUrl(url)) {
            throw new IllegalArgumentException("WebServerUrl 非法");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getWebview().getSettings().setUserAgentString(WebSettings.getDefaultUserAgent(getContext()) + ";MJXX_SPEECH");
        }
        final JavaScriptInterface mapClazz = new JavaScriptInterface();
        webView.addJavascriptInterface(mapClazz, "jsBridge");


        webView.setCurWebUrl(url).startCallback(new WebViewCallback() {
            @Override
            public void onStart() {
                loadFail = false;
            }

            @Override
            public void onProgress(int curProgress) {
                progressBar.setProgress(curProgress);
            }

            @Override
            public void onError(int errorCode, String description, String failingUrl) {
                loadFail = true;
                tvLoading.setText("内容加载失败");
                webView.setVisibility(View.GONE);
                rlNv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished() {
                progressBar.setVisibility(View.GONE);
                if (!loadFail) {
                    rlNv.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    tvLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            onCloseCall();
        }
    }

    private void onCloseCall() {

        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            fm.beginTransaction().remove(this).commitAllowingStateLoss();
        }

        if (onCloseCallListener != null) {
            onCloseCallListener.onCloseCall();
        }

    }

    final class JavaScriptInterface {
        final static int API_GET_DEVICE_INFO = 1;

        final static int API_INIT_VOICE_2_TEXT = 5;
        final static int API_PAUSE_VOICE_TRANS = 10; //
        final static int API_RELEASE_VOICE_TRANS = 12; //

        final static int API_TEXT_TO_VOICE = 11; //
        final static int API_ON_BACK_PRESS = 13;
        final static int API_ON_GET_SERVER_HOST = 14;
        final static int API_GET_SDK_VERSION = 15;

        @JavascriptInterface
        public void hxpApi(final int apiId, String parasJsonStr, final String callBack) {

            LogUtil.d("jsApi", "js_api_id:" + apiId);
            LogUtil.d("jsApi", "parasJasonStr:" + parasJsonStr);
            LogUtil.d("jsApi", "callBack:" + callBack);

            switch (apiId) {
                case API_GET_DEVICE_INFO:
                    Activity activity = getActivity();
                    if (activity == null) {
                        break;
                    }
                    try {
                        JSONObject result = new JSONObject();
                        result.put("imei", DeviceUtil.getIMEI(activity));
                        result.put("network", DeviceUtil.isNetworkAvailable(activity) ? 1 : 0);
                        result.put("ramSize", DeviceUtil.getRamSize(activity));
                        result.put("availableRamSize", DeviceUtil.getAvailableRamSize(activity));
                        result.put("cpuUsed", DeviceUtil.getAppCpuUseRate());
                        webView.doJSCallback(callBack, result.toString());
                    } catch (Exception e) {
                        webView.doJSCallback(callBack, "{}");
                    }
                    break;

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
                            webView.post(new Runnable() {
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

                    try {
                        JSONObject jsonObject = new JSONObject(parasJsonStr);
                        if (jsonObject.has("receiveMode")) {
                            asrReceiveMode = jsonObject.getString("receiveMode");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (recognizer == null) {
                        recognizer = new MyRecognizer(getContext(), recogListener);
                    }
                    try {
                        recognizer.start(asrSendParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case API_PAUSE_VOICE_TRANS:
                    //Log.i(TAG,"API_PAUSE_VOICE_TRANS");
                    recognizer.stop();
                    break;

                case API_RELEASE_VOICE_TRANS:
                    Log.i(TAG, "API_RELEASE_VOICE_TRANS");
                    recognizer.release();
                    break;

                case API_ON_BACK_PRESS:
                    Log.i(TAG, "API_ON_BACK_PRESS");
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                    break;

                case API_ON_GET_SERVER_HOST:
                    try {
                        JSONObject result = new JSONObject();
                        result.put("serverHost", config.getRemoteServerHost());
                        webView.doJSCallback(callBack, result.toString());
                    } catch (Exception e) {
                        webView.doJSCallback(callBack, "err");
                    }
                    break;
                case API_GET_SDK_VERSION:
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("sdkVersion", BuildConfig.VERSION_NAME);
                        webView.doJSCallback(callBack, jsonObject.toString());
                    } catch (JSONException e) {
                        webView.doJSCallback(callBack, "err");
                    }

                    break;
            }
        }

        @JavascriptInterface
        public void hxpApi(int apiId) {
            hxpApi(apiId, null, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        webView.onDestroy();
        if (recognizer != null) {
            recognizer.release();
            recognizer = null;
            Log.i(TAG, "onDestroy recognizer.release()");
        }
        if (ttsHelper != null) {
            ttsHelper.stopAll();
            Log.i(TAG, "onDestroy ttsHelper.stopAll()");
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
            Toast.makeText(getContext(), "获取权限失败", Toast.LENGTH_LONG).show();
            return;
        }
        initWebView();
        iniSpeechSDK();
        Log.i(TAG, "onRequestPermissionsResult PERMISSION_GRANTED");
        LogUtil.writeTraceFile("SpeechSDK", "init", config.toString());
    }

    private boolean initPermission(Activity activity) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
//            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 123);
            this.requestPermissions(toApplyList.toArray(tmpList), 123);
            return false;
        } else {
            return true;
        }

    }

    public interface OnCloseCallListener {
        void onCloseCall();
    }

    private OnCloseCallListener onCloseCallListener;

    public void setOnCloseCallListener(OnCloseCallListener onCloseCallListener) {
        this.onCloseCallListener = onCloseCallListener;
    }
}
