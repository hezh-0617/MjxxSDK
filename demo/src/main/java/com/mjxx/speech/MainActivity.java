package com.mjxx.speech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mjxx.speech.utils.AssetsUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String Shared_Preferences_Name = "speech";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.preferences();





    }

    private void preferences(){

        List<String> keys = new ArrayList<>();
        keys.add("AsrServerUrl");
        keys.add("TtsServerUrl");
        keys.add("WebServerUrl");
        keys.add("AsrPid");
        keys.add("RemoteServer");

        String json = null;
        if (BuildConfig.DEBUG){
            json = AssetsUtils.assets(this,"config_debug.json");
        }else {
            json = AssetsUtils.assets(this,"config_release.json");
        }

        JsonObject config = JsonParser.parseString(json).getAsJsonObject();

        SharedPreferences preferences = getSharedPreferences(Shared_Preferences_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //设置默认值
        for (String key : keys) {
            if (!preferences.contains(key)) {
                editor.putString(key,config.get(key).toString());
            }
        }

        editor.apply();
    }

}