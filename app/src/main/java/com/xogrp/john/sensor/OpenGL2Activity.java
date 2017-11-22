package com.xogrp.john.sensor;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xogrp.john.sensor.openGl2_image_360.OpenGlUtil;
import com.xogrp.john.sensor.openGl2_image_360.FullViewRenderer;
import com.xogrp.john.sensor.openGl2_image_360.SphereRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGL2Activity extends Activity {

    private static String TAG = "ziq";
    GLSurfaceView glSurfaceView;
    MyRenderer myRenderer;
    private String filePath="images/texture_360_n.jpg";
    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl2);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        glSurfaceView.setEGLContextClientVersion(2);
        myRenderer = new MyRenderer(OpenGlUtil.loadBitmapFromAssets(getBaseContext(), filePath));
        glSurfaceView.setRenderer(myRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);


        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                myRenderer.getSphereRenderer().setDeltaX(myRenderer.getSphereRenderer().getDeltaX() + distanceX / sDensity * sDamping);
                myRenderer.getSphereRenderer().setDeltaY(myRenderer.getSphereRenderer().getDeltaY() + distanceY / sDensity * sDamping);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        //使得glSurfaceView的onTouch能够监听ACTION_DOWN以外的事件
        glSurfaceView.setClickable(true);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG, "onTouch: "+motionEvent.toString());
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }




    class MyRenderer implements GLSurfaceView.Renderer {
        private int surfaceWidth,surfaceHeight;

        private int[] frameBuffers = null;
        private int[] frameBufferTextures = null;

        FullViewRenderer mFullViewRenderer;
        SphereRenderer mSphereRenderer;

        MyRenderer(Bitmap bitmap) {
            this.mFullViewRenderer = new FullViewRenderer(bitmap);
            mSphereRenderer = new SphereRenderer();
        }

        public SphereRenderer getSphereRenderer() {
            return mSphereRenderer;
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            mFullViewRenderer.onSurfaceCreated(gl10, eglConfig);
            mSphereRenderer.onSurfaceCreated(gl10, eglConfig);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int surfaceWidth, int surfaceHeight) {
            this.surfaceWidth = surfaceWidth;
            this.surfaceHeight = surfaceHeight;

            mFullViewRenderer.onSurfaceChanged(gl10, surfaceWidth, surfaceHeight);
            mSphereRenderer.onSurfaceChanged(gl10, surfaceWidth, surfaceHeight);


            if(frameBuffers != null){
                destroyFrameBuffers();
            }

            if (frameBuffers == null) {
                frameBuffers = new int[1];
                frameBufferTextures = new int[1];
                GLES20.glGenFramebuffers(1, frameBuffers, 0);
                GLES20.glGenTextures(1, frameBufferTextures, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextures[0]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, surfaceWidth, surfaceHeight, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, frameBufferTextures[0], 0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }

        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glFrontFace(GLES20.GL_CW);
            GLES20.glCullFace(GLES20.GL_BACK);
            GLES20.glEnable(GLES20.GL_CULL_FACE);

            GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            mFullViewRenderer.onDrawFrame(0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            GLES20.glViewport(0, 0 ,surfaceWidth, surfaceHeight);
            mSphereRenderer.onDrawFrame(frameBufferTextures[0]);

            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }

        public void onDestry(){
            mFullViewRenderer.onDestry();
            mSphereRenderer.onDestry();
            destroyFrameBuffers();
        }

        private void destroyFrameBuffers() {
            if (frameBufferTextures != null) {
                GLES20.glDeleteTextures(frameBufferTextures.length, frameBufferTextures, 0);
                frameBufferTextures = null;
            }
            if (frameBuffers != null) {
                GLES20.glDeleteFramebuffers(frameBuffers.length, frameBuffers, 0);
                frameBuffers = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        myRenderer.onDestry();
        super.onDestroy();
    }
}
