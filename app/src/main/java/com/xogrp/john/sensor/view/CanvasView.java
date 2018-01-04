package com.xogrp.john.sensor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
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
        canvas.drawRect(0,0,100,100,p); // 以原始Canvas画出一个矩形1红

        canvas.translate(80,120); // 将Canvas平移

        p.setColor(Color.argb(50,100,255,100));
        canvas.drawRect(0,0,100,100,p); //  矩形2绿

        canvas.rotate(30); //将Canvas旋转30

        p.setColor(Color.argb(50,100,0,255));
        canvas.drawRect(0,0,100,100,p); // 矩形3红

        canvas.scale(2, 2); // 将Canvas以原点为中心，放大两倍

        p.setColor(Color.argb(50,255,255,0));
        canvas.drawRect(0,0,100,100,p); // 矩形4黄

        canvas.restore();
        //画布回到原始状态
        p.setColor(Color.argb(50,255,100,100));
        canvas.drawRect(150,0,250,100,p); // 以原始Canvas画出一个矩形5红

        drawDrag(canvas);
        drawArc(canvas);
        drawTail(canvas);
    }

    private void drawDrag(Canvas canvas) {
        canvas.save();
        canvas.translate(300,0); // 将Canvas平移
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff8b90af);

        int PULL_HEIGHT = 100;
        int mWidth = 100;
        int mHeight = 150;

        canvas.drawRect(0, 0, mWidth, PULL_HEIGHT, paint);
        canvas.translate(0,10);

        Path mPath = new Path();
        mPath.reset();
        mPath.moveTo(0, PULL_HEIGHT);
        mPath.quadTo(0.5f * mWidth, PULL_HEIGHT + (mHeight - PULL_HEIGHT) * 2,
                mWidth, PULL_HEIGHT);
        canvas.drawPath(mPath, paint);
        canvas.restore();
    }

    private void drawArc(Canvas canvas) {
        canvas.save();
        canvas.translate(470,0); // 将Canvas平移
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);//设 STROKE = 圆环， FILL = 填充的半圆
        paint.setStrokeWidth(10);
        paint.setColor(0xff8b90af);

        canvas.drawArc(new RectF(0, 0, 100, 100), 0, 180, false, paint);
        canvas.restore();
    }


    private void drawTail(Canvas canvas) {
        //贝塞尔曲线
        canvas.save();
        canvas.translate(150,0);

        int mWidth = 100;
        int mRadius = 100;
        int centerY = 20;
        int bottom = 150;
        float fraction = 0.5f;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xff8b90af);

        int bezier1w = (int) (mWidth / 2 + (mRadius * 3 / 4) * (1 - fraction));
        PointF start = new PointF(mWidth / 2 + mRadius, centerY);
        PointF bezier1 = new PointF(bezier1w, bottom);
        PointF bezier2 = new PointF(bezier1w + mRadius / 2, bottom);

        Path mPath = new Path();
        mPath.reset();
        mPath.moveTo(start.x, start.y);       //贝塞尔曲线 这里是2阶， 需要3个点
        mPath.quadTo(bezier1.x, bezier1.y,
                bezier2.x, bezier2.y);

        mPath.lineTo(mWidth - bezier2.x, bezier2.y);   //贝塞尔曲线 同样是3个点，接着lineTo的最后一个点
        mPath.quadTo(mWidth - bezier1.x, bezier1.y,
                mWidth - start.x, start.y);

        canvas.drawPath(mPath, paint);
        canvas.restore();
    }
}
