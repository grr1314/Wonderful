package com.example.myapplication.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.Book;
import com.example.myapplication.BookManager;
import com.example.myapplication.service.BookManagerService;

import java.util.List;

public class BookManagerActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(this, BookManagerService.class);
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
    }
    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BookManager bookManager=BookManager.Stub.asInterface(service);
            try {
                List<Book> list=bookManager.getBookList();
//                bookManager.addBook();
                Log.e("BookManagerActivity",list.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
