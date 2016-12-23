package com.example.jiangyue.androidap.views.viewpager;

import android.support.v4.app.Fragment;

/**
 * Created by jiangyue on 15/7/6.
 */
public class PagerInfo {

    private int selectIcon;
    private int unSelectIcon;
    private boolean selected;
    private Fragment fragment;
    private String title;

    public PagerInfo(int selectIcon, int unSelectIcon, Fragment fragment) {
        this(false, selectIcon, unSelectIcon, fragment);
    }

    public PagerInfo(String title, Fragment fragment) {
        this.setFragment(fragment);
        this.setTitle(title);

    }

    public PagerInfo(boolean selected, int selectIcon, int unSelectIcon, Fragment fragment) {
        this.setSelected(selected);
        this.setFragment(fragment);
        this.setSelectIcon(selectIcon);
        this.setUnSelectIcon(unSelectIcon);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getSelected() {
        return selected;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public int getSelectIcon() {
        return selectIcon;
    }

    public int getUnSelectIcon() {
        return unSelectIcon;
    }

    public void setSelectIcon(int selectIcon) {
        this.selectIcon = selectIcon;
    }

    public void setUnSelectIcon(int unSelectIcon) {
        this.unSelectIcon = unSelectIcon;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
