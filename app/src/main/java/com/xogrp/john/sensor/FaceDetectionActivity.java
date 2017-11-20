package com.xogrp.john.sensor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.xogrp.john.sensor.faceDetection.FaceDetectionHelper;

/**
 * Created by Administrator on 2017/11/18.
 */

public class FaceDetectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        FaceDetectionHelper faceDetectionHelper = new FaceDetectionHelper();

        int id = R.mipmap.git1;

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setBackgroundResource(id);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        FaceDetectionHelper.CropResult cropResult = faceDetectionHelper.cropBitmapByFace(bitmap, 400, 400, 5, 4);
        ImageView imageCropView = (ImageView) findViewById(R.id.image_crop);
        imageCropView.setImageBitmap(cropResult.getBitmap());
    }
}
