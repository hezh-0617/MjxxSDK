package com.mjxx.speech.aibee;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mjxx.speechlibsnative.sdk.Config;
import com.mjxx.speechlibsnative.sdk.SpeechFragment;
import com.mjxx.speech.R;

public class TabLayout3 extends RelativeLayout {
    private SpeechFragment fragment;
    private static final String TAG = "SpeechFragment";

    public TabLayout3(Context context) {
        super(context);
        init();
    }

    public TabLayout3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabLayout3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.i(TAG,"TabLayout3 init");
        LayoutInflater.from(getContext()).inflate(R.layout.view_tab3, this, true);

        Config config = new Config();
        config.setShowLog(true);  //是否打印日志
        config.setAsrLongRecordEnable(true);
        config.setAsrServerUrl("http://182.61.15.84:8090/v2"); // 语音识别服务器地址
        config.setTtsServerUrl("http://182.61.15.84:8802/"); // 语音合成服务器地址
        config.setWebServerUrl("http://47.106.235.8:8889/#/"); // web host

        config.setWriteLog(true);
        config.setAsrSaveRecord(true);

        fragment = SpeechFragment.newInstance(config);
        fragment.setOnCloseCallListener(() -> {
            AibeeActivity.activity.backToMain();
        });
    }

    public void show() {
        Log.i(TAG,"TabLayout3 show");
        setVisibility(VISIBLE);

        FragmentManager manager = AibeeActivity.activity.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in,
                android.R.animator.fade_out);
        ft.add(R.id.speech_container, fragment, "speechFragment").addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void hide() {
        Log.i(TAG,"TabLayout3 hide");
        FragmentManager manager = AibeeActivity.activity.getSupportFragmentManager();
        manager.popBackStack();
        setVisibility(GONE);
    }
}
