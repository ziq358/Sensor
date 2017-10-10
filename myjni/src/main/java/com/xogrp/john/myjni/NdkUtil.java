package com.xogrp.john.myjni;

/**
 * Created by john on 10/10/2017.
 */

public class NdkUtil {

    static {
        System.loadLibrary("stringFromJNI-lib");
    }

    public native String stringFromJNI();
}
