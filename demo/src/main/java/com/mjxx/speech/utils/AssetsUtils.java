package com.mjxx.speech.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author e
 * @datetime 2021/2/6 12:13 AM
 */
public final class AssetsUtils {

    public static String assets(Context context, String name) {
        try {
            InputStream is = context.getApplicationContext().getAssets().open(name);
            return string(is);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public static String string(File file) throws IOException {
        return string(new FileInputStream(file));
    }

    public static String string(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        char[] buf = new char[1024];
        int count;
        while ((count = reader.read(buf)) != -1) {
            sb.append(buf, 0, count);
        }
        is.close();
        return sb.toString();
    }
}
