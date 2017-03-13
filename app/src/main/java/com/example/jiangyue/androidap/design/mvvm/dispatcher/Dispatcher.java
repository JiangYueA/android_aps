package com.example.jiangyue.androidap.design.mvvm.dispatcher;

import com.example.jiangyue.androidap.design.mvvm.actions.Action;
import com.example.jiangyue.androidap.design.mvvm.stores.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Flux的Dispatcher模块
 * Created by jiangyue on 17/2/22.
 */
public class Dispatcher {
    private static Dispatcher instance;
    private final List<Store> stores = new ArrayList<>();

    public static Dispatcher get() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    Dispatcher() {}

    public void register(final Store store) {
        stores.add(store);
    }

    public void unregister(final Store store) {
        stores.remove(store);
    }

    public void dispatch(Action action) {
        post(action);
    }

    private void post(final Action action) {
        for (Store store : stores) {
            store.onAction(action);
        }
    }
}