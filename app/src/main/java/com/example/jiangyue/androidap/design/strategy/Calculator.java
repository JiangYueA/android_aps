package com.example.jiangyue.androidap.design.strategy;

/**
 * Created by jiangyue on 17/2/9.
 */
public enum Calculator {

    ADD("+") {
        @Override
        public int exec(int a, int b) {
            return a + b;
        }
    };

    //定义成员值类型
    String value = "";

    private Calculator(String _value) {
        this.value = _value;
    }

    //获得枚举成员的值
    public String getValue() {
        return this.value;
    }

    //声明一个抽象函数
    public abstract int exec(int a, int b);

}
