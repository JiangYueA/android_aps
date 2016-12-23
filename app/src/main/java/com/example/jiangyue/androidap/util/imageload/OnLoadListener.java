package com.example.jiangyue.androidap.util.imageload;

public interface OnLoadListener {
    public void onSuccess(Object obj, ReqBean reqMode);

    public void onError(Object obj, ReqBean bean);
}