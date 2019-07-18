package com.xogrp.john.sensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
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

public class CameraActivity extends Activity implements View.OnClickListener{
    public static final int AREA_SIZE = 100;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    android.hardware.Camera mCamera;
    int mCameraRestOrientation;
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
        findViewById(R.id.effect_negative).setOnClickListener(this);
        findViewById(R.id.effect_none).setOnClickListener(this);
        findViewById(R.id.take_video_intent).setOnClickListener(this);
        findViewById(R.id.take_video_intent).setOnClickListener(this);

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initPreviewSize(CameraActivity.this, mCamera, mSurfaceView.getWidth(), mSurfaceView.getHeight());
                initPictureSize(CameraActivity.this, mCamera, mSurfaceView.getWidth(), mSurfaceView.getHeight());

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
                mCamera.stopPreview();
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                doFocus(mCamera, event, mSurfaceView.getWidth(),mSurfaceView.getHeight(),CameraActivity.this );
                return false;
            }
        });
    }

    public static void doFocus(Camera mCamera, MotionEvent event, int surfaceWidth, int surfaceHeight, final Context context){
        mCamera.cancelAutoFocus();
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
        //在Camera.Area对象中的Rect字段，代表了一个被映射成2000x2000单元格的矩形。
        // 坐标（-1000，-1000）代表Camera图像的左上角，（1000,1000）代表Camera图像的右下角
        float touchX = (event.getX() / surfaceWidth) * 2000 - 1000;
        float touchY = (event.getY() / surfaceHeight) * 2000 - 1000;
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
                Toast.makeText(context, "onAutoFocus:\n" + success, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static int clamp(int x, int min, int max) {//保证坐标必须在min到max之内，否则异常
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public static void initPictureSize(Context context, android.hardware.Camera camera, int surfaceWidth, int surfaceHeight){
        android.hardware.Camera.Parameters parameters = camera.getParameters();

        Log.e("ziq", "initPictureSize: ---"+parameters.getPictureSize().width+" "+parameters.getPictureSize().height);
        android.hardware.Camera.Size size = getCloselySize(isPortrait(context), surfaceWidth, surfaceHeight, parameters.getSupportedPictureSizes());
        Log.e("ziq", "initPictureSize: --- target "+size.width+" "+size.height);
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    public static void initPreviewSize(Context context, android.hardware.Camera camera, int surfaceWidth, int surfaceHeight){
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        Log.e("ziq", "initPreviewSize: ---"+parameters.getPreviewSize().width+" "+parameters.getPreviewSize().height);
        android.hardware.Camera.Size size = getCloselySize(isPortrait(context), surfaceWidth, surfaceHeight, parameters.getSupportedPreviewSizes());
        Log.e("ziq", "initPreviewSize: --- target "+size.width+" "+size.height);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    public static android.hardware.Camera.Size getCloselySize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<android.hardware.Camera.Size> sizeList) {
        Log.e("ziq", "getCloselySize: ----- surfaceWidth "+surfaceWidth+" surfaceHeight "+surfaceHeight);
        android.hardware.Camera.Size targetSize = null;
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        if(sizeList != null){
            for (android.hardware.Camera.Size size:sizeList){
                Log.e("ziq", "getCloselySize: "+size.width+" "+size.height);
                if((size.width == reqTmpWidth) && (size.height == reqTmpHeight)){
                    return size;
                }
            }
            // 得到与传入的宽高比最接近的size
            float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
            float curRatio, deltaRatio;
            float deltaRatioMin = Float.MAX_VALUE;

            for (android.hardware.Camera.Size size : sizeList) {
                curRatio = ((float) size.width) / size.height;
                deltaRatio = Math.abs(reqRatio - curRatio);
                if (deltaRatio < deltaRatioMin) {
                    deltaRatioMin = deltaRatio;
                    targetSize = size;
                }
            }

        }
        return targetSize;
    }

    //activity 方向
    public static boolean isPortrait(Context context){
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }


        @Override
    protected void onResume() {
        super.onResume();
        mCamera = android.hardware.Camera.open();//open the camera for the application
        mCameraRestOrientation = setCameraDisplayOrientation(this,0, mCamera);

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
                                //对原来的图片进行，， 角度旋转，，使得与预览效果一样，解决预览 角度 与实际图片 角度不同的问题
                                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                                Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
                                Matrix matrix = new Matrix();
                                matrix.postRotate(mCameraRestOrientation);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.close();
                                mCamera.stopPreview();
                                mCamera.startPreview();
                            } catch (FileNotFoundException e) {
                            } catch (IOException e) {
                            }
                        }
                    }
                });
                break;
            case R.id.effect_negative:
                android.hardware.Camera.Parameters parametersN = mCamera.getParameters();
                parametersN.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
                mCamera.setParameters(parametersN);
                break;
            case R.id.effect_none:
                android.hardware.Camera.Parameters parameters = mCamera.getParameters();
                parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                mCamera.setParameters(parameters);
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
        mMediaRecorder.setOutputFile(getFile("take_video.wav").toString());
        mMediaRecorder.setOrientationHint(mCameraRestOrientation);
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

    public static File getFile(String fileName){
        File dir = new File(String.format("%s/%s", Environment.getExternalStorageDirectory().getAbsolutePath(), "Sensor"));
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, fileName);
    }

    public static int setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
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
        Log.e("ziq", "mCameraRestOrientation: "+result);
        camera.setDisplayOrientation(result);
        return result;
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
