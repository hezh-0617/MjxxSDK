package com.mjxx.speechlibsnative.baidu.tts;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.mjxx.speechlibsnative.mjxx.sdk.Config;
import com.mjxx.speechlibsnative.mjxx.utils.DeviceUtil;
import com.mjxx.speechlibsnative.mjxx.utils.LogUtil;
import com.mjxx.speechlibsnative.retrofit.RetrofitClient;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class TTSHelper {

    private static final String TAG = "TTSHelper";
    private Config config;
    private SpeakResultListener listener;

    public TTSHelper(Config config, SpeakResultListener listener) {
        this.config = config;
        this.listener = listener;
    }

    public void speak(String result) {
        speak(result, 5);
    }

    public void speak(final String result, int speed) {

        if (TextUtils.isEmpty(result)) {
            stopAll();
            return;
        }

        String ttsServerUrl = config.getTtsServerUrl();
        if (!ttsServerUrl.endsWith("/")) {
            ttsServerUrl+="/";
        }
        try {
            RetrofitClient client = new RetrofitClient(ttsServerUrl);
            Call<ResponseBody> call = client.getRetrofitService().text2audio(
                    result,
                    "zh",
                    993,
                    1,
                    DeviceUtil.getNewMac(),
                    speed,
                    5,
                    10,
                    5117);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        InputStream inputStream = response.body().byteStream();
                        String audioPath = getAudioPath(inputStream);
                        MediaPlayerManager.getInstance().setMediaPlayerManagerInterface(new MediaPlayerManagerInterface() {
                            @Override
                            public void onComplete() {
                                stopAll();
                                listener.onFinish(result);
                            }
                        });
                        MediaPlayerManager.getInstance().mPlay(audioPath);
                    } else {
                        listener.onErr("response false");
                        LogUtil.e(TAG, "response false" );
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    LogUtil.e("tag", t.getMessage());
                    listener.onErr(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            stopAll();
            listener.onErr(e.getMessage());
        }
    }

    private String getAudioPath(InputStream inputStream) {

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String ttsPath = path + "/" + System.currentTimeMillis() + ".wav";
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(ttsPath);
            byte[] buffer = new byte[1024];
            int byteread = 0;
            while ((byteread = inputStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            fs.flush();
            fs.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
            listener.onErr(e.getMessage());
        }

        return ttsPath;

    }

    public void stopAll() {
        MediaPlayerManager.getInstance().mDestory();
    }


}
