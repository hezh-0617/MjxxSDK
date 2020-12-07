package com.mjxx.speech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.mjxx.speechlibsnative.sdk.Config;
import com.mjxx.speechlibsnative.sdk.SpeechSDK;

//import com.mjxx.sdk.Config;
//import com.mjxx.sdk.SpeechSDK;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config config = new Config();
                config.setShowLog(true);  //是否打印日志
                config.setAsrServerUrl("http://182.61.15.84:8090/v2");
                config.setTtsServerUrl("http://182.61.15.84:8802");
                config.setWebServerUrl("http://47.106.235.8:8889/#/");
                SpeechSDK.startSpeech(v.getContext(), config);
            }
        });
    }
}