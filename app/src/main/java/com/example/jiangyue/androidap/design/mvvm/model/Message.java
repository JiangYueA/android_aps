package com.example.jiangyue.androidap.design.mvvm.model;

/**
 * Created by jiangyue on 17/2/22.
 */
public class Message {
    private String mText;

    public Message(){}

    public Message(String text) {
        mText = text;
    }

    public String getMessage() {
        return mText;
    }

    public void setMessage(String text) {
        mText = text;
    }
}