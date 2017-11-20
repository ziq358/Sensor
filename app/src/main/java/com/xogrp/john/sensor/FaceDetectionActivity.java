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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dopngman);
        Bitmap bitmapTarget = bitmap.copy(Bitmap.Config.RGB_565, true);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);

        FaceDetectionHelper.CropResult cropResult = faceDetectionHelper.cropBitmapByFace(bitmapTarget, 400, 400, 5);
        ImageView imageCropView = (ImageView) findViewById(R.id.image_crop);
        imageCropView.setImageBitmap(cropResult.getBitmap());
    }
}
