package com.mjxx.speech;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mjxx.speech.utils.AssetsUtils;
import com.mjxx.speech.widget.CircleView;
import com.mjxx.speechlibsnative.sdk.Config;
import com.mjxx.speechlibsnative.sdk.SpeechSDK;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES_NAME = "speech";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.preferences();

//        View cv_start = findViewById(R.id.cv_start);
        View iv_start = findViewById(R.id.iv_start);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_voice);
//        cv_start.setAnimation(animation);
        iv_start.setAnimation(animation);
        animation.start();

        findViewById(R.id.iv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        findViewById(R.id.iv_background).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                alert();

                return true;
            }
        });
    }

    private void start() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Config config = new Config();
        config.setShowLog(true);  //是否打印日志
        config.setWriteLog(true); //是否保存日志文件，true则输出日志到 手机储存/SpeechSdkLogs/

        //以下参数由服务方提供：
        config.setAsrServerUrl(preferences.getString("AsrServerUrl", null)); // 语音识别服务器地址
        String pid = preferences.getString("AsrPid", "888");
        config.setAsrPid(Integer.parseInt(pid));  //选填，默认888
        config.setAsrLongRecordEnable(true);  //asr收否支持长时间录音
        config.setAsrSaveRecord(true);  //是否保存asr录音文件，true则保存到 手机储存/MUSIC/baidu_asr/

        config.setTtsServerUrl(preferences.getString("TtsServerUrl", null));  // 语音合成服务器地址
        config.setWebServerUrl(preferences.getString("WebServerUrl", null)); //web host
        config.setRemoteServerHost(preferences.getString("RemoteServer", null)); //如需代理，请配置我放业务服务器Host
        SpeechSDK.startSpeech(this, config);
    }

    private void preferences() {

        List<String> keys = new ArrayList<>();
        keys.add("AsrServerUrl");
        keys.add("TtsServerUrl");
        keys.add("WebServerUrl");
        keys.add("AsrPid");
        keys.add("RemoteServer");

        String json = null;
        if (BuildConfig.DEBUG) {
            json = AssetsUtils.assets(this, "config_debug.json");
        } else {
            json = AssetsUtils.assets(this, "config_release.json");
        }

        JsonObject config = JsonParser.parseString(json).getAsJsonObject();

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //设置默认值
        for (String key : keys) {
            if (!preferences.contains(key)) {
                editor.putString(key, config.get(key).getAsString());
            }
        }

        editor.apply();
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(params);
        builder.setView(editText);
        builder.setTitle("请输入认证密码");
        builder.setNegativeButton("取消", null).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    return;
                }

                if (TextUtils.equals("123456", text)) {
                    toSetting();
                }
            }
        });

        if (BuildConfig.DEBUG) {
            editText.setText("123456");
        }

        builder.show();
    }

    private void toSetting() {
        startActivity(new Intent(this, SettingActivity.class));
    }

}