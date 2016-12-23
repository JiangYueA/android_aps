package com.example.jiangyue.androidap.shares;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.jiangyue.androidap.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * Created by jiangyue on 15/8/29.
 */
public class SharesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shares);
        ShareSDK.initSDK(this);
    }

    public void onClick(View v) {
        PlatformActionListener paListener = new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        };

        Platform weibo = ShareSDK.getPlatform(QZone.NAME);
        weibo.SSOSetting(true);
        weibo.setPlatformActionListener(paListener); // 设置分享事件回调
        weibo.authorize();
    }
}
