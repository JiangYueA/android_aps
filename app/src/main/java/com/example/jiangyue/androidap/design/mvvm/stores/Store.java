package com.example.jiangyue.androidap.design.mvvm.stores;

import com.example.jiangyue.androidap.design.mvvm.actions.Action;
import com.example.jiangyue.androidap.design.mvvm.rxbus.RxBus;

import rx.Observable;

/**
 * Flux的Store模块
 * Created by jiangyue on 17/2/22.
 */
public abstract class Store {
    protected Store() {
    }

    public void register(String Tag, final Object view) {
        RxBus.getInstance().register(Tag, view.getClass());
    }

    public void unregister(String Tag, final Observable observable) {
        RxBus.getInstance().unregister(Tag, observable);
    }

    void emitStoreChange() {
        RxBus.getInstance().postEvent(changeEvent());
    }

    public abstract StoreChangeEvent changeEvent();

    public abstract void onAction(Action action);

    public class StoreChangeEvent {
    }
}
