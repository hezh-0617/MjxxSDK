package com.mjxx.speech.aibee;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.mjxx.speech.R;

import androidx.appcompat.app.AppCompatActivity;

public class AibeeActivity extends AppCompatActivity {
    public static AibeeActivity activity;

    private LinearLayout tabsLayout;
    private TabLayout1 tabLayout1;
    private TabLayout2 tabLayout2;
    private TabLayout3 tabLayout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_tabs);

        tabLayout1 = new TabLayout1(this);
        addContentView(tabLayout1,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tabLayout2 = new TabLayout2(this);
        addContentView(tabLayout2,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tabLayout3 = new TabLayout3(this);
        addContentView(tabLayout3,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        tabsLayout = (LinearLayout) View.inflate(this, R.layout.view_tabs, null);
        addContentView(tabsLayout,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        tabsLayout.findViewById(R.id.tab1).setOnClickListener(v -> {
            backToMain();
        });

        tabsLayout.findViewById(R.id.tab2).setOnClickListener(v -> {
            tabLayout1.hide();
            tabLayout2.show();
            tabLayout3.hide();
            tabsLayout.setVisibility(View.VISIBLE);
        });

        tabsLayout.findViewById(R.id.tab3).setOnClickListener(v -> {
            tabLayout1.hide();
            tabLayout2.hide();
            tabLayout3.show();
            tabsLayout.setVisibility(View.GONE);
        });

        backToMain();
    }

    public void backToMain() {
        tabLayout1.show();
        tabLayout2.hide();
        tabLayout3.hide();
        tabsLayout.setVisibility(View.VISIBLE);
    }
}
