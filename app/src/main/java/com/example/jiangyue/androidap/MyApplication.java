package com.example.jiangyue.androidap;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

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
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(this, "4c09736075", true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        Beta.installTinker();
    }

    public static Context getAppContext() {
        return mcontext;
    }
}
