package com.example.mylibrary;

import android.content.Context;

import java.util.List;

public interface IModulePlugin {
    void init(Context context, String pluginName, PluginRegisterListener pluginRegisterListener);
    void startLoadModulePlugin(List<String> modules,PluginRegisterListener pluginRegisterListener);
}
