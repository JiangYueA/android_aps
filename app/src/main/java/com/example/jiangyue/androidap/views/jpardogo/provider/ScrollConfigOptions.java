package com.example.jiangyue.androidap.views.jpardogo.provider;

/**
 * Created by jpardogo on 23/02/2014.
 */
public enum ScrollConfigOptions {
    RIGHT,
    LEFT, ScrollConfigOptions;


    public int getConfigValue() {
        return ordinal() + 1;
    }
}
