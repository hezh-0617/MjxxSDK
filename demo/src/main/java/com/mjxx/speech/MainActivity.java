package com.mjxx.speech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mjxx.speechlibsnative.sdk.Config;
import com.mjxx.speechlibsnative.sdk.SpeechSDK;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText etAsrServerUrl= findViewById(R.id.etAsrServerUrl);
        final EditText etTtsServerUrl= findViewById(R.id.etTtsServerUrl);
        final EditText etWebServerUrl= findViewById(R.id.etWebServerUrl);
        final EditText etAsrPid= findViewById(R.id.etAsrPid);

        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config config = new Config();
                config.setShowLog(true);  //是否打印日志
                //以下参数由服务方提供：
                config.setAsrServerUrl(etAsrServerUrl.getText().toString()); // 语音识别服务器地址
                config.setAsrPid(Integer.parseInt(etAsrPid.getText().toString()));  //选填，默认888
                config.setTtsServerUrl(etTtsServerUrl.getText().toString());  // 语音合成服务器地址
                config.setWebServerUrl(etWebServerUrl.getText().toString()); //web host
                SpeechSDK.startSpeech(v.getContext(), config);
            }
        });
    }
}