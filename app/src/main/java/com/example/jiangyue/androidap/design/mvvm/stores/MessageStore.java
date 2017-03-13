package com.example.jiangyue.androidap.design.mvvm.stores;

import com.example.jiangyue.androidap.design.mvvm.actions.Action;
import com.example.jiangyue.androidap.design.mvvm.actions.MessageAction;
import com.example.jiangyue.androidap.design.mvvm.model.Message;

/**
 * MessageStore类主要用来维护MainActivity的UI状态
 * Created by jiangyue on 17/2/22
 */
public class MessageStore extends Store {
    private static MessageStore singleton;
    private Message mMessage = new Message();

    public MessageStore() {
        super();
    }

    public String getMessage() {
        return mMessage.getMessage();
    }

    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case MessageAction.ACTION_NEW_MESSAGE:
                mMessage.setMessage((String) action.getData());
                break;
            default:
        }
        emitStoreChange();
    }


    @Override
    public StoreChangeEvent changeEvent() {
        return new StoreChangeEvent();
    }
}