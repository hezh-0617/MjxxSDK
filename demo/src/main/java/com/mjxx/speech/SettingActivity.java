package com.mjxx.speech;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mjxx.speech.utils.AssetsUtils;

import java.util.Set;

/**
 * 设置
 *
 * @author e
 * @datetime 2021/2/5 11:54 PM
 */
public class SettingActivity extends AppCompatActivity {

    private ArrayMap<String, EditText> mEditTextMap = new ArrayMap<>(6);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        setTitle("设置");

        final EditText etAsrServerUrl = findViewById(R.id.etAsrServerUrl);
        final EditText etTtsServerUrl = findViewById(R.id.etTtsServerUrl);
        final EditText etWebServerUrl = findViewById(R.id.etWebServerUrl);
        final EditText etAsrPid = findViewById(R.id.etAsrPid);
        final EditText etRemoteServerHost = findViewById(R.id.etRemoteServerHost);

        mEditTextMap.put("AsrServerUrl", etAsrServerUrl);
        mEditTextMap.put("TtsServerUrl", etTtsServerUrl);
        mEditTextMap.put("WebServerUrl", etWebServerUrl);
        mEditTextMap.put("AsrPid", etAsrPid);
        mEditTextMap.put("RemoteServer", etRemoteServerHost);

        final SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> set = mEditTextMap.keySet();
        for (String key : set) {
            String value = preferences.getString(key, "");
            EditText editText = mEditTextMap.get(key);
            assert editText != null;
            editText.setText(value);
        }

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Config config = new Config();
//                config.setShowLog(true);  //是否打印日志
//                config.setWriteLog(true); //是否保存日志文件，true则输出日志到 手机储存/SpeechSdkLogs/
//
//                //以下参数由服务方提供：
//                config.setAsrServerUrl(etAsrServerUrl.getText().toString()); // 语音识别服务器地址
//                config.setAsrPid(Integer.parseInt(etAsrPid.getText().toString()));  //选填，默认888
//                config.setAsrLongRecordEnable(true);  //asr收否支持长时间录音
//                config.setAsrSaveRecord(true);  //是否保存asr录音文件，true则保存到 手机储存/MUSIC/baidu_asr/
//
//                config.setTtsServerUrl(etTtsServerUrl.getText().toString());  // 语音合成服务器地址
//                config.setWebServerUrl(etWebServerUrl.getText().toString()); //web host
//                config.setRemoteServerHost(etRemoteServerHost.getText().toString()); //如需代理，请配置我放业务服务器Host

                SharedPreferences.Editor editor = preferences.edit();
                Set<String> set = mEditTextMap.keySet();
                for (String key : set) {
                    EditText editText = mEditTextMap.get(key);
                    assert editText != null;
                    String text = editText.getText().toString();
                    editor.putString(key, text);
                }

                editor.apply();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_release:
                config("config_release.json");

                return true;
            case R.id.menu_debug:
                config("config_debug.json");

            case R.id.menu_proxy:
                config("config_proxy.json");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void config(String name) {
        String json = AssetsUtils.assets(this, name);
        JsonObject config = JsonParser.parseString(json).getAsJsonObject();

        Set<String> set = mEditTextMap.keySet();
        for (String key : set) {
            String value = config.get(key).getAsString();
            EditText editText = mEditTextMap.get(key);
            assert editText != null;
            editText.setText(value);
        }

    }

}
