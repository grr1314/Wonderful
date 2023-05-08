package com.example.center;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.mylibrary.IModulePlugin;
import com.example.mylibrary.ModuleInfoTable;
import com.example.mylibrary.ModuleServiceCenter;
import com.example.mylibrary.ModuleServiceCenterProvider;
import com.example.mylibrary.PluginRegisterListener;

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
public class CenterPlugin implements IModulePlugin {
    private final List<String> moduleList = new ArrayList<>();
    private ConcurrentHashMap<String, IModulePlugin> iModulePluginConcurrentHashMap;
    private ModuleServiceCenter moduleServiceCenter;
    private ExecutorService singleThreadExecutor;
    private Context mContext;

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
        if (moduleServiceCenter == null)
            moduleServiceCenter = new ModuleServiceCenter();
        ModuleServiceCenterProvider.getInstance().setModuleServiceCenter(moduleServiceCenter);
    }

    @Override
    public void startLoadModulePlugin(List<String> modules, PluginRegisterListener pluginRegisterListener) {
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
        AtomicBoolean allSuccess = new AtomicBoolean(true);
        singleThreadExecutor = getSingleThreadExecutor();
        singleThreadExecutor.execute(() -> {
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
                        }

                        @Override
                        public void onFail(String pluginName) {
                            allSuccess.set(false);
                            pluginRegisterListener.onFail(pluginName);
                        }
                    });
                    iModulePluginConcurrentHashMap.put(entry.getKey(), iModulePlugin);
                }
            }
            if (allSuccess.get()) {
                pluginRegisterListener.onSuccess();
            }
        });
    }

    private ExecutorService getSingleThreadExecutor() {
        if (singleThreadExecutor == null)
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        return singleThreadExecutor;
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

    private static class CenterPluginHolder {
        @SuppressLint("StaticFieldLeak")
        private static final CenterPlugin plugin = new CenterPlugin();
    }

}
