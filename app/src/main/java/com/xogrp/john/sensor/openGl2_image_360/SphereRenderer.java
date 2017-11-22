package com.xogrp.john.sensor.openGl2_image_360;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by john on 22/11/2017.
 */

public class SphereRenderer implements GLSurfaceView.Renderer {

    protected int surfaceWidth,surfaceHeight;
    private int mtextureId;

    // 与 FullViewRenderer 一样
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

    private int mProgramId;

    private int maPositionHandle;
    private int maTextureCoordinateHandle;
    private int uMVPMatrixHandle;
    private int uTextureSamplerHandle;


    private Sphere mSphere;

    //Sphere/touch/sensor
    private float[] modelMatrix = new float[16];
    //gluLookAt
    private float[] viewMatrix = new float[16];
    //perspective/scaling
    private float[] projectionMatrix = new float[16];

    private float[] modelViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float mScale = 1;
    private float ratio;
    private float mDeltaX = -90;
    private float mDeltaY = 0;



    public SphereRenderer() {
        mSphere = new Sphere(18,75,150);

        Matrix.setIdentityM(modelMatrix,0);
        Matrix.setIdentityM(projectionMatrix,0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f,-1.0f,
                0.0f, 1.0f, 0.0f);

    }


    public float getDeltaX() {
        return mDeltaX;
    }

    public void setDeltaX(float mDeltaX) {
        this.mDeltaX = mDeltaX;
    }

    public float getDeltaY() {
        return mDeltaY;
    }

    public void setDeltaY(float mDeltaY) {
        this.mDeltaY = mDeltaY;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

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
        ratio=(float)surfaceWidth/ surfaceHeight;
    }

    @Override
    public void onDrawFrame(GL10 gl10){

    }

    public void onDrawFrame(int textureId) {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUseProgram(mProgramId);
        OpenGlUtil.checkGlError("glUseProgram");

        //根据句柄，， 添加 数据 buffer
        FloatBuffer textureBuffer = mSphere.getTexCoordinateBuffer();
        if (textureBuffer != null){
            textureBuffer.position(0);
            GLES20.glVertexAttribPointer(maTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
            OpenGlUtil.checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureCoordinateHandle);
            OpenGlUtil.checkGlError("glEnableVertexAttribArray maTextureHandle");
        }

        FloatBuffer vertexBuffer = mSphere.getVerticesBuffer();
        if (vertexBuffer != null){
            vertexBuffer.position(0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            OpenGlUtil.checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            OpenGlUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");
        }



        float currentDegree= (float) (Math.toDegrees(Math.atan(mScale))*2);
        Matrix.perspectiveM(projectionMatrix, 0, currentDegree, ratio, 1f, 500f);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        if (textureId != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(uTextureSamplerHandle, 0);
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);


        ShortBuffer indexBuffer = mSphere.getIndexBuffer();
        int indicesNum = mSphere.getIndicesNum();
        if (indexBuffer != null){
            indexBuffer.position(0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesNum, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, indicesNum);
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void onDestry(){
        GLES20.glDeleteProgram(mProgramId);
    }


    class Sphere {

        private FloatBuffer mVerticesBuffer;
        private FloatBuffer mTexCoordinateBuffer;
        private ShortBuffer mIndexBuffer;
        private int mNumIndices;

        public Sphere(float radius, int rings, int sectors) {
            final float PI = (float) Math.PI;
            final float PI_2 = (float) (Math.PI / 2);

            float R = 1f/(float)rings;
            float S = 1f/(float)sectors;
            short r, s;
            float x, y, z;

            int numPoint = (rings + 1) * (sectors + 1);

            float[] vertexs = new float[numPoint * 3];
            float[] texcoords = new float[numPoint * 2];
            short[] indices = new short[numPoint * 6];

            //map texture 2d-3d
            int t = 0, v = 0;
            for(r = 0; r < rings + 1; r++) {
                for(s = 0; s < sectors + 1; s++) {
                    x = (float) (Math.cos(2*PI * s * S) * Math.sin( PI * r * R ));
                    y = (float) Math.sin( -PI_2 + PI * r * R );
                    z = (float) (Math.sin(2*PI * s * S) * Math.sin( PI * r * R ));

                    texcoords[t++] = s*S;
                    texcoords[t++] = r*R;

                    vertexs[v++] = x * radius;
                    vertexs[v++] = y * radius;
                    vertexs[v++] = z * radius;
                }
            }

            //glDrawElements
            int counter = 0;
            int sectorsPlusOne = sectors + 1;
            for(r = 0; r < rings; r++){
                for(s = 0; s < sectors; s++) {
                    indices[counter++] = (short) (r * sectorsPlusOne + s);       //(a)
                    indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s));    //(b)
                    indices[counter++] = (short) ((r) * sectorsPlusOne + (s+1));  // (c)
                    indices[counter++] = (short) ((r) * sectorsPlusOne + (s+1));  // (c)
                    indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s));    //(b)
                    indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s+1));  // (d)
                }
            }

            // initialize vertex byte buffer for shape coordinates
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 4 bytes per float)
                    vertexs.length * 4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(vertexs);
            vertexBuffer.position(0);

            // initialize vertex byte buffer for shape coordinates
            ByteBuffer cc = ByteBuffer.allocateDirect(
                    texcoords.length * 4);
            cc.order(ByteOrder.nativeOrder());
            FloatBuffer texBuffer = cc.asFloatBuffer();
            texBuffer.put(texcoords);
            texBuffer.position(0);

            // initialize byte buffer for the draw list
            ByteBuffer dlb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 2 bytes per short)
                    indices.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            ShortBuffer indexBuffer = dlb.asShortBuffer();
            indexBuffer.put(indices);
            indexBuffer.position(0);

            mTexCoordinateBuffer=texBuffer;
            mVerticesBuffer=vertexBuffer;
            mIndexBuffer = indexBuffer;
            mNumIndices=indices.length;
        }

        public FloatBuffer getVerticesBuffer() {
            return mVerticesBuffer;
        }

        public FloatBuffer getTexCoordinateBuffer() {
            return mTexCoordinateBuffer;
        }

        public ShortBuffer getIndexBuffer() {
            return mIndexBuffer;
        }

        public int getIndicesNum() {
            return mNumIndices;
        }
    }


}
