package com.example.jiangyue.androidap.design.mvvm.actions;

/**
 * Created by jiangyue on 17/2/22.
 */
public class Action<T> {
    private final String type;
    private final T data;

    Action(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }
}