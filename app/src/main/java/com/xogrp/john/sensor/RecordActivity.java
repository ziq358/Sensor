package com.xogrp.john.sensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class RecordActivity extends AppCompatActivity {

    TextView result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        result = (TextView) findViewById(R.id.result);
    }

    public void onRecord(View view){
        Toast.makeText(this, "onRecord", Toast.LENGTH_SHORT).show();
    }

    public void onPlayRecord(View view){
        Toast.makeText(this, "onPlayRecord", Toast.LENGTH_SHORT).show();
        String url = result.getText().toString();
        if(!TextUtils.isEmpty(url)){

        }
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
}
