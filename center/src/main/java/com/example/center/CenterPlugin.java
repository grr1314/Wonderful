package com.example.center;

import static com.example.mylibrary.ModuleInfoTable.MODULE_CENTER;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mylibrary.IModulePlugin;
import com.example.mylibrary.ModuleInfoTable;
import com.example.mylibrary.ModuleServiceCenter;
import com.example.mylibrary.ModuleServiceCenterProvider;
import com.example.mylibrary.PluginRegisterListener;
import com.example.mylibrary.service.CenterModuleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 1 注册其他的组件
 * 2 提供注册服务和查询服务的功能
 */
public class CenterPlugin implements IModulePlugin, CenterModuleService {
    private static int PLUGIN_STATE_INIT = 0;
    private static int PLUGIN_STATE_STARTED = 1;
    private static int PLUGIN_STATE_STOPED = 2;
    private static int pluginState = PLUGIN_STATE_INIT;
    private final List<String> moduleList = new ArrayList<>();
    private ConcurrentHashMap<String, IModulePlugin> iModulePluginConcurrentHashMap;
    private ModuleServiceCenter moduleServiceCenter;
    private static final int DEFAULT_THREAD_COUNT = 10;
    private ExecutorService executorService;
    private Context mContext;
    int showActivityCount = 0;
    int allActivityCount = 0;

    private final Object mLock = new Object();

    AtomicBoolean allSuccess = new AtomicBoolean(true);

    private CenterPlugin() {
    }

    public static CenterPlugin getInstance() {
        return CenterPluginHolder.plugin;
    }


    public ModuleServiceCenter getModuleServiceCenter() {
        return moduleServiceCenter;
    }

    public void init(Context context) {
        init(context, "", null);
    }

    @Override
    public void init(Context context, String pluginName, PluginRegisterListener pluginRegisterListener) {
        mContext = context;
        if (moduleServiceCenter == null) moduleServiceCenter = new ModuleServiceCenter();
        ModuleServiceCenterProvider.getInstance().setModuleServiceCenter(moduleServiceCenter);
        //注册自己的服务
        moduleServiceCenter.addService(MODULE_CENTER, this);
        context = context.getApplicationContext();
        if (context instanceof Application) {
            Application application = (Application) context;


            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                    allActivityCount++;
                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    if (++showActivityCount == 1) {
                        if (pluginState < PLUGIN_STATE_STARTED) {
                            synchronized (mLock) {
                                onResume();
                            }
                        }
                    }
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {

                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {

                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                    if (--showActivityCount == 0) {
                        onStop();
                    }
                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {
                    if (--allActivityCount == 0) {
                        onDestroy();
                    }
                }
            });
        }
    }

    @Override
    public void startLoadModulePlugin(List<String> modules, PluginRegisterListener pluginRegisterListener, boolean async) {
        Log.e("initPlugins", "startLoadModulePlugin");
        moduleList.clear();
        if (modules == null || modules.isEmpty()) {
            modules = ModuleInfoTable.DEFAULT_MODULE_LIST;
        }
        moduleList.addAll(modules);
        if (iModulePluginConcurrentHashMap == null) {
            iModulePluginConcurrentHashMap = new ConcurrentHashMap<>();
        }
        //调用自己的init函数，初始化
        init(mContext, "", null);
        if (async) {
            executorService = getThreadExecutor();
            executorService.execute(() -> {
                initChildPlugin(pluginRegisterListener);
            });
        } else {
            initChildPlugin(pluginRegisterListener);
        }
    }

    private void initChildPlugin(PluginRegisterListener pluginRegisterListener) {
        for (Map.Entry<String, String> entry : ModuleInfoTable.map.entrySet()) {
            IModulePlugin iModulePlugin;
            if (iModulePluginConcurrentHashMap.containsKey(entry.getKey()) && iModulePluginConcurrentHashMap.get(entry.getKey()) != null) {
                iModulePlugin = iModulePluginConcurrentHashMap.get(entry.getKey());
            } else {
                //反射获取对象
                iModulePlugin = findClass(entry.getValue());
            }
            if (iModulePlugin == null) {
                allSuccess.set(false);
                iModulePluginConcurrentHashMap.remove(entry.getKey());
                pluginRegisterListener.onFail(entry.getKey());
            } else {
                iModulePlugin.init(mContext, entry.getKey(), new PluginRegisterListener() {
                    @Override
                    public void onSuccess() {
                        //更新注册表，以便以后查询
                        iModulePluginConcurrentHashMap.put(entry.getKey(), iModulePlugin);
                    }

                    @Override
                    public void onFail(String pluginName) {
                        allSuccess.set(false);
                        pluginRegisterListener.onFail(pluginName);
                    }
                });
            }
        }
        if (allSuccess.get()) {
            pluginRegisterListener.onSuccess();
            if (pluginState < PLUGIN_STATE_STARTED) {
                synchronized (mLock) {
                    onResume();
                }
            }
        }
    }

    @Override
    public void onResume() {
        //查询已经注册成功的组件，分别调用onResume函数
        for (Map.Entry<String, IModulePlugin> entry : iModulePluginConcurrentHashMap.entrySet()) {
            entry.getValue().onResume();
        }
        pluginState = PLUGIN_STATE_STARTED;
    }

    @Override
    public void onStop() {
        new Handler(Looper.myLooper()).post(() -> {
            for (Map.Entry<String, IModulePlugin> entry : iModulePluginConcurrentHashMap.entrySet()) {
                entry.getValue().onStop();
            }
        });
        pluginState = PLUGIN_STATE_STOPED;
    }

    @Override
    public void onDestroy() {
        new Handler(Looper.myLooper()).post(() -> {
            for (Map.Entry<String, IModulePlugin> entry : iModulePluginConcurrentHashMap.entrySet()) {
                entry.getValue().onDestroy();
            }
        });
        pluginState = PLUGIN_STATE_INIT;
    }

    private ExecutorService getThreadExecutor() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        }
        return executorService;
    }

    private IModulePlugin findClass(String value) {
        try {
            Class<?> clz = Class.forName(value);
            return (IModulePlugin) clz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public ExecutorService globalExecutorService() {
        return getThreadExecutor();
    }

    private static class CenterPluginHolder {
        @SuppressLint("StaticFieldLeak")
        private static final CenterPlugin plugin = new CenterPlugin();
    }

}
