package com.example.jiangyue.androidap.design.mvvm.actions;

/**
 * Created by jiangyue on 17/2/22.
 */
public class MessageAction extends Action<String> {
    public static final String ACTION_NEW_MESSAGE = "new_message";

    MessageAction(String type, String data) {
        super(type, data);
    }
}
