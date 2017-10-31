package com.xogrp.john.sensor.wifi;

import android.net.Credentials;
import android.net.LinkProperties;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by john on 31/10/2017.
 */

public class WifiUtil {
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    public static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static WifiConfiguration getConfig(String ssid, String name, String password, int security) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + ssid + "\"";

        switch (security) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                if (password.length() != 0) {
                    int length = password.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58)
                            && password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;
            case SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;
            case SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                // 对应 修改参数
                config.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
                config.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);

                config.enterpriseConfig.setIdentity(name);
                config.enterpriseConfig.setAnonymousIdentity("");
                if (password.length() != 0) {
                    config.enterpriseConfig.setPassword(password);
                }
                break;
            default:
                return null;
        }
        return config;
    }

}
