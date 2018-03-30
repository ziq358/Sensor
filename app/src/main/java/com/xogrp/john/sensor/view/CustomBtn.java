package com.xogrp.john.sensor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by john on 30/03/2018.
 */

public class CustomBtn extends Button {
    public CustomBtn(Context context) {
        super(context);
    }

    public CustomBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.e("ziq", "dispatchTouchEvent: ");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("ziq", "onTouchEvent: ");
        return super.onTouchEvent(event);
    }
}
