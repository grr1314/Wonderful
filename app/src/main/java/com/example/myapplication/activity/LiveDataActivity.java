package com.example.myapplication.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.myapplication.bus.LiveDataBus;

public class LiveDataActivity extends BaseActivity {
    private MutableLiveData<String> liveData = new MutableLiveData<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        liveData.observe(this, result -> {
//            Log.e("LiveDataActivity", result);
//        });
//        liveData.setValue("123");
//        //确实是之后注册的也会收到消息
//        liveData.observe(this, result -> {
//            Log.e("LiveDataActivity", "new" + result);
//        });


        LiveDataBus liveDataBus = LiveDataBus.getInstance();
//        liveDataBus.getChannel("001",String.class).setValue("001");
        liveDataBus.getChannel("003",Integer.class).setValue(4);

        liveDataBus.observe(this, String.class, "001", s -> Log.e("LiveDataActivity", s))
                   .observe(this, Integer.class, "003", s -> Log.e("LiveDataActivity", "ws:"+s))
                   .observe(this, Integer.class, "003", integer -> Log.e("LiveDataActivity", integer.toString()));
//        liveDataBus.getChannel("003",Integer.class).setValue(5);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LiveDataBus.getInstance().getChannel("003",Integer.class).postValue(4);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LiveDataBus.getInstance().getChannel("002").setValue("002");
    }
}
