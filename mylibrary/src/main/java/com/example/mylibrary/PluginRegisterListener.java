package com.example.mylibrary;

public interface PluginRegisterListener {
    void onSuccess();

    void onFail(String pluginName);
}
