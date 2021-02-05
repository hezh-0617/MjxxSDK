package com.mjxx.speech;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mjxx.speechlibsnative.sdk.Config;
import com.mjxx.speechlibsnative.sdk.SpeechSDK;

/**
 * @author e
 * @datetime 2021/2/5 11:54 PM
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        final EditText etAsrServerUrl= findViewById(R.id.etAsrServerUrl);
        final EditText etTtsServerUrl= findViewById(R.id.etTtsServerUrl);
        final EditText etWebServerUrl= findViewById(R.id.etWebServerUrl);
        final EditText etAsrPid= findViewById(R.id.etAsrPid);
        final EditText etRemoteServerHost= findViewById(R.id.etRemoteServerHost);

        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config config = new Config();
                config.setShowLog(true);  //是否打印日志
                config.setWriteLog(true); //是否保存日志文件，true则输出日志到 手机储存/SpeechSdkLogs/

                //以下参数由服务方提供：
                config.setAsrServerUrl(etAsrServerUrl.getText().toString()); // 语音识别服务器地址
                config.setAsrPid(Integer.parseInt(etAsrPid.getText().toString()));  //选填，默认888
                config.setAsrLongRecordEnable(true);  //asr收否支持长时间录音
                config.setAsrSaveRecord(true);  //是否保存asr录音文件，true则保存到 手机储存/MUSIC/baidu_asr/

                config.setTtsServerUrl(etTtsServerUrl.getText().toString());  // 语音合成服务器地址
                config.setWebServerUrl(etWebServerUrl.getText().toString()); //web host
                config.setRemoteServerHost(etRemoteServerHost.getText().toString()); //如需代理，请配置我放业务服务器Host
                SpeechSDK.startSpeech(v.getContext(), config);
            }
        });
    }
}
