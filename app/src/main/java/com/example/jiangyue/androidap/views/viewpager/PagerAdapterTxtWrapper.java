package com.example.jiangyue.androidap.views.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by jiangyue on 15/7/6.
 */
public class PagerAdapterTxtWrapper extends FragmentPagerAdapter {

    private List<PagerInfo> pagerInfos;

    public PagerAdapterTxtWrapper(FragmentManager fm, List<PagerInfo> pagerInfos) {
        super(fm);
        this.pagerInfos = pagerInfos;
    }

    @Override
    public int getCount() {
        return pagerInfos.size();
    }

    @Override
    public Fragment getItem(int position) {
        if (checkPager(position)) {
            return pagerInfos.get(position).getFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (checkPager(position)) {
            return pagerInfos.get(position).getTitle();
        }
        return "";
    }

    /* 移出item */
    public void removeItem(int position) {
        if (checkPager(position)) {
            pagerInfos.remove(position);
        }
    }

    private boolean checkPager(int position) {
        if (null != pagerInfos && position < pagerInfos.size()) {
            return true;
        } else {
            return false;
        }
    }
}
