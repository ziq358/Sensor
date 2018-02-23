package com.xogrp.john.sensor.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 22/02/2018.
 */

public class MusicContentProvider extends ContentProvider{

    private final static String TAG = "ziq";
    private List<MusicData> dataList = new ArrayList<>();
    private static final String[] COLUMN_NAME = new String[]{"_id", "title", "content"};

    @Override
    public boolean onCreate() {
        Log.e(TAG, "MusicContentProvider onCreate: "+android.os.Process.myPid());

        //android:process=":core"
        //android:multiprocess="true"
        // provider不会随应用的启动而加载，当调用到provider的时候才会加载，
        // 加载时provider是在调用者的进程中初始化的。
        // 这时候可能定义provider的  core进程还没有启动。其他app 调用时，core进程会启动

        for (int i = 0; i < 3; i++) {
            dataList.add(new MusicData("title "+i, "content "+i));
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.e(TAG, "query: "+uri.toString());
        Log.e(TAG, "query: "+projection.toString());
        Log.e(TAG, "query: "+selection);
        Log.e(TAG, "query: "+selectionArgs);
        Log.e(TAG, "query: "+sortOrder);
        MatrixCursor matrixCursor = new MatrixCursor(COLUMN_NAME);
        for (int i = 0; i < dataList.size(); i++) {
            matrixCursor.addRow(new Object[]{i, dataList.get(i).title, dataList.get(i).content});
        }
        return matrixCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.e(TAG, "getType: "+uri.toString());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.e(TAG, "insert: "+uri.toString());
        Log.e(TAG, "insert: "+values.toString());
        dataList.add(new MusicData("title "+dataList.size(), "content "+dataList.size()));
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.e(TAG, "delete: "+uri.toString());
        Log.e(TAG, "delete: "+selection);
        Log.e(TAG, "delete: "+selectionArgs);
        if(!dataList.isEmpty()){
            dataList.remove(dataList.size() - 1);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.e(TAG, "update: "+uri.toString());
        return 0;
    }

    public static class MusicData{
        String title;
        String content;

        public MusicData(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

}
