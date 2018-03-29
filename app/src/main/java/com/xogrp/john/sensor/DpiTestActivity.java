package com.xogrp.john.sensor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xogrp.john.myjni.NdkUtil;

public class DpiTestActivity extends AppCompatActivity {

    private static final String TAG = "DpiTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dpi_test);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        String info = "手机型号: " + android.os.Build.MODEL
                + "\nSDK版本:" + android.os.Build.VERSION.SDK
                + "\n系统版本:" + android.os.Build.VERSION.RELEASE
                + "\n屏幕宽度（像素）: " +width
                + "\n屏幕高度（像素）: " + height
                + "\n屏幕密度:  " + density
                + "\n屏幕密度DPI: "+ densityDpi;
        Log.e(TAG, info);
        TextView textSource = (TextView) findViewById(R.id.textSource);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blue_card_intro_hand);
        textSource.setText("源图片 宽： "+bitmap.getWidth()+ "长： "+bitmap.getHeight());
        final TextView textView = (TextView) findViewById(R.id.text);
        final ImageView imageView = (ImageView) findViewById(R.id.img);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText("宽： "+imageView.getWidth()+ "长： "+imageView.getHeight());
            }
        });
    }

}
