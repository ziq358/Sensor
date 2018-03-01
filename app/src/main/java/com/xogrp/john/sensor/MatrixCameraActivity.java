package com.xogrp.john.sensor;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.xogrp.john.sensor.matrix.Rotate3dAnimation;

/**
 * Created by john on 01/03/2018.
 */

public class MatrixCameraActivity extends AppCompatActivity {
    ImageView img, img2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_camera);
        img = (ImageView) findViewById(R.id.img);
        img2 = (ImageView) findViewById(R.id.img2);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(img, 0, 180, false);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(img2, 0, 180, true);
            }
        });

    }

    private void rotate(View targetView, float fromDegrees, float toDegrees, boolean fix){
        final float centerX = targetView.getWidth() / 2.0f;
        final float centerY = targetView.getHeight() / 2.0f;
        Rotate3dAnimation animation = new Rotate3dAnimation(this, fromDegrees, toDegrees, centerX, centerY, 1.0f, true, fix);
        animation.setDuration(1500);
        animation.setFillAfter(true);
        targetView.startAnimation(animation);
    }


}
