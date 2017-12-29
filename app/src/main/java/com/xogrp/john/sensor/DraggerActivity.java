package com.xogrp.john.sensor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import javax.security.auth.login.LoginException;

/**
 * Created by john on 29/12/2017.
 */

public class DraggerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 需要设置<item name="android:windowIsTranslucent">true</item>
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setBackgroundDrawable(null);

        setContentView(R.layout.activity_dragger);
        TextView textView = (TextView) findViewById(R.id.dragger_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DraggerActivity.this, "toast", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
