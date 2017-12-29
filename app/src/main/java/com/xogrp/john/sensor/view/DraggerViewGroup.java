package com.xogrp.john.sensor.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xogrp.john.sensor.R;

/**
 * Created by john on 29/12/2017.
 */

public class DraggerViewGroup extends FrameLayout {

    ViewDragHelper mViewDragHelper;
    private View mFirstChild;


    public DraggerViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public DraggerViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggerViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mFirstChild;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }


            //返回拖拽的范围，不对拖拽进行真正的限制，仅仅决定了动画执行速度
            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth()-child.getMeasuredWidth();
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFirstChild = findViewById(R.id.dragger_text);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll()
    {
        if(mViewDragHelper.continueSettling(true))
        {
            invalidate();
        }
    }
}
