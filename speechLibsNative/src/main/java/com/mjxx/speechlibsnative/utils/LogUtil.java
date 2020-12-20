package com.mjxx.speechlibsnative.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by topTech on 2017/8/30.
 * des:
 */

public class LogUtil {

    private static boolean isWrite = false;  // TODO: 2017/9/27 暂定不写日志
    private static boolean isOutPrint = true;

    /**
     * Log初始化
     *
     * @param isOut logcat 输出
     * @param write log文件输出  log优先
     */
    public static void init(boolean write, boolean isOut) {
        isWrite = write;
        isOutPrint = isOut;
    }


    public static void e(String tag, String msg) {
        if (tag == null) {
            return;
        }
        if (isWrite) {
            writeTraceFile(tag, "err", msg);
        }
        Trace(tag, "err", msg);
    }

    public static void v(String tag, String msg) {
        if (tag == null) {
            return;
        }
        if (isWrite) {
            writeTraceFile(tag, "verbose", msg);
        }
        Trace(tag, "verbose", msg);
    }

    public static void d(String tag, String msg) {
        if (tag == null) {
            return;
        }
        if (isWrite) {
            writeTraceFile(tag, "debug", msg);
        }
        Trace(tag, "debug", msg);
    }

    public static void i(String tag, String msg) {
        if (tag == null) {
            return;
        }
        if (isWrite) {
            writeTraceFile(tag, "info", msg);
        }
        Trace(tag, "info", msg);
    }

    public static void w(String tag, String msg) {
        if (tag == null) {
            return;
        }
        if (isWrite) {
            writeTraceFile(tag, "warm", msg);
        }
        Trace(tag, "warm", msg);
    }

    private static void Trace(String tag, String label, String msg) {
        String data = "File[" + _FILE_() + "]Line[" + _LINE_() + "]FUN[" + _FUNC_() + "]," + label + " msg: " + msg;
        if (isOutPrint) {
            if ("err".equals(label)) {
                Log.e(tag, "[" + tag + "]," + data);
            } else if ("verbose".equals(label)) {
                Log.v(tag, "[" + tag + "]," + data);
            } else if ("debug".equals(label)) {
                Log.d(tag, "[" + tag + "]," + data);
            } else if ("info".equals(label)) {
                Log.i(tag, "[" + tag + "]," + data);
            } else if ("warm".equals(label)) {
                Log.w(tag, "[" + tag + "]," + data);
            }
        }
    }

    /*
     * private static void Trace(String tag,String msg) { String data = "File["+
     * _FILE_() +"]Line[" + _LINE_() + "]FUN[" + _FUNC_() + "]," +" msg: " +
     * msg; if(isOutPrint) { Log.e(TAG,"[" + tag + "],"+ data); } //Log.e(tag,
     * data); }
     */
    private static void writeTraceFile(String tag, String label, String msg) {
        String data = "File[" + _FILE_()  + "]Time[" + _TIME_() + "]," + label
                + " msg: " + msg;
        String path = getSDPath();
        String dir_path = path + "/" + "SpeechSdkLogs";
        if (checkTraceDir(dir_path)) {
            String traceFilePath = dir_path + "/" + tag + ".log";
            if (checkTraceFile(traceFilePath)) {
                FileWriter fw = null;
                try {
                    fw = new FileWriter(traceFilePath, true);
                    fw.append(data);
                    fw.append("\r\n");
                    fw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fw != null) {
                            fw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取sd卡路径
     *
     * @return String
     * @throws throws
     */
    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        } else {
            return "";
        }
    }

    //当前文件名
    private static String _FILE_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[3];
        return traceElement.getFileName();
    }

    // 当前方法名
    private static String _FUNC_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[3];
        return traceElement.getMethodName();
    }

    // 当前行号
    private static int _LINE_() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[3];
        return traceElement.getLineNumber();
    }

    // 当前时间
    @SuppressLint("SimpleDateFormat")
    private static String _TIME_() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(now);
    }

    private static boolean checkTraceDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        } else if (!dir.isDirectory()) {
            return false;
        }

        return true;
    }

    private static boolean checkTraceFile(String path) {
        File mFile = new File(path);
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else if (!mFile.isFile()) {
            return false;
        }
        return true;
    }

    /**
     * 打印错误信息
     *
     * @param e
     */
    public static void printException(Exception e) {
        if (isOutPrint) {
            e.printStackTrace();
        }
    }

}
