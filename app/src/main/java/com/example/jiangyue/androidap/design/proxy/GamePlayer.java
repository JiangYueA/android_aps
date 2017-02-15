package com.example.jiangyue.androidap.design.proxy;

import java.util.ArrayList;

/**
 * Created by jiangyue on 17/2/8.
 */
public class GamePlayer implements IGamePlayer, Cloneable {
    private ArrayList<String> arrayList = new ArrayList<String>();
    private String name = "";
    private IGamePlayer proxy = null;

    public GamePlayer() {
    }

    public GamePlayer(String _name) {
        this.name = _name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //设置HashMap的值
    public void setValue(String value) {
        this.arrayList.add(value);
    }

    //取得arrayList的值
    public ArrayList<String> getValue() {
        return this.arrayList;
    }

    @Override
    public void login(String user, String password) {
        System.out.println(name + "登陆");
    }

    @Override
    public void killBoss() {
        System.out.println(name + "杀怪");
    }

    @Override
    public void upgrade() {
        System.out.println(name + "升级");
    }

    @Override
    public IGamePlayer getProxy() {
        return null;
    }

    @Override
    public GamePlayer clone() {
        //原型模式，相比较new，是以内存二进制流的拷贝，性能更好，clone时不会执行构造方法
        GamePlayer prototypeClass = null;
        try {
            prototypeClass = (GamePlayer) super.clone();
            prototypeClass.arrayList = (ArrayList<String>) prototypeClass.arrayList.clone();
        } catch (CloneNotSupportedException e) {
            //异常处理
        }
        return prototypeClass;
    }

}
