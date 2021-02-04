package com.mjxx.speech.aibee;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import com.mjxx.speech.R;

public class TabLayout2 extends RelativeLayout {
    public TabLayout2(Context context) {
        super(context);
        init();
    }

    public TabLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_tab2, this, true);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }
}
