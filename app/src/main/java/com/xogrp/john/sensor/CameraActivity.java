package com.xogrp.john.sensor;

import android.content.Intent;
import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
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
    SurfaceHolder mSurfaceHolder;
    android.hardware.Camera mCamera;
    Button takeVideoBtn;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private static final int TAKE_PHOTO_CODE = 1;
    private static final int TAKE_VIDEO_CODE = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_activity);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_camera);
        findViewById(R.id.take_photo).setOnClickListener(this);
        takeVideoBtn = (Button) findViewById(R.id.take_video);
        takeVideoBtn.setOnClickListener(this);
        findViewById(R.id.take_photo_intent).setOnClickListener(this);
        findViewById(R.id.take_video_intent).setOnClickListener(this);
        mCamera = android.hardware.Camera.open();
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mSurfaceHolder = holder;
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
            case R.id.take_video:
                if(isRecording){
                    takeVideoBtn.setText("开始录像");
                    mMediaRecorder.stop();
                    releaseMediaRecorder();
                    isRecording = false;
                }else{
                    if (prepareVideoRecorder()) {
                        mMediaRecorder.start();
                        isRecording = true;
                        takeVideoBtn.setText("停止录像");
                    } else {
                        releaseMediaRecorder();
                    }
                }
                break;
            case R.id.take_photo_intent:
                //intent 调用的不需要 声明camera权限
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri = FileProvider.getUriForFile(this, "com.xogrp.john.sensor.fileProvider", getFile("sensor_camera.jpg"));
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_CODE);
                break;
            case R.id.take_video_intent:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Uri videoUri = FileProvider.getUriForFile(this, "com.xogrp.john.sensor.fileProvider", getFile("sensor_camera.mp4"));
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(takeVideoIntent, TAKE_VIDEO_CODE);
                break;
        }
    }

    private boolean prepareVideoRecorder(){
        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setOutputFile(getFile("take_video.mp4").toString());
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = android.hardware.Camera.open();//open the camera for the application
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
        takeVideoBtn.setText("开始录像");
        isRecording = false;
    }


    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
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
