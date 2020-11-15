package com.mjxx.sdk;

import android.content.Context;
import android.content.Intent;

public class SpeechSDK {

    public static void startSpeech(Context context){
        context.startActivity(new Intent(context,SpeechActivity.class));
    }
}
