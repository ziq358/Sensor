package com.xogrp.john.sensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by john on 23/02/2018.
 */

public class FragmentStackActivity extends AppCompatActivity implements View.OnClickListener{
    FragmentManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_stack);
        findViewById(R.id.add_a).setOnClickListener(this);
        findViewById(R.id.add_b).setOnClickListener(this);
        findViewById(R.id.add_c).setOnClickListener(this);
        findViewById(R.id.add_d).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);
        manager = getSupportFragmentManager();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = manager.beginTransaction();
        switch (v.getId()){
            case R.id.add_a:
                transaction.add(R.id.content1, new FragmentA(), "fragmentA");
                transaction.addToBackStack("fragmentATransaction");
                transaction.commitAllowingStateLoss();
                break;
            case R.id.add_b:
                transaction.add(R.id.content2, new FragmentB(), "fragmentB");
                transaction.addToBackStack("fragmentBTransaction");
                transaction.commitAllowingStateLoss();
                break;
            case R.id.add_c:
                transaction.add(R.id.content3, new FragmentC(), "fragmentC");
                transaction.addToBackStack("fragmentCTransaction");
                transaction.commitAllowingStateLoss();
                break;
            case R.id.add_d:
                transaction.add(R.id.content4, new FragmentD(), "fragmentD");
                transaction.addToBackStack("fragmentDTransaction");
                transaction.commitAllowingStateLoss();
                break;
            case R.id.remove:
                transaction.remove(manager.findFragmentById(R.id.content1));
                transaction.addToBackStack("fragmentRemove");
                transaction.commitAllowingStateLoss();
                break;
        }
    }


    public static class FragmentA extends Fragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            Log.e("ziq", "a onCreate: "+this);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onResume() {
            Log.e("ziq", "a onResume: "+this);
            super.onResume();
        }

        @Override
        public void onDestroyView() {
            Log.e("ziq", "a onDestroyView: "+this);
            super.onDestroyView();
        }

        @Override
        public void onDestroy() {
            Log.e("ziq", "a onDestroy: "+this);
            super.onDestroy();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_stack_a, container, false);
        }
    }

    public static class FragmentB extends Fragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            Log.e("ziq", "b onCreate: "+this);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onResume() {
            Log.e("ziq", "b onResume: "+this);
            super.onResume();
        }

        @Override
        public void onDestroyView() {
            Log.e("ziq", "b onDestroyView: "+this);
            super.onDestroyView();
        }

        @Override
        public void onDestroy() {
            Log.e("ziq", "b onDestroy: "+this);
            super.onDestroy();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_stack_b, container, false);
        }
    }

    public static class FragmentC extends Fragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            Log.e("ziq", "c onCreate: "+this);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onResume() {
            Log.e("ziq", "c onResume: "+this);
            super.onResume();
        }

        @Override
        public void onDestroyView() {
            Log.e("ziq", "c onDestroyView: "+this);
            super.onDestroyView();
        }

        @Override
        public void onDestroy() {
            Log.e("ziq", "c onDestroy: "+this);
            super.onDestroy();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_stack_c, container, false);
        }
    }

    public static class FragmentD extends Fragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            Log.e("ziq", "d onCreate: "+this);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onResume() {
            Log.e("ziq", "d onResume: "+this);
            super.onResume();
        }

        @Override
        public void onDestroyView() {
            Log.e("ziq", "d onDestroyView: "+this);
            super.onDestroyView();
        }

        @Override
        public void onDestroy() {
            Log.e("ziq", "d onDestroy: "+this);
            super.onDestroy();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_stack_d, container, false);
        }
    }

}
