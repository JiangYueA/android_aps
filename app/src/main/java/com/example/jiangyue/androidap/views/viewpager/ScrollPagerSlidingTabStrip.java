package com.example.jiangyue.androidap.views.viewpager;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.jiangyue.androidap.R;

/**
 * Created by jiangyue on 15/8/20.
 */
public class ScrollPagerSlidingTabStrip extends PagerSlidingTabStrip {
    public ScrollPagerSlidingTabStrip(Context context) {
        super(context);
    }

    public ScrollPagerSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollPagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* 设置初始tab按钮颜色 */
    public void setTabTextViewColor(int position) {
        if (tabsContainer != null && tabsContainer.getChildCount() > position) {
            ((TextView) tabsContainer.getChildAt(position)).setTextColor(getResources().getColor(R.color.gplus_color_1));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
