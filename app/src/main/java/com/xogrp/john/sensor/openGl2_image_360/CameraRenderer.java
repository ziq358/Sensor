package com.xogrp.john.sensor.openGl2_image_360;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.xogrp.john.sensor.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.xogrp.john.sensor.CameraGLActivity.NO_TEXTURE;

public class CameraRenderer implements GLSurfaceView.Renderer {
        protected int surfaceWidth,surfaceHeight;

        private int mProgramId;
        private String mVertexShader =  "";
        private String mFragmentShader ="";

    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;

    private int mTextureTransformMatrixLocation;
    private int mSingleStepOffsetLocation;
    private int mParamsLocation;

        private FloatBuffer mVerticesBuffer;
        private FloatBuffer mTexCoordinateBuffer;
        private final float TRIANGLES_DATA_CW[] = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        };

        public final float TEXTURE_NO_ROTATION[] = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };

        protected float[] projectionMatrix = new float[16];


        public CameraRenderer(Context context) {
            mVertexShader = readShaderFromRawResource(context, R.raw.default_vertex);
            mFragmentShader = readShaderFromRawResource(context, R.raw.default_fragment);

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

            // 相当于从 Program 中取得相应变量 的引用
            mProgramId = OpenGlUtil.createProgram(mVertexShader, mFragmentShader);
            if (mProgramId == 0) {
                return;
            }

            mGLAttribPosition = GLES20.glGetAttribLocation(mProgramId, "position");
            OpenGlUtil.checkGlError("glGetAttribLocation position");
            if (mGLAttribPosition == -1) {
                throw new RuntimeException("Could not get attrib location for position");
            }
            mGLUniformTexture = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture");
            OpenGlUtil.checkGlError("glGetAttribLocation inputImageTexture");
            if (mGLUniformTexture == -1) {
                throw new RuntimeException("Could not get attrib location for inputImageTexture");
            }
            mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mProgramId, "inputTextureCoordinate");
            OpenGlUtil.checkGlError("glGetAttribLocation mGLAttribTextureCoordinate");
            if (mGLAttribTextureCoordinate == -1) {
                throw new RuntimeException("Could not get attrib location for mGLAttribTextureCoordinate");
            }

            mTextureTransformMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "textureTransform");
            mSingleStepOffsetLocation = GLES20.glGetUniformLocation(mProgramId, "singleStepOffset");
            mParamsLocation = GLES20.glGetUniformLocation(mProgramId, "params");
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int surfaceWidth, int surfaceHeight) {
            this.surfaceWidth = surfaceWidth;
            this.surfaceHeight = surfaceHeight;
        }

        @Override
        public void onDrawFrame(GL10 gl10){

        }

        public void setTextureTransformMatrix(float[] mtx){
            projectionMatrix = mtx;
        }

        public void onDrawFrame(int textureId) {
            GLES20.glUseProgram(mProgramId);
            OpenGlUtil.checkGlError("glUseProgram");

            mVerticesBuffer.position(0);
            GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mVerticesBuffer);
            OpenGlUtil.checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(mGLAttribPosition);
            OpenGlUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");

            mTexCoordinateBuffer.position(0);
            GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTexCoordinateBuffer);
            OpenGlUtil.checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
            OpenGlUtil.checkGlError("glEnableVertexAttribArray maTextureHandle");

            GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, projectionMatrix, 0);
            if(textureId != NO_TEXTURE){
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
                GLES20.glUniform1i(mGLUniformTexture, 0);
            }
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(mGLAttribPosition);
            GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        }

        public void onDestry(){
            GLES20.glDeleteProgram(mProgramId);
        }

    public static String readShaderFromRawResource(Context context, final int resourceId){
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try{
            while ((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e){
            return null;
        }
        return body.toString();
    }
}