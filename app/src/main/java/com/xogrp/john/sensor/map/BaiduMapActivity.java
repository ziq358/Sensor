package com.xogrp.john.sensor.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.xogrp.john.sensor.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BaiduMapActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map);
        mMapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        findViewById(R.id.my_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLocation();
            }
        });

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList=locationManager.getProviders(true);
        if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider=LocationManager.GPS_PROVIDER;
        }else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
        }else {
            //当前没有可用的位置提供器时，弹出Toast提示
            Toast.makeText(this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        locationManager.requestLocationUpdates(provider,2000,10,locationListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private LocationManager locationManager;
    private  String provider = "";

    @SuppressLint("MissingPermission")
    private void myLocation(){

        //获取所有可用的位置提供器
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
            navigateTo(location);
            Log.e("ziq", "myLocation location : "+location.getLatitude()+" - "+location.getLongitude());
        }

//--------------------

        // 查找到服务信息
        Criteria criteria = new Criteria();
        // 高精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        // 低功耗
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 获取GPS信息
        String provider2 = locationManager.getBestProvider(criteria, true);
        // 通过GPS获取位置
        Location location2 = locationManager.getLastKnownLocation(provider2);
        if(location2 != null){
            Log.e("ziq", "myLocation location2 : "+location2.getLatitude()+" - "+location2.getLongitude());
        }

        locationManager.requestLocationUpdates(provider,2000,10,locationListener);

    }

    LocationListener locationListener =new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(locationManager!=null) navigateTo(location);
            if(location!=null){
                Log.e("ziq", "onLocationChanged location : "+location.getLatitude()+" - "+location.getLongitude());
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(doubles != null && doubles.length >= 2){
                LocationMode mCurrentMode = LocationMode.FOLLOWING;//定位跟随态
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(90)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(doubles[1])
                        .longitude(doubles[0]).build();

                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.glm_location);
                MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
                mBaiduMap.setMyLocationConfiguration(config);
            }

        }
    };

    double[] doubles;
    private void navigateTo(final Location location) {
        Log.e("ziq", "navigateTo location : "+location.getLatitude()+" - "+location.getLongitude());

        new Thread(new Runnable() {
            @Override
            public void run() {
                doubles = postBaidu(location.getLatitude(), location.getLongitude());
                Log.e("ziq", "navigateTo doubles : "+doubles[0]+" - "+doubles[1]);
                handler.handleMessage(new Message());
            }
        }).start();

    }


    public static double[] postBaidu(double lat, double lng) {
        double[] latlng = null;

        URL url = null;
        URLConnection connection = null;
        try {
            url = new URL("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + String.valueOf(lng) + "&y="
                    + String.valueOf(lat));
            connection = url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            out.flush();
            out.close();

            // 服务器的回应的字串，并解析
            String sCurrentLine;
            String sTotalString;
            sCurrentLine = "";
            sTotalString = "";
            InputStream l_urlStream;
            l_urlStream = connection.getInputStream();
            BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
            while ((sCurrentLine = l_reader.readLine()) != null) {
                if (!sCurrentLine.equals(""))
                    sTotalString += sCurrentLine;
            }
            // System.out.println(sTotalString);
            sTotalString = sTotalString.substring(1, sTotalString.length() - 1);
            // System.out.println(sTotalString);
            String[] results = sTotalString.split("\\,");
            if (results.length == 3) {
                if (results[0].split("\\:")[1].equals("0")) {
                    String mapX = results[1].split("\\:")[1];
                    String mapY = results[2].split("\\:")[1];
                    mapX = mapX.substring(1, mapX.length() - 1);
                    mapY = mapY.substring(1, mapY.length() - 1);
                    mapX = new String(Base64.decode(mapX, Base64.DEFAULT));
                    mapY = new String(Base64.decode(mapY, Base64.DEFAULT));
                    // System.out.println(mapX);
                    // System.out.println(mapY);
                    latlng = new double[] { Double.parseDouble(mapX), Double.parseDouble(mapY) };
                } else {
                    System.out.println("error != 0");
                }
            } else {
                System.out.println("String invalid!");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("GPS转百度坐标异常！");
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        Log.e("ziq","百度GPS===" + dateFormat1.format(new Date()) + " " + latlng[0] + " " + latlng[1]);
        return latlng;
    }

}
