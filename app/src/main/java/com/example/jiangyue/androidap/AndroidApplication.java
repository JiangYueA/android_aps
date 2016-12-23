package com.example.jiangyue.androidap;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

/**
 * Created by jiangyue on 16/7/28.
 */
public class AndroidApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        Nuwa.init(this);
    }

}
