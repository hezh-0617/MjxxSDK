package com.mjxx.speechlibsnative.baidu.tts;

import android.media.MediaPlayer;

public class MediaPlayerManager {

    private static volatile MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    public MediaPlayerManagerInterface mediaPlayerManagerInterface;

    public static MediaPlayerManager getInstance() {
        if (null == instance) {
            synchronized (MediaPlayerManager.class) {
                if (null == instance) {
                    instance = new MediaPlayerManager();
                }
            }
        }
        return instance;

    }

    public void mDestory() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    public void mPlay(String paramString) {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();

        try {
            this.mediaPlayer.setDataSource(paramString);
            this.mediaPlayer.prepareAsync();
            this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayerManagerInterface != null) {
                        mediaPlayerManagerInterface.onComplete();
                    }
                }
            });
            this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaPlayerManagerInterface(MediaPlayerManagerInterface paramMediaPlayerManagerInterface) {
        this.mediaPlayerManagerInterface = paramMediaPlayerManagerInterface;
    }
}
