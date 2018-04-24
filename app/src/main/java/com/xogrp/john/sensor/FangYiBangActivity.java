package com.xogrp.john.sensor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class FangYiBangActivity extends AppCompatActivity {

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fang_yi_bang);
        result = (TextView) findViewById(R.id.result);
    }

    public void fangyi(View view){
        Toast.makeText(this, "fangyi", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    call();
                } catch (Exception e) {
                    Log.e("ziq", "fangyi: "+e);
                }
            }
        }).start();

    }

    public void guanji(View view){
        Toast.makeText(this, "guangji", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";


    public void call() throws java.lang.Exception{
        URL url = new URL("https://sai-pilot.msxiaobing.com/api/Translator/GetResponse?api-version=2018-04-15");
        String requestBody = "{\"request\":\"你喜欢我妈？\",\"requestType\":\"text\",\"sourcelang\":\"zh-cn\",\"targetlang\":\"en-us\",\"responseType\":\"text\"}";
        String appID = "XIz84k5lya2tboswj9";
        String secret = "0l68wzb9cpastoejfk4hgixqvd7u15nr";
        String userID = "e10adc3949ba59abbe56e057f20f883e";
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime()/1000;
        String verb = "Post";
        String path = url.getPath();
        String params = url.getQuery();

        String[] paramList = params.split("&");
        String[] headerList = {"x-msxiaoice-request-app-id:" + appID, "x-msxiaoice-request-user-id:" + userID};
        String signature = ComputeSignature(verb, path, paramList, headerList, requestBody, timestamp, secret);

        HttpsURLConnection con = BuildRequest(url, appID, userID, timestamp, signature, requestBody);
        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), ENCODING));
        String inputLine;
        final StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                result.setText(" "+ response.toString());
            }
        });
    }


    /**
     * Build request for SAI.
     * @param url : request url.
     * @param appID : app ID.
     * @param userID : user ID.
     * @param timestamp : the number of seconds since January 1, 1970, 00:00:00 UTC.
     * @param signature : computed from request url, headers and body.
     * @param requestBody : request body.
     * @return SAI request.
     **/
    public HttpsURLConnection BuildRequest(
            URL url,
            String appID,
            String userID,
            long timestamp,
            String signature,
            String requestBody) throws Exception {
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("x-msxiaoice-request-app-id", appID);
        con.setRequestProperty("x-msxiaoice-request-timestamp", String.valueOf(timestamp));
        con.setRequestProperty("x-msxiaoice-request-user-id", userID);
        con.setRequestProperty("x-msxiaoice-request-signature", signature);

        con.setDoOutput(true);
        byte[] outputBytes = requestBody.getBytes(ENCODING);
        OutputStream os = con.getOutputStream();
        os.write( outputBytes );
        os.close();

        return con;
    }

    /**
     * Compute signature to be used in header.
     * @param verb : request method.
     * @param path : request path.
     * @param paramList : request param list.
     * @param headerList : request header list.
     * @param requestBody : request body.
     * @param timestamp : the number of seconds since January 1, 1970, 00:00:00 UTC.
     * @param secret : user secret key.
     * @return signature : signature to be used in header.
     **/
    public String ComputeSignature(
            String verb,
            String path,
            String[] paramList,
            String[] headerList,
            String requestBody,
            long timestamp,
            String secret) throws Exception {
        verb = verb.toLowerCase();
        path = path.toLowerCase();
        Arrays.sort(paramList);
        String strParam = getStringWithDelimiter("&", paramList);

        if(headerList != null){
            for (int i = 0; i < headerList.length; i++){
                headerList[i] = headerList[i].toLowerCase();
            }
        }
        Arrays.sort(headerList);
        String strHeader = getStringWithDelimiter(",", headerList);
        String content = getStringWithDelimiter(";", new String[]{verb, path, strParam, strHeader, requestBody, String.valueOf(timestamp), secret});
        System.out.println("content: " + content);

        byte[] computedHash = HmacSHA1Encrypt(content, secret);

//            String base64 = Base64.getEncoder().encodeToString(computedHash);
        String base64 = Base64.encodeToString(computedHash, Base64.NO_WRAP);
        System.out.println("signature: " + base64);
        return base64;
    }

    public String getStringWithDelimiter(CharSequence delimiter, CharSequence... elements){
        StringBuilder stringBuilder = new StringBuilder();
        int length = elements.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(elements[i]);
            if(i < length - 1){
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * HmacSHA1 Encrypt.
     * @param encryptText : content to be encrypted.
     * @param encryptKey : secret key.
     * @return Encrypted bytes.
     **/
    public byte[] HmacSHA1Encrypt(
            String encryptText,
            String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        byte[] digest = mac.doFinal(text);
        return digest;
    }
}
