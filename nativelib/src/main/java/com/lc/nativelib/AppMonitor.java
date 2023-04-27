package com.lc.nativelib;

import android.content.Context;
import android.os.Looper;
import android.util.Printer;

import com.google.gson.Gson;
import com.lc.nativelib.configs.AnrConfig;
import com.lc.nativelib.configs.Config;
import com.lc.nativelib.display.MonitorDisplayActivity;
import com.lc.nativelib.file.FileManager;
import com.lc.nativelib.listener.AnrListener;
import com.lc.nativelib.listener.IConfig;
import com.lc.nativelib.listener.IMonitor;
import com.lc.nativelib.monitors.AnrMonitor;
import com.lc.nativelib.service.AnrDisplayService;
import com.lc.nativelib.service.AnrUploadService;
import com.lc.nativelib.window.FloatClient;
import com.lc.nativelib.window.FloatHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AppMonitor implements Printer {
    private boolean isDebug = true;
    private boolean alreadyBegun = false;
    private Config config;
    private AnrListener anrListener;
    private Context mContext;
    private final Map<Class<?>, IMonitor> monitorMap = new HashMap<>();
    private FloatHelper floatHelper;
    private static final int DEFAULT_THREAD_COUNT = 10;
    private ExecutorService executorService;

    private AppMonitor() {
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
    }

    public Map<Class<?>, IMonitor> getMonitorMap() {
        return monitorMap;
    }

    @Override
    public void println(String x) {
        //分发到每一个monitor
        for (Map.Entry<Class<?>, IMonitor> next : monitorMap.entrySet()) {
            if (next.getValue().monitorState()) {
                next.getValue().println(x);
            }
        }
    }

    private static class AppMonitorHolder {
        private static final AppMonitor appMonitor = new AppMonitor();
    }

    public static AppMonitor get() {
        return AppMonitorHolder.appMonitor;
    }

    public static class Builder {
        private boolean isDebug = true;
        private Config config;
        private AnrListener anrListener;
        private Context mContext;

        public Builder addConfig(Config config) {
            this.config = config;
            return this;
        }

        public Builder isDebug(boolean debug) {
            this.isDebug = debug;
            return this;
        }

        public Builder addAnrListener(AnrListener anrListener) {
            this.anrListener = anrListener;
            return this;
        }

        public AppMonitor build() {
            AppMonitor appMonitor = AppMonitor.get();
            appMonitor.isDebug = this.isDebug;
            appMonitor.config = this.config;
            appMonitor.anrListener = this.anrListener;
            appMonitor.mContext = this.mContext;
            return appMonitor;
        }

        public Builder with(Context context) {
            if (context.getApplicationContext() == null) {
                this.mContext = context;
            } else
                this.mContext = context.getApplicationContext();
            return this;
        }
    }

    public void stop() {
        Looper.getMainLooper().setMessageLogging(null);
        for (Map.Entry<Class<?>, IMonitor> next : monitorMap.entrySet()) {
            IMonitor current = next.getValue();
            if (current.monitorState()) {
                current.stopMonitor();
            }
        }
    }

    private void initWindow() {
        if (floatHelper == null) {
            //定义悬浮窗助手
            floatHelper = new FloatClient.Builder()
                    .with(mContext)
//                .addView(LayoutInflater.from(mContext).inflate(R.layout.window_view, null, false))
                    //是否需要展示默认权限提示弹窗，建议使用自己的项目中弹窗样式（默认开启）
                    .enableDefaultPermissionDialog(false)
                    .setClickTarget(MonitorDisplayActivity.class)
                    .addPermissionCallback(b -> {

                    })
                    .build();
        }
        floatHelper.show();
    }

    public void start() {
        if (!alreadyBegun) {
            Looper.getMainLooper().setMessageLogging(this);
            if (isDebug) {
                initWindow();
            }
            //获取键值对的迭代器
            for (Map.Entry<Class<?>, IConfig> next : config.getConfigMap().entrySet()) {
                IConfig value = next.getValue();
                if (value instanceof AnrConfig) {
                    AnrConfig anrConfig = (AnrConfig) value;
                    if (anrConfig.isOpen()) {
                        AnrMonitor anrMonitor = new AnrMonitor(mContext, isDebug, anrConfig);
                        monitorMap.put(AnrMonitor.class, anrMonitor);
                        anrMonitor.setAnrFileManager(new FileManager(new Gson()));
                        anrMonitor.setAnrListener(anrListener);
                        anrMonitor.setExecutorService(executorService);
                        anrMonitor.addAnrHandler(isDebug ? AnrDisplayService.class : AnrUploadService.class);
                        NativeLib.getInstance().anrMonitor(anrMonitor);//注册anr监听
                        anrMonitor.startMonitor();
                    }
                }
                //else ....
            }
            alreadyBegun = true;
        }
    }

}
