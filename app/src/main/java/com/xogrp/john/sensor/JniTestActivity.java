package com.xogrp.john.sensor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xogrp.john.myjni.NdkUtil;

public class JniTestActivity extends AppCompatActivity {

    ImageView ivRoot, ivResult;

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

        ivRoot = (ImageView) findViewById(R.id.gaussBlur_root);
        ivResult = (ImageView) findViewById(R.id.gaussBlur_result);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.checklist_default);
        Bitmap copyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),bitmap.getConfig());//
        Canvas canvas = new Canvas(copyBitmap);
        Paint paint = new Paint();
        Matrix m = new Matrix();
        canvas.drawBitmap(bitmap, m, paint);//生成了了一个bitmap 的副本，不受之后 原bitmap的变化影响

        //也是副本
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1); // 实现图片的反转
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/2 ,bitmap.getWidth(), bitmap.getHeight()/2, matrix, false);

        ivRoot.setImageBitmap(reflectionImage);
        ivResult.setImageBitmap(bitmap);
        findViewById(R.id.gaussBlur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gaussBlur(ivResult, bitmap);
            }
        });
    }

    public native String stringFromJNI();

    public void gaussBlur(View v, Bitmap bitmap) {
        //调用native方法，传入Bitmap对象，对Bitmap进行高斯迷糊处理
        NdkUtil ndkUtil = new NdkUtil();
        ndkUtil.gaussBlur(bitmap);
        //把Bitmap对象设置给iv2，，因为是直接修改bitmap，之前setImageBitmap 中的imageView 的bitmap 也变了，所以不用再设置
//        ivResult.setImageBitmap(bitmap);
    }

}
