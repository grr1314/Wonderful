package com.example.login;

import android.content.Context;
import android.util.Log;

import com.example.mylibrary.service.LoginModuleService;

public class LoginModuleServiceImpl implements LoginModuleService {
    private Context context;

    @Override
    public void attachContext(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Context getContext() {
        return context.getApplicationContext();
    }

    @Override
    public boolean isLogin() {
        Log.e("LoginModuleServiceImpl","isLogin");
        return false;
    }

}
