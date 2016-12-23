package com.example.jiangyue.androidap.util.imageload;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ReqBean implements Parcelable {
    // 当前的上下文
    private Context mContext = null;
    // 下载地址
    private String url = "";
    // 请求类型，post或者get方式
    private String reqType = "";
    // 请求来源
    private int reqMode = 0;
    // 地址附带信息
    private String json = "";

    private Object obj = null;

    private int index = -1;

    private String newsId = "";

    private OnLoadListener lister = null;

    private String sourceUrl = "";
    
    private Object mSyncToken = null;
    

    public Object getmSyncToken() {
        return mSyncToken;
    }

    public void setmSyncToken(Object mSyncToken) {
        this.mSyncToken = mSyncToken;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public OnLoadListener getLister() {
        return lister;
    }

    public void setLister(OnLoadListener lister) {
        this.lister = lister;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public int getReqMode() {
        return reqMode;
    }

    public void setReqMode(int reqMode) {
        this.reqMode = reqMode;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return " url : " + url + " reqType : " + reqType + " json: " + json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().getName().equals(this.getClass().getName())) {
            if (obj instanceof ReqBean) {
                ReqBean temp = (ReqBean) obj;
                if (temp.getUrl().equalsIgnoreCase(this.getUrl()) && temp.getReqMode() == this.getReqMode()
                        && temp.getJson().equals(this.getJson())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
