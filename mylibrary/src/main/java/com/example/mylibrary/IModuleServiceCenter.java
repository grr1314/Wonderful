package com.example.mylibrary;

public interface IModuleServiceCenter {
    void addService(String name,IModuleService iModuleService);

    IModuleService getService(String name);
}
