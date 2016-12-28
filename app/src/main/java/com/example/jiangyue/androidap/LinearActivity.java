package com.example.jiangyue.androidap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.jiangyue.androidap.fragment.ListFragment;
import com.example.jiangyue.androidap.fragment.MyOnTouchListener;
import com.example.jiangyue.androidap.fragment.OnListListener;
import com.example.jiangyue.androidap.views.viewpager.PagerAdapterTxtWrapper;
import com.example.jiangyue.androidap.views.viewpager.PagerChangeListener;
import com.example.jiangyue.androidap.views.viewpager.PagerInfo;
import com.example.jiangyue.androidap.views.viewpager.PagerView;
import com.example.jiangyue.androidap.views.viewpager.ScrollPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyue on 16/7/13.
 */
public class LinearActivity extends FragmentActivity implements OnListListener {

    private ScrollPagerSlidingTabStrip mIndicator;
    private PagerView mViewPager;


    private List<PagerInfo> function;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear);

        mIndicator = (ScrollPagerSlidingTabStrip) findViewById(R.id.id_scrolllayout_indicator);
        mViewPager = (PagerView) findViewById(R.id.id_scrolllayout_viewpager);

        function = new ArrayList<PagerInfo>();
        PagerInfo photoPager = new PagerInfo("照片", new ListFragment());
        PagerInfo nicePager = new PagerInfo("精选", new ListFragment());
        function.add(photoPager);
        function.add(nicePager);

        PagerAdapterTxtWrapper adapter = new PagerAdapterTxtWrapper(getSupportFragmentManager(), function);

        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setTabTextViewColor(0);

        mIndicator.setOnPageChangeListener(new PagerChangeListener(0) {
            @Override
            public void onPageSelected(int position, LinearLayout tabsContainer) {
            }

            @Override
            public void onPageClickListener(final int position, View view) {
            }

        });
    }

    public void onClick(View v) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoading(String msg) {

    }

    @Override
    public void onLoading(String msg, int progress) {

    }

    @Override
    public void finishLoading() {

    }

    @Override
    public void switchContent(Fragment fragment) {

    }

    @Override
    public void addContent(Fragment fragment) {

    }

    @Override
    public void onBack() {

    }

    @Override
    public void reloadActivity() {

    }

    @Override
    public void switchContent(int rid) {

    }

    @Override
    public void switchContent(int rid, int type) {

    }

    @Override
    public void setInputKeyPan(boolean allow) {

    }

    @Override
    public void setOnTouchListener(MyOnTouchListener listener) {

    }

    @Override
    public void removeOnTouchListener(MyOnTouchListener listener) {

    }
}
