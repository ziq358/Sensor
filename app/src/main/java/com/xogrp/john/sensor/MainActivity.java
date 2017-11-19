package com.xogrp.john.sensor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private List<Model> getData(){
        List<Model> dataList = new ArrayList<>();
        dataList.add(new Model("传感器，蓝牙", SensorActivity.class));
        dataList.add(new Model("二维码", QRcodeActivity.class));
        dataList.add(new Model("Google 地图", GoogleMapViewActivity.class));
        dataList.add(new Model("百度 地图", BaiduMapActivity.class));
        dataList.add(new Model("JNI", JniTestActivity.class));
        dataList.add(new Model("Drawable", DrawableActivity.class));
        dataList.add(new Model("OpenGl", OpenglActivity.class));
        dataList.add(new Model("OpenGl2", OpenGL2Activity.class));
        dataList.add(new Model("Wifi", WifiActivity.class));
        dataList.add(new Model("人脸识别", FaceDetectionActivity.class));
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
