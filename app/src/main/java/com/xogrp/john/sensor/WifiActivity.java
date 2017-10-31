package com.xogrp.john.sensor;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xogrp.john.sensor.wifi.WifiUtil;

import java.util.List;


/**
 * Created by john on 31/10/2017.
 */

public class WifiActivity extends AppCompatActivity implements View.OnClickListener{

    WifiManager wifiManager;

    WifiInfo wifiInfo;

    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;

    ListView listView;
    WifiListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        wifiManager = (WifiManager) getBaseContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        findViewById(R.id.open_wifi).setOnClickListener(this);
        findViewById(R.id.close_wifi).setOnClickListener(this);
        findViewById(R.id.scan_wifi).setOnClickListener(this);

        listView = (ListView) findViewById(R.id.wifi_list);
        adapter = new WifiListAdapter(null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ScanResult scanResult  = (ScanResult) adapterView.getAdapter().getItem(i);
                final Context context = WifiActivity.this;
                final AlertDialog.Builder alertBuilder =new AlertDialog.Builder(context);
                alertBuilder.setTitle(scanResult.SSID);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.wifi_connect_dialog, null, false);

                alertBuilder.setView(dialogView);


                final EditText nameEdit = dialogView.findViewById(R.id.name);
                final EditText passwordEdit = dialogView.findViewById(R.id.password);
                TextView link = dialogView.findViewById(R.id.connect_wifi);
                TextView cancel = dialogView.findViewById(R.id.cancel);

                final android.app.AlertDialog alertDialog = alertBuilder.create();
                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WifiConfiguration wifiConfiguration = IsExsits(scanResult.SSID);
                        if(wifiConfiguration != null){
                            addAndConnectNetwork(wifiConfiguration);
                        }else{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                wifiConfiguration = WifiUtil.getConfig(scanResult.SSID, nameEdit.getText().toString(), passwordEdit.getText().toString(), WifiUtil.getSecurity(scanResult));
                                addAndConnectNetwork(wifiConfiguration);
                            }else{
                                Toast.makeText(getBaseContext(),"版本要大于4.3", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }

    private class WifiListAdapter extends BaseAdapter {

        private List<ScanResult> mData;

        public WifiListAdapter(List<ScanResult> data) {
            mData = data;
        }

        public void setData(List<ScanResult> mData) {
            this.mData = mData;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
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
            textView.setText(((ScanResult)getItem(i)).SSID);
            return itemView;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.open_wifi:
                openWifi();
                break;
            case R.id.close_wifi:
                closeWifi(getBaseContext());
                break;
            case R.id.scan_wifi:
                adapter.setData(startScan(getBaseContext()));
                break;
        }
    }


    public void openWifi(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }else if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING){
            Toast.makeText(getBaseContext(),"亲，Wifi正在开启，不用再开了", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getBaseContext(),"亲，Wifi已经开启,不用再开了    "+ wifiManager.getWifiState(), Toast.LENGTH_SHORT).show();
        }
    }

    public void closeWifi(Context context) {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }else if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED){
            Toast.makeText(context,"亲，Wifi已经关闭，不用再关了", Toast.LENGTH_SHORT).show();
        }else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            Toast.makeText(context,"亲，Wifi正在关闭，不用再关了", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"请重新关闭  "+ wifiManager.getWifiState(), Toast.LENGTH_SHORT).show();
        }
    }


    public List<ScanResult> startScan(Context context) {

        wifiInfo = wifiManager.getConnectionInfo();

        Log.e("ziq", "startScan: ");
        wifiManager.startScan();
        // 得到扫描结果
        Log.e("ziq", "getScanResults: ");
        mWifiList = wifiManager.getScanResults();
        // 得到配置好的网络连接
        Log.e("ziq", "getConfiguredNetworks: ");
        mWifiConfiguration = wifiManager.getConfiguredNetworks();
        Log.e("ziq", "endScan: ");
        if (mWifiList == null) {
            if(wifiManager.getWifiState()==3){
                Toast.makeText(context,"当前区域没有无线网络", Toast.LENGTH_SHORT).show();
            }else if(wifiManager.getWifiState()==2){
                Toast.makeText(context,"WiFi正在开启，请稍后重新点击扫描", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"WiFi没有开启，无法扫描", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.e("ziq", "mWifiList: "+mWifiList.size());
            for (ScanResult scanResult:mWifiList) {
                Log.e("ziq", "     "+scanResult.SSID);
            }
            Log.e("ziq", "-----------------");
            Log.e("ziq", "-----------------");
            if(mWifiConfiguration != null){
                Log.e("ziq", "mWifiConfiguration: "+mWifiConfiguration.size());
                for (WifiConfiguration wifiConfiguration:mWifiConfiguration) {
                    Log.e("ziq", "    "+wifiConfiguration.SSID);
                }
            }
        }

        if(wifiInfo != null && null != wifiInfo.getSSID()){
            Log.e("ziq", "wifiInfo getSSID: "+wifiInfo.getSSID());
            Log.e("ziq", "wifiInfo getBSSID: "+wifiInfo.getBSSID());
            Log.e("ziq", "wifiInfo getHiddenSSID: "+wifiInfo.getHiddenSSID());
            Log.e("ziq", "wifiInfo getLinkSpeed: "+wifiInfo.getLinkSpeed());
            Log.e("ziq", "wifiInfo getMacAddress: "+wifiInfo.getMacAddress());
            Log.e("ziq", "wifiInfo getNetworkId: "+wifiInfo.getNetworkId());
            Log.e("ziq", "wifiInfo getRssi: "+wifiInfo.getRssi());
            Log.e("ziq", "wifiInfo getSupplicantState: "+wifiInfo.getSupplicantState());
            Log.e("ziq", "wifiInfo getDetailedStateOf: "+wifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()));
        }else {
            Log.e("ziq","没有连接到wifi");
        }

        return mWifiList;

    }

    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

    // 添加一个网络并连接
    public void addAndConnectNetwork(WifiConfiguration wcg) {
        int wcgID = wifiManager.addNetwork(wcg);
        boolean b =  wifiManager.enableNetwork(wcgID, true);
        if(b){
            Toast.makeText(getBaseContext(),"连接 成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(),"连接 失败", Toast.LENGTH_SHORT).show();
        }
    }

}
