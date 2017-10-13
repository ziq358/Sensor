package com.xogrp.john.sensor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by john on 13/10/2017.
 */

public class CanvasView extends View {
    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();

        int save1 = canvas.save();

        p.setColor(Color.argb(50,255,100,100));
        canvas.drawRect(0,0,200,200,p); // 以原始Canvas画出一个矩形1

        canvas.translate(300,300); // 将Canvas平移  (100,100)

        p.setColor(Color.argb(50,100,255,100));
        canvas.drawRect(0,0,200,200,p); //  矩形2

        canvas.rotate(30); //将Canvas旋转30

        p.setColor(Color.argb(50,100,0,255));
        canvas.drawRect(0,0,200,200,p); // 矩形3

        canvas.scale(2, 2); // 将Canvas以原点为中心，放大两倍

        p.setColor(Color.argb(50,255,255,0));
        canvas.drawRect(0,0,200,200,p); // 矩形4

        canvas.restore();
        //画布回到原始状态
        p.setColor(Color.argb(50,255,100,100));
        canvas.drawRect(300,0,500,200,p); // 以原始Canvas画出一个矩形5

    }
}
