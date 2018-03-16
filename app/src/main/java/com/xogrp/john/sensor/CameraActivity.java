package com.xogrp.john.sensor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Rect;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 15/03/2018.
 */

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int AREA_SIZE = 100;
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

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCamera.cancelAutoFocus();
                android.hardware.Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
                float touchX = (event.getX() / mSurfaceView.getWidth()) * 2000 - 1000;
                float touchY = (event.getY() / mSurfaceView.getHeight()) * 2000 - 1000;
                int left = clamp((int) touchX - AREA_SIZE / 2, -1000, 1000);
                int right = clamp(left + AREA_SIZE, -1000, 1000);
                int top = clamp((int) touchY - AREA_SIZE / 2, -1000, 1000);
                int bottom = clamp(top + AREA_SIZE, -1000, 1000);
                Rect rect = new Rect(left, top, right, bottom);

                if (parameters.getMaxNumFocusAreas() > 0) {
                    List<android.hardware.Camera.Area> areaList = new ArrayList<android.hardware.Camera.Area>();
                    areaList.add(new android.hardware.Camera.Area(rect, 1000));
                    parameters.setFocusAreas(areaList);
                }

                if (parameters.getMaxNumMeteringAreas() > 0) {
                    List<android.hardware.Camera.Area> areaList = new ArrayList<android.hardware.Camera.Area>();
                    areaList.add(new android.hardware.Camera.Area(rect, 1000));
                    parameters.setMeteringAreas(areaList);
                }
                mCamera.setParameters(parameters);
                mCamera.autoFocus(new android.hardware.Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, android.hardware.Camera camera) {
                        Toast.makeText(CameraActivity.this, "onAutoFocus:\n" + success, Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            }
        });
    }

    private int clamp(int x, int min, int max) {//保证坐标必须在min到max之内，否则异常
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = android.hardware.Camera.open();//open the camera for the application
        setCameraDisplayOrientation(this,0, mCamera);

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

    public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
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
