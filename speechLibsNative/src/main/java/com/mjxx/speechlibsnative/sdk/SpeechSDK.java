package com.mjxx.speechlibsnative.sdk;

import android.content.Context;
import android.content.Intent;

import com.mjxx.speechlibsnative.utils.LogUtil;


public class SpeechSDK {

    public static void startSpeech(Context context,Config config){

        if (config != null) {
            LogUtil.init(false,config.isShowLog());
        }

        context.startActivity(new Intent(context, SpeechActivity.class).putExtra("config",config));
    }
}
