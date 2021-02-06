package com.mjxx.speechlibsnative.sdk;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mjxx.speechlibsnative.R;

public class SpeechActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SpeechActivity", "onCreate");
        setContentView(R.layout.activity_speech);
        Config config = (Config) getIntent().getSerializableExtra("config");
        if (config == null) {
            throw new NullPointerException("config 不能为空");
        }
        SpeechFragment fragment = SpeechFragment.newInstance(config);
        fragment.setOnCloseCallListener(new SpeechFragment.OnCloseCallListener() {
            @Override
            public void onCloseCall() {
                Log.i("SpeechActivity", "onCloseCall");
                finish();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("SpeechActivity", "onDestroy");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
