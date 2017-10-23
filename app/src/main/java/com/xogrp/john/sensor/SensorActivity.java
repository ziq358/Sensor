package com.xogrp.john.sensor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

public class SensorActivity extends AppCompatActivity implements SensorEventListener{
    SensorManager mSensorManager;
    Sensor mSensor;
    TextView text, bluetooth;
    BluetoothAdapter bluetoothAdapter;
    private String TAG = "ziq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        text = (TextView) findViewById(R.id.text);
        bluetooth = (TextView) findViewById(R.id.my_bluetooth);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 获取传感器的类型(TYPE_ACCELEROMETER:加速度传感器)
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);



        //蓝牙。。。。。。。。

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mBluetoothReceiver, filter);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mBluetoothReceiver, filter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkAccessFinePermission = ActivityCompat.checkSelfPermission(SensorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SensorActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                        Log.e(TAG, "没有权限，请求权限");
                        return;
                    }
                    Log.e(TAG, "已有定位权限");
                    //这里可以开始搜索操作
                    search();
                }
            }
        });
    }

    private void search(){
        if(!bluetoothAdapter.isEnabled()){
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler,100);
        }else{
            //获取本机蓝牙名称
            String name = bluetoothAdapter.getName();
            //获取本机蓝牙地址
            String address = bluetoothAdapter.getAddress();
            Log.d(TAG,"bluetooth name ="+name+" address ="+address);
            //获取已配对蓝牙设备
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            Log.d(TAG, "bonded device size ="+devices.size());
            for(BluetoothDevice bonddevice:devices){
                Log.d(TAG, "bonded device name ="+bonddevice.getName()+" address"+bonddevice.getAddress());
            }
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
//            startActivity(discoverableIntent);

            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,"mBluetoothReceiver action ="+action);
            if(BluetoothDevice.ACTION_FOUND.equals(action)){//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(scanDevice == null) return;
                Log.d(TAG, "ACTION_FOUND name="+scanDevice.getName()+"address="+scanDevice.getAddress());
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
            }
        }

    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mBluetoothReceiver);
    }

    // 当传感器的值改变的时候回调该方法
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float[] values = sensorEvent.values;
        Log.e("ziq", " "+values[0]);
        text.setText(" "+values[0]);

    }
    // 当传感器精度发生改变时回调该方法

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
