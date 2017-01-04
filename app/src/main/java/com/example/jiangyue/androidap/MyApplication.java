package com.example.jiangyue.androidap;

import android.app.Application;
import android.content.Context;

/**
 * Created by linlinet on 15/1/19.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static Context mcontext;


    @Override
    public void onCreate() {
        super.onCreate();
        mcontext = this;
    }

    public static Context getAppContext() {
        return mcontext;
    }
}
