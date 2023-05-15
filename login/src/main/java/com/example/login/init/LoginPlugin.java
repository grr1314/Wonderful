package com.example.login.init;

import android.content.Context;
import android.util.Log;

import com.example.login.LoginModuleServiceImpl;
import com.example.mylibrary.BaseModulePlugin;
import com.example.mylibrary.ModuleServiceCenterProvider;
import com.example.mylibrary.PluginRegisterListener;

public class LoginPlugin extends BaseModulePlugin {
    @Override
    public void init(Context context, String pluginName, PluginRegisterListener pluginRegisterListener) {
        try {
            Log.e("LoginPlugin", "init");
            ModuleServiceCenterProvider.getInstance().getModuleServiceCenter().addService(pluginName, new LoginModuleServiceImpl());
        } catch (Exception e) {
            pluginRegisterListener.onFail(pluginName);
            return;
        }
        pluginRegisterListener.onSuccess();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

}
