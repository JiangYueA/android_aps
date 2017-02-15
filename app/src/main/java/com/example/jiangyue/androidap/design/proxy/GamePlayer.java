package com.example.jiangyue.androidap.design.proxy;

/**
 * Created by jiangyue on 17/2/8.
 */
public class GamePlayer implements IGamePlayer {
    private String name = "";
    private IGamePlayer proxy = null;

    public GamePlayer(String _name) {
        this.name = _name;
    }

    @Override
    public void login(String user, String password) {
        System.out.println("登陆");
    }

    @Override
    public void killBoss() {
        System.out.println("杀怪");
    }

    @Override
    public void upgrade() {
        System.out.println("升级");
    }

    @Override
    public IGamePlayer getProxy() {
        return null;
    }
}
