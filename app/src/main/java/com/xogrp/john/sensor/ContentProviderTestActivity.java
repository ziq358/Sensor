package com.xogrp.john.sensor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by john on 22/02/2018.
 */

public class ContentProviderTestActivity extends AppCompatActivity implements View.OnClickListener{
    ContentResolver contentResolver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);
        findViewById(R.id.query).setOnClickListener(this);
        findViewById(R.id.insert).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.update).setOnClickListener(this);
        contentResolver = getContentResolver();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query:
                Log.e("ziq", "query: ");
                Cursor cursor = contentResolver.query(Uri.parse("content://com.xogrp.john.sensor.MusicProvider/user"),
                        new String[]{"title","content"}, null, null, null);
                if(cursor != null && cursor.moveToFirst()){
                    do {
                        Log.e("ziq", "query: ---------------");
                        int count = cursor.getColumnCount();
                        for (int i = 0; i < count; i++) {
                            Log.e("ziq", "query: "+cursor.getColumnName(i)+"  "+cursor.getString(i));
                        }
                    }while (cursor.moveToNext());
                    cursor.close();
                }

                break;
            case R.id.insert:
                Log.e("ziq", "insert: ");
                ContentValues contentValues = new ContentValues();
                contentValues.put("title","testTitle");
                contentValues.put("content","testContent");
                contentResolver.insert(Uri.parse("content://com.xogrp.john.sensor.MusicProvider/user"), contentValues);
                break;
            case R.id.delete:
                Log.e("ziq", "delete: ");
                contentResolver.delete(Uri.parse("content://com.xogrp.john.sensor.MusicProvider/user"), "test where", new String[]{"selectionArgs"});
                break;
            case R.id.update:
                Log.e("ziq", "update: ");
                break;
        }
    }
}
