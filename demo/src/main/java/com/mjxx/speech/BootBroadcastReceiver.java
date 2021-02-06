package com.mjxx.speech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 开机自启动
 *
 * @author e
 * @datetime 2021/2/6 7:26 PM
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(Intent.ACTION_BOOT_COMPLETED, intent.getAction())) {
            Intent boot = new Intent(context, MainActivity.class);
            boot.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }
}
