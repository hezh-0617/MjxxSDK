package com.mjxx.speechlibsnative.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by topTech on 2019/5/7.
 * des:
 */
public class InnerWebView extends WebView {
    public InnerWebView(Context context) {
        super(context);
    }

    public InnerWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//        Log.d("onOverScrolled", "scrollX: "+scrollX +" scrollY: "+scrollY +" clampedX: "+clampedX +" clampedY: "+clampedY );
        if (scrollY == 0 && clampedY && onScrollTopListener != null) {
            onScrollTopListener.onScrollTop();
        }

    }

    public interface OnScrollTopListener{
        void onScrollTop();
    }
    private OnScrollTopListener onScrollTopListener;

    public void setOnScrollTopListener(OnScrollTopListener onScrollTopListener) {
        this.onScrollTopListener = onScrollTopListener;
    }
}
