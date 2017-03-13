package com.example.jiangyue.androidap.design.mvvm.actions;

import com.example.jiangyue.androidap.design.mvvm.dispatcher.Dispatcher;

/**
 * Flux的ActionCreator模块
 * Created by jiangyue on 17/2/22.
 */
public class ActionsCreator {

    private static ActionsCreator instance;
    final Dispatcher dispatcher;

    ActionsCreator(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static ActionsCreator get(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new ActionsCreator(dispatcher);
        }
        return instance;
    }

    public void sendMessage(String message) {
        dispatcher.dispatch(new MessageAction(MessageAction.ACTION_NEW_MESSAGE, message));
    }
}