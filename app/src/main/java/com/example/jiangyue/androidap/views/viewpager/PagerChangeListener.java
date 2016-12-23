package com.example.jiangyue.androidap.views.viewpager;

import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by jiangyue on 15/7/6.
 */
public class PagerChangeListener {

    public int lastPosition;

    public PagerChangeListener() {
    }

    public PagerChangeListener(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, LinearLayout tabsContainer) {
    }

    public void onPageScrollStateChanged(int state) {
    }

    public void onPageSelected(int position, LinearLayout tabsContainer) {
    }

    public void onPageClickListener(int position, View view) {
    }

    public void onPageLongClickListener(int position, View view) {
    }
}
