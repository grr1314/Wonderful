package com.example.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessengerService extends Service {
    public static final String TAG = "MyMessenger";
    public static final int MSG_FROMCLIENT = 1000;
    public static final int MSG_FROMSERVER = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_FROMCLIENT:
                    Log.d(TAG, "receive message from client:" + msg.getData().getString("msg"));

                    Messenger messenger = msg.replyTo;
                    Message message = Message.obtain(null, MSG_FROMSERVER);
                    Bundle bundle = new Bundle();
                    bundle.putString("rep", "message form server");
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(mHandler).getBinder();
    }
}
