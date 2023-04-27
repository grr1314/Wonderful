package com.example.mylibrary;

import java.util.HashMap;

/**
 * 为组件提供注册和获取服务的能力
 */
public class ModuleServiceCenter implements IModuleServiceCenter {
    private final HashMap<String, IModuleService> serviceMap = new HashMap<>();

    @Override
    public void addService(String name, IModuleService iModuleService) {
        serviceMap.put(name, iModuleService);
    }

    @Override
    public IModuleService getService(String name) {
        return serviceMap.get(name);
    }
}
