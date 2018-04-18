package com.example.mqtt_test;

import android.app.Application;

/**
 * Created by 叶泽锐 on 2018/1/2.
 */

public class Myapplication extends Application {

    //声明一个变量
    public double Tem;
    public double Hum;

    //实现setname()方法，设置变量的值
    public void setTem(double tem) {
        this.Tem = tem;
    }

    //实现getname()方法，获取变量的值
    public double getTem() {
        return Tem;
    }

    public void setHum(double hum) {
        Hum = hum;
    }

    public double getHum() {
        return Hum;
    }
}
