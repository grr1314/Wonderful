package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.center.CenterPlugin;
import com.example.mylibrary.ModuleInfoTable;
import com.example.mylibrary.PluginRegisterListener;
import com.lc.nativelib.AppMonitor;
import com.lc.nativelib.configs.Config;
import com.lc.nativelib.configs.AnrConfig;
import com.lc.nativelib.listener.AnrListener;
import com.lc.nativelib.model.MessageInfo;
import com.lc.routerlib.BuildConfig;
import com.lc.routerlib.IGlobalNavigationCallback;
import com.lc.routerlib.core.RouteBus;
import com.lc.routerlib.core.ZRouter;

import java.io.File;
import java.util.ArrayDeque;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ZRouter.init(base);
        ZRouter.setGlobalListener(new IGlobalNavigationCallback() {
            @Override
            public void onSuccess(Context context, RouteBus routeBus) {
                Log.w("App", "success");
            }

            @Override
            public void onFailed(RouteBus routeBus, int errorCode) {
                Log.w("App", "error:" + "code is" + errorCode + "");
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initPlugins(this);
    }

    private void initPlugins(Context base) {
        AnrConfig anrConfig = new AnrConfig();
        anrConfig.setGupTime(20);
        anrConfig.setWarningTime(200);
        Config config = new Config();
        config.add(anrConfig);
        AppMonitor appMonitor = new AppMonitor.Builder()
                .with(base)
                .addConfig(config)
                .isDebug(BuildConfig.DEBUG)
                .addAnrListener(new AnrListener() {
                    @Override
                    public void singleWarningMessage(MessageInfo info) {
                        Log.w("AppMonitor", info.toString());
                    }

                    @Override
                    public void anrEvent(ArrayDeque<MessageInfo> arrayDeque) {
                        Log.w("AppMonitor", "anrEvent:"+arrayDeque.size() + "");
                    }

                    @Override
                    public void uploadAnrFile(File anrFile) {
                        Log.w("AppMonitor", "uploadAnrFile:"+anrFile.getAbsolutePath() + "");
                    }
                })
                .build();
        appMonitor.startMonitor();
        CenterPlugin.getInstance().init(base);
        CenterPlugin.getInstance().startLoadModulePlugin(ModuleInfoTable.DEFAULT_MODULE_LIST, new PluginRegisterListener() {
            @Override
            public void onSuccess() {
                Log.e("initPlugins", "onSuccess");
            }

            @Override
            public void onFail(String pluginName) {

            }
        });
    }
}
