package com.xogrp.john.sensor;

import android.content.Intent;
import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by john on 15/03/2018.
 */

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{
    SurfaceView mSurfaceView;
    android.hardware.Camera mCamera;
    private static final int TAKE_PHOTO_CODE = 1;
    private static final int TAKE_VIDEO_CODE = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_activity);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_camera);
        findViewById(R.id.take_photo).setOnClickListener(this);
        findViewById(R.id.take_video).setOnClickListener(this);
        mCamera = android.hardware.Camera.open();
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_photo:
                mCamera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                        File outputFile = getFile("take_photo.jpg");
                        if(outputFile != null){
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                                fileOutputStream.write(data);
                                fileOutputStream.close();
                            } catch (FileNotFoundException e) {
                            } catch (IOException e) {
                            }
                        }
                    }
                });
                break;
            case R.id.take_photo_intent:
                //intent 调用的不需要 声明camera权限
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri = Uri.fromFile(getFile("sensor_camera.jpg"));
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_CODE);
                break;
            case R.id.take_video_intent:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Uri videoUri = Uri.fromFile(getFile("sensor_camera.mp4"));
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(takeVideoIntent, TAKE_VIDEO_CODE);
                break;
        }
    }

    private File getFile(String fileName){
        File dir = new File(String.format("%s/%s", Environment.getExternalStorageDirectory().getAbsolutePath(), "Sensor"));
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, fileName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(TAKE_PHOTO_CODE == requestCode){
            if(RESULT_OK == resultCode){
                if(data != null){
                    Toast.makeText(this, "Image saved to:\n" +
                            data.getData(), Toast.LENGTH_LONG).show();
                }

            }
        }

        if(TAKE_VIDEO_CODE == requestCode){
            if(RESULT_OK == resultCode){
                if(data != null){
                    Toast.makeText(this, "Video saved to:\n" +
                            data.getData(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
