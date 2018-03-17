package com.xogrp.john.sensor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.xogrp.john.sensor.openGl2_image_360.CameraRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by john on 15/03/2018.
 */

public class CameraGLActivity extends Activity implements View.OnClickListener{
    public static final int NO_TEXTURE = -1;
    GLSurfaceView mSurfaceView;
    SurfaceTexture mSurfaceTexture;
    CameraRenderer mCameraRenderer;
    int textureId = NO_TEXTURE;
    Camera mCamera;
    int mCameraRestOrientation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gl_activity);
        mSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface);
        findViewById(R.id.take_photo).setOnClickListener(this);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CameraActivity.doFocus(mCamera, event, mSurfaceView.getWidth(),mSurfaceView.getHeight(),CameraGLActivity.this );
                return false;
            }
        });
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                if(mCameraRenderer == null){
                    mCameraRenderer = new CameraRenderer(CameraGLActivity.this);
                }
                mCameraRenderer.onSurfaceCreated(gl, config);

                if (textureId == NO_TEXTURE) {
                    textureId = getExternalOESTextureID();
                    if (textureId != NO_TEXTURE) {
                        mSurfaceTexture = new SurfaceTexture(textureId);
                        mSurfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
                    }
                }
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                mCameraRenderer.onSurfaceChanged(gl, width, height);
                openCamera();
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                if(mSurfaceTexture == null) return;
                mSurfaceTexture.updateTexImage();
                float[] mtx = new float[16];
                mSurfaceTexture.getTransformMatrix(mtx);
                mCameraRenderer.setTextureTransformMatrix(mtx);
                mCameraRenderer.onDrawFrame(textureId);
            }
        });
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public static int getExternalOESTextureID(){
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            mSurfaceView.requestRender();
        }
    };

    private void openCamera(){
        if(mCamera == null) mCamera = Camera.open();
        CameraActivity.initPreviewSize(CameraGLActivity.this, mCamera, mSurfaceView.getWidth(), mSurfaceView.getHeight());
        CameraActivity.initPictureSize(CameraGLActivity.this,mCamera, mSurfaceView.getWidth(), mSurfaceView.getHeight());
        if(mSurfaceTexture != null){
            if(mCamera != null){
                try {
                    mCamera.setPreviewTexture(mSurfaceTexture);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = Camera.open();//open the camera for the application
        mCameraRestOrientation =  CameraActivity.setCameraDisplayOrientation(this,0, mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
        mSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        mCameraRenderer.onDestry();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_photo:
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        File outputFile = CameraActivity.getFile("take_photo.jpg");
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
        }
    }

}
