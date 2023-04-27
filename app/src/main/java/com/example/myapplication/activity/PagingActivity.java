package com.example.myapplication.activity;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.example.myapplication.R;
import com.example.myapplication.fragmemt.Paging3Fragment;
import com.sjk.apt_annotation.Route;

@Route(pageName = "page", action = "jump", tradeLine = "ac")
public class PagingActivity extends BaseActivity {
    private Button add;
    private Button remove;
    private Paging3Fragment fragment;
    Paging3Fragment paging3Fragment = new Paging3Fragment();
    Fragment paging3Fragment1 = new Paging3Fragment();
    Handler mainHandler = new Handler(Looper.getMainLooper());

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            add.setText("我是：  ");
//            textView.postDelayed(this,2500);

//            int i = 1;
//            while (i < 500_000_000) {
//                i++;
//            }
            add.postDelayed(this, 16);
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        mainHandler.post(runnable);


        remove.setOnClickListener(v -> {
            try {
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
//                // 2、获取FragmentManager的实例
//                FragmentManager fm = getSupportFragmentManager();
//                // 3、开启FragmentTransaction事务
//                FragmentTransaction beginTransaction;
//                beginTransaction = fm.beginTransaction();
//                beginTransaction.remove(paging3Fragment);
//                beginTransaction.commit();
        });
        add.setOnClickListener(v -> {
//                addFragment();
        });
//        addFragment();
        Log.e("PagingActivity", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("PagingActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("PagingActivity", "onStop");


    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fm = getSupportFragmentManager();
        fragment = (Paging3Fragment) fm.findFragmentById(R.id.fragment);
        Bundle b = new Bundle();
        b.putString("key", "123");
        fragment.setArguments(b);
        Log.e("PagingActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("PagingActivity", "onPause");
    }

    private void addFragment() {
//        // 1、创建Fragment对象
//        Paging3Fragment paging3Fragment1 = new Paging3Fragment();
//        // 2、获取FragmentManager的实例
        FragmentManager fm = getSupportFragmentManager();
//        // 3、开启FragmentTransaction事务
        FragmentTransaction beginTransaction;
        beginTransaction = fm.beginTransaction();
        // 4、向Activity布局器添加两个Fragment
        if (!paging3Fragment.isAdded())
            beginTransaction.add(R.id.container, paging3Fragment);
        if (!paging3Fragment1.isAdded())
            beginTransaction.add(R.id.container1, paging3Fragment1);
//        beginTransaction.add(paging3Fragment1,"paging3Fragment1");

//        beginTransaction.replace(R.id.container,paging3Fragment);
//        beginTransaction.addToBackStack(null);
//        beginTransaction.add(R.id.container,paging3Fragment1);
//        beginTransaction.addToBackStack(null);
//        beginTransaction.replace(R.id.menu,menuFragment);
        // 5、提交事务
        beginTransaction.commit();
        beginTransaction.commitNow();
        beginTransaction.commitAllowingStateLoss();
//        beginTransaction.commitNowAllowingStateLoss();
        fm.executePendingTransactions();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("PagingActivity", "onDestroy");
    }
}
