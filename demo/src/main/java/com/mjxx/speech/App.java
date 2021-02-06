package com.mjxx.speech;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author e
 * @datetime 2021/2/6 12:15 PM
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(this, "5c8bf634c6", BuildConfig.DEBUG);

    }
}
