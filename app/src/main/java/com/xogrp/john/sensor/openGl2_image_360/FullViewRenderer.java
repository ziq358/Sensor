package com.xogrp.john.sensor.openGl2_image_360;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FullViewRenderer implements GLSurfaceView.Renderer {
        protected int surfaceWidth,surfaceHeight;
        Bitmap bitmap;

        private int imageTextureId;
        private int imageSize[];

        private int mProgramId;
        // 与 SphereRenderer 一样
        private String mVertexShader = "attribute vec4 aPosition;\n" +
                                        "attribute vec4 aTextureCoord;\n" +
                                        "varying vec2 vTextureCoord;\n" +
                                        "uniform mat4 uMVPMatrix;\n" +
                                        "void main() {\n" +
                                        "  gl_Position = uMVPMatrix * aPosition;\n" +
                                        "  vTextureCoord = aTextureCoord.xy;\n" +
                                        "}";

        private String mFragmentShader =    "precision mediump float;\n" +
                                            "varying vec2 vTextureCoord;\n" +
                                            "uniform sampler2D sTexture;\n" +
                                            "void main() {\n" +
                                            "    gl_FragColor=texture2D(sTexture, vTextureCoord);\n" +
                                            "}";

        private int maPositionHandle;
        private int maTextureCoordinateHandle;
        private int uMVPMatrixHandle;
        private int uTextureSamplerHandle;

        private FloatBuffer mVerticesBuffer;
        private FloatBuffer mTexCoordinateBuffer;
        private final float TRIANGLES_DATA_CW[] = {
                -1.0f, -1.0f, 0f, //LD
                -1.0f, 1.0f, 0f,  //LU
                1.0f, -1.0f, 0f,  //RD
                1.0f, 1.0f, 0f    //RU
        };

        public final float TEXTURE_NO_ROTATION[] = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        protected float[] projectionMatrix = new float[16];


        public FullViewRenderer(Bitmap bitmap) {
            this.bitmap = bitmap;
            mVerticesBuffer = ByteBuffer.allocateDirect(
                    TRIANGLES_DATA_CW.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(TRIANGLES_DATA_CW);
            mVerticesBuffer.position(0);

            mTexCoordinateBuffer =ByteBuffer.allocateDirect(
                    TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(TEXTURE_NO_ROTATION);
            mTexCoordinateBuffer.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

            imageTextureId = OpenGlUtil.getTextureFromBitmap(bitmap, imageSize);


            // 相当于从 Program 中取得相应变量 的引用
            mProgramId = OpenGlUtil.createProgram(mVertexShader, mFragmentShader);
            if (mProgramId == 0) {
                return;
            }
            maPositionHandle = GLES20.glGetAttribLocation(mProgramId, "aPosition");
            OpenGlUtil.checkGlError("glGetAttribLocation aPosition");
            if (maPositionHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aPosition");
            }
            maTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramId, "aTextureCoord");
            OpenGlUtil.checkGlError("glGetAttribLocation aTextureCoord");
            if (maTextureCoordinateHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aTextureCoord");
            }

            uTextureSamplerHandle= GLES20.glGetUniformLocation(mProgramId,"sTexture");
            OpenGlUtil.checkGlError("glGetUniformLocation uniform sTexture");

            uMVPMatrixHandle=GLES20.glGetUniformLocation(mProgramId,"uMVPMatrix");
            OpenGlUtil.checkGlError("glGetUniformLocation uMVPMatrix");



        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int surfaceWidth, int surfaceHeight) {
            this.surfaceWidth = surfaceWidth;
            this.surfaceHeight = surfaceHeight;
        }

        @Override
        public void onDrawFrame(GL10 gl10){

        }

        public void onDrawFrame(int textureId) {

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUseProgram(mProgramId);
            OpenGlUtil.checkGlError("glUseProgram");
            Matrix.setIdentityM(projectionMatrix,0);
            GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            if (imageTextureId != 0) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTextureId);
                GLES20.glUniform1i(uTextureSamplerHandle, 0);
            }

            mTexCoordinateBuffer.position(0);
            GLES20.glVertexAttribPointer(maTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTexCoordinateBuffer);
            OpenGlUtil.checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureCoordinateHandle);
            OpenGlUtil.checkGlError("glEnableVertexAttribArray maTextureHandle");

            mVerticesBuffer.position(0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVerticesBuffer);
            OpenGlUtil.checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            OpenGlUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");

            Matrix.setIdentityM(projectionMatrix,0);
            GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, projectionMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glDisable(GLES20.GL_BLEND);
        }

        public void onDestry(){
            GLES20.glDeleteProgram(mProgramId);
        }
}