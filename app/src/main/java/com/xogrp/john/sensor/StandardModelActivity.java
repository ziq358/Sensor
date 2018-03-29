package com.xogrp.john.sensor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StandardModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ziq", "onCreate: "+this);
        setContentView(R.layout.activity_model);
        ((TextView)findViewById(R.id.text)).setText(" "+this);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class cls = null;
                switch (v.getId()){
                    case R.id.btn_standard:
                        cls = StandardModelActivity.class;
                        break;
                    case R.id.btn_singleTop:
                        cls = SingleTopModelActivity.class;
                        break;
                    case R.id.btn_singleTask:
                        cls = SingleTaskModelActivity.class;
                        break;
                    case R.id.btn_singleInstance:
                        cls = SingleInstanceModelActivity.class;
                        break;
                }
                if(cls != null){
                    Intent intent = new Intent(v.getContext(), cls);
                    startActivity(intent);
                }
            }
        };
        findViewById(R.id.btn_standard).setOnClickListener(listener);
        findViewById(R.id.btn_singleTop).setOnClickListener(listener);
        findViewById(R.id.btn_singleTask).setOnClickListener(listener);
        findViewById(R.id.btn_singleInstance).setOnClickListener(listener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("ziq", "onNewIntent: "+this);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        Log.e("ziq", "onResume: "+this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e("ziq", "onStop: "+this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("ziq", "onDestroy: "+this);
        super.onDestroy();
    }
}
