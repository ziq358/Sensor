package com.xogrp.john.sensor.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShutdownBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ziq";
    private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";

    @Override
    public void onReceive(Context context, Intent intent) {  //即将关机时，要做的事情
        if (intent.getAction().equals(ACTION_SHUTDOWN)) {
            Log.i(TAG, "Shutdown  BroadcastReceiver onReceive(), Do thing!");
        }
    }
}
