package com.xogrp.john.sensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.xogrp.john.myjni.NdkUtil;

public class JniTestActivity extends AppCompatActivity {

    static {
        System.loadLibrary("hello-jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni_test);
        Log.e("ziq", stringFromJNI());
        NdkUtil ndkUtil = new NdkUtil();
        Log.e("ziq", ndkUtil.stringFromJNI());
    }

    public native String stringFromJNI();
}
