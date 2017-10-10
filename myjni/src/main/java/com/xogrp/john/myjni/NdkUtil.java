package com.xogrp.john.myjni;

import android.graphics.Bitmap;

/**
 * Created by john on 10/10/2017.
 */

public class NdkUtil {

    static {
        System.loadLibrary("stringFromJNI-lib");
    }

    public native String stringFromJNI();

    public native void gaussBlur(Bitmap bitmap);
}
