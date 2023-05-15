package com.lc.repository.init;

import android.content.Context;
import android.util.Log;

import com.example.mylibrary.BaseModulePlugin;
import com.example.mylibrary.IModuleService;
import com.example.mylibrary.ModuleServiceCenterProvider;
import com.example.mylibrary.PluginRegisterListener;
import com.example.mylibrary.service.RepositoryModuleService;

public class RepositoryPlugin extends BaseModulePlugin {
    @Override
    public void init(Context context, String pluginName, PluginRegisterListener pluginRegisterListener) {
        try {
            Log.e("RepositoryPlugin", "init");
            RepositoryModuleServiceImpl repositoryModuleService=new RepositoryModuleServiceImpl();
            repositoryModuleService.attachContext(context);
            ModuleServiceCenterProvider.getInstance().getModuleServiceCenter().addService(pluginName, repositoryModuleService);
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

    private static class RepositoryModuleServiceImpl implements RepositoryModuleService {
        Context context;

        @Override
        public void attachContext(Context context) {
            this.context = context;
        }

        @Override
        public Context getContext() {
            return context;
        }
    }
}
