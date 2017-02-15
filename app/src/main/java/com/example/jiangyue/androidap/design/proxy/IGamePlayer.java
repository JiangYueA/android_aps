package com.example.jiangyue.androidap.design.proxy;

/**
 * Created by jiangyue on 17/2/8.
 */
public interface IGamePlayer {
    //登录游戏
    public void login(String user, String password);

    //杀怪，这是网络游戏的主要特色
    public void killBoss();

    //升级
    public void upgrade();

    //每个人都可以找一下自己的代理
    public IGamePlayer getProxy();
}
