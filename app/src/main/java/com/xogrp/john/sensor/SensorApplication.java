package com.xogrp.john.sensor;

import android.app.Application;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

public class SensorApplication extends Application {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onCreate() {
         super.onCreate();
            Log.e("ziq", "SensorApplication onCreate: "+android.os.Process.myPid());
         JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush
    }
}