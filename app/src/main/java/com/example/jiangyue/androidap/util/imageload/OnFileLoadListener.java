package com.example.jiangyue.androidap.util.imageload;

public interface OnFileLoadListener {
    public void onSuccess(ReqFileBean bean);

    public void onLoadPercent(ReqFileBean bean);

    public void onError();

}
