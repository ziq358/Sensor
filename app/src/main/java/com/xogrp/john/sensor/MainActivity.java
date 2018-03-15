package com.xogrp.john.sensor;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xogrp.john.sensor.map.BaiduMapActivity;
import com.xogrp.john.sensor.map.GoogleMapViewActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mlist;
    private static final String TAG = "ziq";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "MainActivity onCreate: 1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mlist = (ListView) findViewById(R.id.list_activity);
        mlist.setAdapter(new ActivityListAdapter(getData()));
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Model model  = (Model) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(MainActivity.this, model.cls);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        Log.e(TAG, "MainActivity onCreate: 2");// 没有调用
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        Log.e(TAG, "MainActivity onCreateView: name");//多次调用
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        Log.e(TAG, "MainActivity onCreateView: parent");//多次调用
        return super.onCreateView(parent, name, context, attrs);
    }
    @Override
    protected void onRestart() {
        Log.e(TAG, "MainActivity onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "MainActivity onStart: ");
        super.onStart();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "MainActivity onPostCreate: 1");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "MainActivity onResume: ");
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        Log.e(TAG, "MainActivity onPostResume: ");
        super.onPostResume();
    }

    @Override
    public void onAttachedToWindow() {
        Log.e(TAG, "MainActivity onAttachedToWindow: ");
        super.onAttachedToWindow();
    }

    @Nullable
    @Override
    public View onCreatePanelView(int featureId) {
        Log.e(TAG, "MainActivity onCreatePanelView: ");
        return super.onCreatePanelView(featureId);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "MainActivity onPause: ");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "MainActivity onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "MainActivity onStop: ");
        super.onStop();
    }


    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        Log.e(TAG, "MainActivity onPostCreate: 2");//没有调用
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e(TAG, "MainActivity onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "MainActivity onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onDetachedFromWindow() {
        Log.e(TAG, "MainActivity onDetachedFromWindow: ");
        super.onDetachedFromWindow();
    }

    private List<Model> getData(){
        List<Model> dataList = new ArrayList<>();
        dataList.add(new Model("传感器，蓝牙", SensorActivity.class));
        dataList.add(new Model("二维码", QRcodeActivity.class));
        dataList.add(new Model("Google 地图", GoogleMapViewActivity.class));
        dataList.add(new Model("百度 地图", BaiduMapActivity.class));
        dataList.add(new Model("JNI", JniTestActivity.class));
        dataList.add(new Model("OpenGl", OpenglActivity.class));
        dataList.add(new Model("OpenGl2", OpenGL2Activity.class));
        dataList.add(new Model("Wifi", WifiActivity.class));
        dataList.add(new Model("人脸识别", FaceDetectionActivity.class));
        dataList.add(new Model("拖动 dragger", DraggerActivity.class));
        dataList.add(new Model("Drawable", DrawableActivity.class));
        dataList.add(new Model("Path 绘制", PathOnDrawActivity.class));
        dataList.add(new Model("ContentProvider", ContentProviderTestActivity.class));
        dataList.add(new Model("FragmentStackActivity", FragmentStackActivity.class));
        dataList.add(new Model("MatrixCamera", MatrixCameraActivity.class));
        dataList.add(new Model("相机", CameraActivity.class));
        return dataList;
    }

    private class ActivityListAdapter extends BaseAdapter{

        private List<Model> mData;

        public ActivityListAdapter(List<Model> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_item, viewGroup, false);
            TextView textView = itemView.findViewById(R.id.name);
            textView.setText(((Model)getItem(i)).name);
            return itemView;
        }
    }

    private static class Model {

        public Model(String name, Class cls) {
            this.name = name;
            this.cls = cls;
        }

        String name;
        Class cls;
    }

}
