package com.xogrp.john.sensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * author: wuyanqiang
 * 2018/12/3
 */
public class UniversalImageLoaderAty extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_image_loader);
        ImageView imageView = (ImageView) findViewById(R.id.image);
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);

        DisplayImageOptions.Builder displayOptionsBuilder = new DisplayImageOptions.Builder();
        displayOptionsBuilder.cacheOnDisk(true);
        //1M   或 10张
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this)
                .diskCacheSize(1024 * 1024)
                .diskCacheFileCount(10)
                .defaultDisplayImageOptions(displayOptionsBuilder.build());
        ImageLoaderConfiguration configuration = builder.build();
        ImageLoader.getInstance().init(configuration);
        ImageLoader.getInstance().displayImage("http://img3.imgtn.bdimg.com/it/u=496641143,3869583512&fm=200&gp=0.jpg", imageView);
    }
}
