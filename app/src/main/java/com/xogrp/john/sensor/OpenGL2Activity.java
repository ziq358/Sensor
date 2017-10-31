package com.xogrp.john.sensor;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGL2Activity extends AppCompatActivity {

    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl2);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);

    }

    class MyRenderer implements GLSurfaceView.Renderer {

        float[] vertices = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.5f, 1.0f
        };

        float[] textureVertices = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.5f, 1.0f
        };

        private FloatBuffer verticesBuffer;
        private FloatBuffer textureBuffer;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

            ByteBuffer buff = ByteBuffer.allocateDirect(vertices.length * 4);
            buff.order(ByteOrder.nativeOrder());
            verticesBuffer = buff.asFloatBuffer();
            verticesBuffer.put(vertices);
            verticesBuffer.position(0);

            buff = ByteBuffer.allocateDirect(textureVertices.length * 4);
            buff.order(ByteOrder.nativeOrder());
            textureBuffer = buff.asFloatBuffer();
            textureBuffer.put(textureVertices);
            textureBuffer.position(0);

        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {

        }

        @Override
        public void onDrawFrame(GL10 gl10) {

        }
    }

}
