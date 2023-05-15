package com.lc.im.init;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.mylibrary.BaseModulePlugin;
import com.example.mylibrary.ModuleInfoTable;
import com.example.mylibrary.ModuleServiceCenterProvider;
import com.example.mylibrary.PluginRegisterListener;
import com.example.mylibrary.service.CenterModuleService;
import com.lc.im.ImModuleServiceImpl;
import com.lc.im.agora.AgoraImClient;
import com.lc.im.hyphenate.HyImClient;
import com.tencent.mmkv.MMKV;

/**
 * created by lvchao 2023/5/5
 * describe:
 */
public class ImPlugin extends BaseModulePlugin {
    @Override
    public void init(Context context, String pluginName, PluginRegisterListener pluginRegisterListener) {
        try {
            Log.e("ImPlugin", "init" + context.toString());
            ModuleServiceCenterProvider.getInstance().getModuleServiceCenter().addService(pluginName, new ImModuleServiceImpl());
            AgoraImClient.INSTANCE.with(context);
            HyImClient.INSTANCE.with(context);
        } catch (Exception e) {
            pluginRegisterListener.onFail(pluginName);
            return;
        }
        pluginRegisterListener.onSuccess();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onResume() {
        Log.d("ImPlugin", "onResume");
        //判断是否已经登录，如果是则start否则不会start
        MMKV kv = MMKV.defaultMMKV();
        boolean state = kv.decodeBool("imLoginState", false);
        if (state) {
            String token = kv.decodeString("token");
            String userId = kv.decodeString("userId");
            AgoraImClient.INSTANCE.login(token, userId);
            AgoraImClient.INSTANCE.start();
        }
        CenterModuleService centerModuleService = (CenterModuleService) ModuleServiceCenterProvider.getInstance().getModuleServiceCenter().getService(ModuleInfoTable.MODULE_CENTER);
        HyImClient.INSTANCE.setExecutorService(centerModuleService.globalExecutorService());
        HyImClient.INSTANCE.start();
    }

    @Override
    public void onStop() {
        Log.d("ImPlugin", "onStop");
        AgoraImClient.INSTANCE.stop();
    }

    @Override
    public void onDestroy() {
        Log.d("ImPlugin", "onDestroy");
        AgoraImClient.INSTANCE.clear();
    }
}
