package com.lc.im.init;

import android.content.Context;
import android.util.Log;

import com.example.mylibrary.BaseModulePlugin;
import com.example.mylibrary.ModuleServiceCenterProvider;
import com.example.mylibrary.PluginRegisterListener;
import com.lc.im.ImModuleServiceImpl;
import com.lc.im.ImRtmClient;

/**
 * created by lvchao 2023/5/5
 * describe:
 */
public class ImPlugin extends BaseModulePlugin {
    @Override
    public void init(Context context, String pluginName, PluginRegisterListener pluginRegisterListener) {
        try {
            Log.e("ImPlugin", "init"+context.toString());
            ModuleServiceCenterProvider.getInstance().getModuleServiceCenter().addService(pluginName, new ImModuleServiceImpl());
            ImRtmClient.INSTANCE.with(context);
        } catch (Exception e) {
            pluginRegisterListener.onFail(pluginName);
            return;
        }
        pluginRegisterListener.onSuccess();
    }
}
