package com.lc.nativelib;

import android.content.Context;
import android.os.Looper;
import android.util.Printer;

import com.google.gson.Gson;
import com.lc.nativelib.configs.AnrConfig;
import com.lc.nativelib.configs.Config;
import com.lc.nativelib.configs.UiConfig;
import com.lc.nativelib.display.MonitorDisplayActivity;
import com.lc.nativelib.file.FileManager;
import com.lc.nativelib.listener.AnrListener;
import com.lc.nativelib.listener.IConfig;
import com.lc.nativelib.listener.IMonitor;
import com.lc.nativelib.listener.MessageListener;
import com.lc.nativelib.listener.UiMonitorListener;
import com.lc.nativelib.monitors.AnrMonitor;
import com.lc.nativelib.monitors.UiRenderMonitor;
import com.lc.nativelib.service.AnrDisplayService;
import com.lc.nativelib.service.AnrUploadService;
import com.lc.nativelib.window.FloatClient;
import com.lc.nativelib.window.FloatHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AppMonitor implements Printer, IMonitor {
    private boolean isDebug = true;
    private boolean alreadyBegun = false;
    private Config config;
    private AnrListener anrListener;
    private UiMonitorListener uiMonitorListener;
    private Context mContext;
    private final Map<Class<?>, IMonitor> monitorMap = new HashMap<>();
    private final ArrayList<MessageListener> listeners = new ArrayList<>();
    private FloatHelper floatHelper;
    private static final int DEFAULT_THREAD_COUNT = 10;
    private ExecutorService executorService;

    private AppMonitor() {
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
    }

    public Map<Class<?>, IMonitor> getMonitorMap() {
        return monitorMap;
    }

    //调用println  默认false 是start  true 是end
    private final AtomicBoolean odd = new AtomicBoolean(false);

    @Override
    public void startMonitor() {
        start();
    }

    /**
     * 消息打印
     * 由于每一个消息都会打印一个开始和一个结束，因此主线程调度一个消息就会调用两次println函数，且传入的数据x也不一样
     *
     * @param x
     */
    @Override
    public void println(String x) {
        if (x.contains("<<<<< Finished to") && !odd.get() || !monitorState()) {
            //由于这个库本身可能就是在某个Message的分发周期中启动的，所以会先收到依稀Finished
            return;
        }
        if (!odd.get()) {
            //>>>>> Dispatching to Handler (android.view.inputmethod.InputMethodManager$H) {1edcba4} null: 4
            dispatchMessageStart(x);
        } else {
            //<<<<< Finished to Handler (android.view.inputmethod.InputMethodManager$H) {1edcba4} null
            dispatchMessageEnd();
        }
        odd.set(!odd.get());
    }

    public void dispatchMessageStart(String x) {
        //分发到每一个monitor
        for (MessageListener listener : listeners) {
            if (listener.monitorState()) {
                listener.dispatchMessageStart(x);
            }
        }
    }

    public void dispatchMessageEnd() {
        //分发到每一个monitor
        for (MessageListener listener : listeners) {
            if (listener.monitorState()) {
                listener.dispatchMessageEnd();
            }
        }
    }

    @Override
    public boolean monitorState() {
        return alreadyBegun;
    }

    @Override
    public void stopMonitor() {
        stop();
    }


//    @Override
//    public void println(String x) {
//        //分发到每一个monitor
//        for (Map.Entry<Class<?>, IMonitor> next : monitorMap.entrySet()) {
//            if (next.getValue().monitorState()) {
//                next.getValue().println(x);
//            }
//        }
//    }

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
        private UiMonitorListener uiMonitorListener;
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

        public Builder addUiListener(UiMonitorListener uiMonitorListener) {
            this.uiMonitorListener = uiMonitorListener;
            return this;
        }

        public AppMonitor build() {
            AppMonitor appMonitor = AppMonitor.get();
            appMonitor.isDebug = this.isDebug;
            appMonitor.config = this.config;
            appMonitor.anrListener = this.anrListener;
            appMonitor.mContext = this.mContext;
            appMonitor.uiMonitorListener = this.uiMonitorListener;
            return appMonitor;
        }

        public Builder with(Context context) {
            if (context.getApplicationContext() == null) {
                this.mContext = context;
            } else this.mContext = context.getApplicationContext();
            return this;
        }
    }

    private void stop() {
        Looper.getMainLooper().setMessageLogging(null);
        for (Map.Entry<Class<?>, IMonitor> next : monitorMap.entrySet()) {
            IMonitor current = next.getValue();
            if (current.monitorState()) {
                current.stopMonitor();
            }
        }
        alreadyBegun = false;
    }

    private void initWindow() {
        if (floatHelper == null) {
            //定义悬浮窗助手
            floatHelper = new FloatClient.Builder().with(mContext)
//                .addView(LayoutInflater.from(mContext).inflate(R.layout.window_view, null, false))
                    //是否需要展示默认权限提示弹窗，建议使用自己的项目中弹窗样式（默认开启）
                    .enableDefaultPermissionDialog(false).setClickTarget(MonitorDisplayActivity.class).addPermissionCallback(b -> {

                    }).build();
        }
        floatHelper.show();
    }

    private void start() {
        if (!monitorState()) {
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
                        listeners.add(anrMonitor);//需要监听主线程消息的monitor就需要添加
                        anrMonitor.setAnrFileManager(new FileManager(new Gson()));//添加一个文件管理器
                        anrMonitor.setAnrListener(anrListener);
                        anrMonitor.setExecutorService(executorService);
//                        anrMonitor.addAnrHandler(isDebug ? AnrDisplayService.class : AnrUploadService.class);
                        NativeLib.getInstance().anrMonitor(anrMonitor);//注册anr监听
                        anrMonitor.startMonitor();
                    }
                } else if (value instanceof UiConfig) {
                    UiConfig uiConfig = (UiConfig) value;
                    if (uiConfig.isOpen()) {
                        UiRenderMonitor uiRenderMonitor = new UiRenderMonitor(mContext, isDebug, uiConfig);
                        monitorMap.put(UiRenderMonitor.class, uiRenderMonitor);
                        listeners.add(uiRenderMonitor);
                        uiRenderMonitor.setListener(uiMonitorListener);
                        uiRenderMonitor.startMonitor();
                    }
                }
                //else ....
            }
            alreadyBegun = true;
        }
    }

}
