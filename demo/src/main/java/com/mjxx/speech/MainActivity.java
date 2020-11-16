package com.mjxx.speech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.mjxx.sdk.Config;
import com.mjxx.sdk.SpeechSDK;


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
//                config.setServerUrl("");
                SpeechSDK.startSpeech(v.getContext(), config);
            }
        });
    }
}