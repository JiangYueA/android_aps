package com.example.jiangyue.androidap.design;

import com.example.jiangyue.androidap.design.proxy.GamePlayIH;
import com.example.jiangyue.androidap.design.proxy.GamePlayer;
import com.example.jiangyue.androidap.design.proxy.IGamePlayer;
import com.example.jiangyue.androidap.design.strategy.Calculator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by jiangyue on 17/2/8.
 */
public class Test {

    public static void main(String[] args) {
        StrategyTest();
    }

    private static void ProxyTest() {
        IGamePlayer player = new GamePlayer("张三");


        InvocationHandler handler = new GamePlayIH(player);


        System.out.println("开始时间是：2009-8-25 10:45");
        //获得类的class loader
        ClassLoader cl = player.getClass().getClassLoader();
        //动态产生一个代理者
        IGamePlayer proxy = (IGamePlayer) Proxy.newProxyInstance(cl, new Class[]{IGamePlayer.class}, handler);


        proxy.login("zhangSan", "password");
        proxy.killBoss();
        proxy.upgrade();


        System.out.println("结束时间是：2009-8-26 03:40");
    }

    private static void PrototypeTest() {
        //产生一个对象
        GamePlayer thing = new GamePlayer();
        //设置一个值
        thing.setValue("张三");
        //拷贝一个对象
        GamePlayer cloneThing = thing.clone();
        cloneThing.setValue("李四");
        System.out.println(thing.getValue());
    }


    private static void StrategyTest() {
        System.out.printf("运算结果：" + Calculator.ADD.exec(1, 2));
    }

}
