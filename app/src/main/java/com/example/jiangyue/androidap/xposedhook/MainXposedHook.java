package com.example.jiangyue.androidap.xposedhook;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by jiangyue on 17/3/22.
 */
public class MainXposedHook implements IXposedHookLoadPackage {

    public String TAG = MainXposedHook.class.getName();
    private final String mStrPackageName = "com.lcf"; //HOOK APP目标的包名
    private final String mStrClassPath = "com.lcf.presenter.login.LoginPresenter"; //HOOK 目标类全路径
    private final String helloworld = "loadLoginData"; //HOOK 目标函数名

    private void LOGI(String ct) {
        XposedBridge.log(TAG + ct);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //对比此时加载的包名是否与目标包名一致
        if (loadPackageParam.packageName.equals(mStrPackageName)) {
            LOGI("found target: " + loadPackageParam.packageName);
            // findAndHookMethod方法用于查找匹配HOOK的函数方法，这里参数为HOOK的目标信息
            XposedHelpers.findAndHookMethod(mStrClassPath, //类全路径
                    loadPackageParam.classLoader, //ClassLoader
                    helloworld, //HOOK目标函数名
                    Context.class, //参数1类型
                    String.class, //参数2类型
                    String.class, //参数3类型（这里目标函数有多少个参数就多少个，与HOOK目标函数保持一致）
                    new XC_MethodHook() { //最后一个参数为一个回调CALLBACK
                        @Override //故名思意，这个函数会在目标函数被调用前被调用
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            LOGI("，beforeHook：" + param.args[1] + "," + param.args[2]);
                        }

                        @Override//这个函数会在目标函数被调用后被调用
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            LOGI("，afterHooke param: ");
                        }
                    });
        }
    }
}
