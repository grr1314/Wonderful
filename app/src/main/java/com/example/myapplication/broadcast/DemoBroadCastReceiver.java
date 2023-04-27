package com.example.myapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DemoBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(),"jieshou",Toast.LENGTH_SHORT).show();
    }
}
