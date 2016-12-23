package com.example.jiangyue.androidap.fragment;

import android.app.ActionBar;

/**
 * Created by linlinet on 14-8-19.
 */
public interface OnListListener {

    public void onLoading();

    public void onLoading(String msg);

    public void onLoading(String msg, int progress);

    public void finishLoading();

    public void switchContent(android.support.v4.app.Fragment fragment);

    public void addContent(android.support.v4.app.Fragment fragment);

    public void onBack();

    public void reloadActivity();

//        public void popBackStack();

    public void switchContent(int rid);

    public void switchContent(int rid, int type);

    public ActionBar getActionBar();

    public void setInputKeyPan(boolean allow);

    public void setOnTouchListener(MyOnTouchListener listener);

    public void removeOnTouchListener(MyOnTouchListener listener);
}
