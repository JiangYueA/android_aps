package com.example.jiangyue.androidap.util.imageload;

import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class ReqDetailBean extends ReqBean implements Parcelable {
    /*----------------定义发送网络请求时的特殊参数----------------*/
    private String groupName;
    private String groupId;
    private boolean isTemp;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }

}