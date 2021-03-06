package com.xogrp.john.sensor;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class RecordActivity extends AppCompatActivity {

    TextView result;
    MediaRecorder mMediaRecorder;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "media.wav";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        result = (TextView) findViewById(R.id.result);
    }

    public void onRecord(View view){
        Toast.makeText(this, "onRecord", Toast.LENGTH_SHORT).show();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(path);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            mMediaRecorder.release();
        }
    }

    public void onStop(View view){
        if(mMediaRecorder != null){
            mMediaRecorder.stop();
        }
    }

    public void onFanyi(View view){
        Toast.makeText(this, "onFanyi", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    call(path);
                } catch (Exception e) {
                    Log.e("ziq", "fangyi: "+e);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        mMediaRecorder.release();
        super.onDestroy();
    }

    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.NO_WRAP);

    }


    public static void decoderBase64File(String base64Code, String targetPath)
            throws Exception {
        byte[] buffer =  Base64.decode(base64Code, Base64.NO_WRAP);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();

    }


    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";


    public void call(String filePath) throws java.lang.Exception{
        URL url = new URL("https://sai-pilot.msxiaobing.com/api/Translator/GetResponse?api-version=2018-04-15");
//        String requestBody = "{\"request\":\"你喜欢我妈？\",\"requestType\":\"text\",\"sourcelang\":\"zh-cn\",\"targetlang\":\"en-us\",\"responseType\":\"text\"}";
//        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "media.wav";
//        String filePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "media2.wav";
//        String fileContent = RecordActivity.encodeBase64File(filePath);
//        RecordActivity.decoderBase64File(fileContent, filePath2);
//        String requestBody = "{\"request\":\""+fileContent+"\",\"requestType\":\"wavaudio\",\"sourcelang\":\"zh-cn\",\"targetlang\":\"en-us\",\"responseType\":\"wavaudioandtext\"}";

        String fileContent = RecordActivity.encodeBase64File(filePath);
        String requestBody = "{\"request\":\""+fileContent+"\",\"requestType\":\"wavaudio\",\"sourcelang\":\"zh-cn\",\"targetlang\":\"en-us\",\"responseType\":\"wavaudioandtext\"}";
        String appID = "XIz84k5lya2tboswj9";
        String secret = "0l68wzb9cpastoejfk4hgixqvd7u15nr";
        String userID = "e10adc3949ba59abbe56e057f20f883e";
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime()/1000;
        String verb = "Post";
        String path = url.getPath();
        String params = url.getQuery();

        System.out.println(requestBody);

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

        JSONObject resultJson = new JSONObject(response.toString());
        if(resultJson.has("audio")){
            String resultUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "media2result.wav";
            RecordActivity.decoderBase64File(resultJson.getString("audio"), resultUrl);
        }

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
