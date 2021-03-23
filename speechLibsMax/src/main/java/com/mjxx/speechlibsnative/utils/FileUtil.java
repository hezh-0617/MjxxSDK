package com.mjxx.speechlibsnative.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class FileUtil {

    public static String getAsrCachePath() {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString();
        File myDir = new File(root + "/baidu_asr");
        myDir.mkdirs();
        File[] files = myDir.listFiles();
        if (files != null && files.length == 500) {

            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    long diff = f1.lastModified() - f2.lastModified();
                    if (diff > 0)
                        return 1;
                    else if (diff == 0)
                        return 0;
                    else
                        return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
                }
                public boolean equals(Object obj) {
                    return true;
                }

            });
            files[0].delete();
        }
        String fName = "audio-" + nowTimeString() + ".pcm";
        File file = new File(myDir, fName);

        if (file.exists())
            file.delete();
        return file.getPath();
    }

    private static String nowTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
        return formatter.format(System.currentTimeMillis());
    }
}
